package com.moin.sheetssnapshot.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SnapshotResponse {

    @SerializedName("snapshots")
    private List<Snapshot> snapshots;

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }
}
