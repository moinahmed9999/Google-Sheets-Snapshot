package com.moin.sheetssnapshot.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moin.sheetssnapshot.databinding.ActivityFullScreenBinding;

public class FullScreenActivity extends AppCompatActivity {

    private ActivityFullScreenBinding binding;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        showSnapshot();
    }

    private void getIntentData() {
        Intent intent = getIntent();

        url = intent.getStringExtra("EXTRA_URL");

        if (url == null || url.isEmpty()) {
            showToast("Something went wrong !");
            finish();
        }
    }

    private void showSnapshot() {
        Glide.with(this).load(url).into(binding.photoView);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}