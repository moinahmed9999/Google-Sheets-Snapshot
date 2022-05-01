package com.moin.sheetssnapshot.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.moin.sheetssnapshot.adapter.SnapshotAdapter;
import com.moin.sheetssnapshot.api.RetrofitClient;
import com.moin.sheetssnapshot.api.SnapshotService;
import com.moin.sheetssnapshot.databinding.ActivityMainBinding;
import com.moin.sheetssnapshot.model.Snapshot;
import com.moin.sheetssnapshot.model.SnapshotResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SnapshotAdapter adapter;

    private final List<Snapshot> snapshots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupSwipeRefresh();
        getSnapshots();
    }

    private void setupRecyclerView() {
        adapter = new SnapshotAdapter(snapshots, this, url -> {
            try {
                Intent intent = new Intent(this, FullScreenActivity.class);
                intent.putExtra("EXTRA_URL", url);
                startActivity(intent);
            } catch (Error e) {
                showToast(e.getMessage());
            }
        });

        binding.rvSnapshots.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::getSnapshots);
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

                    snapshots.clear();
                    snapshots.addAll(response.body().getSnapshots());

                    adapter.notifyDataSetChanged();

                    if (response.body().getSnapshots().isEmpty()) {
                        showToast("Nothing to show here!");
                    }
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}