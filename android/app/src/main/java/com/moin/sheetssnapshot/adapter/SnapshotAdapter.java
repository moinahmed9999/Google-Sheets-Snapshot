package com.moin.sheetssnapshot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moin.sheetssnapshot.databinding.LayoutSnapshotBinding;
import com.moin.sheetssnapshot.model.Snapshot;

import java.util.List;

public class SnapshotAdapter extends RecyclerView.Adapter<SnapshotViewHolder> {

    private final List<Snapshot> snapshots;
    private final Context context;
    private final OnClickListener onClickListener;

    public SnapshotAdapter(List<Snapshot> snapshots, Context context, OnClickListener onClickListener) {
        this.snapshots = snapshots;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SnapshotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutSnapshotBinding binding = LayoutSnapshotBinding
                .inflate(layoutInflater, parent, false);

        return new SnapshotViewHolder(binding, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SnapshotViewHolder holder, int position) {
        holder.bind(snapshots.get(position), context);
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }
}
