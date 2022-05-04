const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const bodyParser = require('body-parser');
const multer  = require('multer');
const multerS3 = require('multer-s3');
const AWS = require('aws-sdk');

require("dotenv").config();

app.use(bodyParser.json());

const mongo = require("mongodb").MongoClient;
let db, snapshots;

const s3 = new AWS.S3({
    accessKeyId: process.env.AWS_ACCESS_KEY_ID,
    secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
    Bucket: process.env.AWS_BUCKET_NAME
});

const upload = multer({
    storage: multerS3({
        s3: s3,
        bucket: process.env.AWS_BUCKET_NAME,
        metadata: (req, file, cb) => {
            cb(null, { fieldName: file.fieldname });
        },
        key: (req, file, cb) => {
            cb(null, Date.now() + '_' + file.originalname);
        }
    })
}).single('image');

app.get('/', (req, res) => {
    res.send('Hello there!');
});

app.post('/snapshot', (req, res) => {
    upload(req, res, (error) => {
        if (error instanceof multer.MulterError) {
            res.status(400).json({ 
                message: 'Upload unsuccessful', 
                errorMessage: error.message,
                errorCode: error.code
            });
        }
        
        if (error) {
            res.status(500).json({
                message: 'Error occured',
                errorMessage: error.message
            });
        }

        const image = req.file;
        const imageUrl = image.location;

        console.log(image);

        const date = new Date().toUTCString();
        console.log(date);

        const object = {
            imageUrl: imageUrl,
            date: date
        };

        snapshots.insertOne(object, (err, result) => {
            if (err) {
                console.log(err);
                res.status(500).send({err: err});
                return;
            }

            console.log(result);
            console.log(object);
            res.status(200).json(object);

            io.emit('new snapshot', object);
        });
    });
});

app.get('/snapshot', (req, res) => {
    snapshots.find({}).sort({"_id": -1}).toArray((err, result) => {
        if(err) {
            res.status(500).send(error);
        }

        res.json({
            snapshots: result
        });
    });
});

io.on('connection', (socket) => {
    console.log('socket connection started');
    socket.on('disconnect', () => {
        console.log('socket connection ended');
    });
});

const PORT = process.env.PORT || 80;
server.listen(PORT, () => {
    console.log('Application listening on port ' + PORT);

    const url = process.env.MONGO_DB_CONNECTION_URL;

    mongo.connect(url, {
        useNewUrlParser: true,
        useUnifiedTopology: true}, (err, client) => {
        if (err) {
            console.log(err);
            return;
        }

        db = client.db("snapshot-db");
        snapshots = db.collection("snapshots");

        console.log('Connected to db!');
    });
});