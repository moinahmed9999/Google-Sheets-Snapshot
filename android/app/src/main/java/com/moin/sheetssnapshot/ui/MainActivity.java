package com.moin.sheetssnapshot.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.moin.sheetssnapshot.ConnectionLiveData;
import com.moin.sheetssnapshot.adapter.SnapshotAdapter;
import com.moin.sheetssnapshot.api.RetrofitClient;
import com.moin.sheetssnapshot.api.SnapshotService;
import com.moin.sheetssnapshot.databinding.ActivityMainBinding;
import com.moin.sheetssnapshot.model.Snapshot;
import com.moin.sheetssnapshot.model.SnapshotResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String EVENT_NEW_SNAPSHOT = "new snapshot";

    private ActivityMainBinding binding;
    private SnapshotAdapter adapter;

    private final List<Snapshot> snapshots = new ArrayList<>();

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupSwipeRefresh();
        binding.swipeRefresh.setRefreshing(true);
        setupSocket();
        checkInternetAndGetSnapshots();
    }

    private void checkInternetAndGetSnapshots() {
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, isConnected -> {
            if (isConnected != null) {
                if (isConnected) {
                    showLog("Online!");
//                    showToast("Online!");
                    getSnapshots();
                } else {
                    showLog("Offline!");
//                    showToast("Offline!");
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.rvSnapshots.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        // TODO find correct value
        linearLayoutManager.setStackFromEnd(false);
        binding.rvSnapshots.setLayoutManager(linearLayoutManager);

        adapter = new SnapshotAdapter(snapshots, this, this::gotoFullScreenActivity);
        binding.rvSnapshots.setAdapter(adapter);
    }

    private void gotoFullScreenActivity(String url) {
        try {
            Intent intent = new Intent(this, FullScreenActivity.class);
            intent.putExtra("EXTRA_URL", url);
            startActivity(intent);
        } catch (Error e) {
            showToast(e.getMessage());
        }
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::getSnapshots);
    }

    private void setupSocket() {
        try {
            socket = IO.socket(RetrofitClient.BASE_URL);

            socket.on(Socket.EVENT_CONNECT, this::onConnect);
            socket.on(Socket.EVENT_DISCONNECT, this::onDisconnect);
            socket.on(EVENT_NEW_SNAPSHOT, this::onNewSnapshot);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void connectSocket() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    private void onConnect(Object... args) {
        showLog("Socket connected");
    }

    private void onDisconnect(Object... args) {
        showLog("Socket disconnected");
    }

    private void onNewSnapshot(Object... args) {
        showLog("New Snapshot is here");

        JSONObject data = (JSONObject) args[0];
        showLog(data.toString());

        addNewSnapshot(data);
    }

    private void addNewSnapshot(JSONObject data) {
        try {
            String id = data.getString("_id");
            String imageUrl = data.getString("imageUrl");
            String date = data.getString("date");

            Snapshot snapshot = new Snapshot(id, imageUrl, date);

            snapshots.add(0, snapshot);
            adapter.notifyItemInserted(0);
            binding.rvSnapshots.smoothScrollToPosition(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSnapshots() {
        SnapshotService snapshotService = RetrofitClient.getInstance().getSnapshotService();
        Call<SnapshotResponse> call = snapshotService.getSnapshots();

        call.enqueue(new Callback<SnapshotResponse>() {
            @Override
            public void onResponse(@NonNull Call<SnapshotResponse> call, @NonNull Response<SnapshotResponse> response) {
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getSnapshots() != null) {

                    Log.d(TAG, response.body().toString());

                    showSnapshots(response.body().getSnapshots());

                    connectSocket();
                } else {
                    showToast("Something went wrong !");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SnapshotResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                showToast(t.getMessage());
            }
        });
    }

    private void showSnapshots(List<Snapshot> snapshots) {
        this.snapshots.clear();
        this.snapshots.addAll(snapshots);

        adapter.notifyDataSetChanged();
        binding.rvSnapshots.smoothScrollToPosition(0);

        if (this.snapshots.isEmpty()) {
            showToast("Nothing to show here!");
            showLog("Nothing to show here!");
        } else {
            showLog("Snapshots here " + this.snapshots.size());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }
}