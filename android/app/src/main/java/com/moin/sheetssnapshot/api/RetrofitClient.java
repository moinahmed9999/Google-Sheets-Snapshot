package com.moin.sheetssnapshot.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "https://sheets-snapshot.herokuapp.com";

    private static RetrofitClient instance = null;
    private final SnapshotService snapshotService;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        snapshotService = retrofit.create(SnapshotService.class);
    }

    public SnapshotService getSnapshotService() {
        return snapshotService;
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }

        return instance;
    }
}
