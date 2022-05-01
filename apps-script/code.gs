function onInstall(e) {
  onOpen(e);
}

function onOpen(e) {
  var ui = SpreadsheetApp.getUi();
  
  ui.createMenu('Snapshot')
      .addItem('Take snapshot', 'takeSnapshot')
      .addToUi();
}

function takeSnapshot() {
  var sheet = SpreadsheetApp.getActiveSheet();

  // Method 1
  var data = sheet.getDataRange().getDisplayValues();
  const [header, ...values] = data;

  var dataTable = Charts.newDataTable();
  header.forEach((e) => dataTable.addColumn(Charts.ColumnType.STRING, e));
  values.forEach((e) => dataTable.addRow(e));

  var chart = Charts.newTableChart()
      .setDataTable(dataTable)
      .setDimensions(1000, 1000)
      .build();

  // Method 2
  // var chart = sheet.newChart()
  //     .setChartType(Charts.ChartType.TABLE)
  //     .addRange(sheet.getDataRange())
  //     .setPosition(10, 10, 0, 0)
  //     .build();

  // Get Chart as PNG Blob
  var chartImage = chart.getAs('image/png');

  // Upload image to our server
  var url = 'https://sheets-snapshot.herokuapp.com/snapshot';

  var payload = {
    image: chartImage
  };

  var options = {
    method: "post",
    payload: payload,
    muteHttpExceptions: true
  };

  var response = UrlFetchApp.fetch(url, options);

  Logger.log(response);
}
