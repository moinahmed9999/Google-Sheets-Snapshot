package com.moin.sheetssnapshot.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moin.sheetssnapshot.databinding.LayoutSnapshotBinding;
import com.moin.sheetssnapshot.model.Snapshot;

public class SnapshotViewHolder extends RecyclerView.ViewHolder {

    private final LayoutSnapshotBinding binding;
    private final OnClickListener onCLickListener;

    public SnapshotViewHolder(@NonNull LayoutSnapshotBinding binding, OnClickListener onCLickListener) {
        super(binding.getRoot());

        this.binding = binding;
        this.onCLickListener = onCLickListener;
    }

    public void bind(Snapshot snapshot, Context context) {
        if (snapshot.getImageUrl() != null && !snapshot.getImageUrl().isEmpty()) {
            binding.tvDate.setText(snapshot.getDate());

            Glide.with(context)
                    .load(snapshot.getImageUrl())
                    .centerCrop()
                    .into(binding.ivSnapshot);

            binding.getRoot().setOnClickListener(view -> {
                if (onCLickListener != null) {
                    onCLickListener.onClick(snapshot.getImageUrl());
                }
            });
        }
    }
}
