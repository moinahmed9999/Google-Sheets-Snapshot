package com.moin.sheetssnapshot.api;

import com.moin.sheetssnapshot.model.SnapshotResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SnapshotService {

    @GET("/snapshot")
    Call<SnapshotResponse> getSnapshots();
}
