package com.moin.sheetssnapshot.model;

import com.google.gson.annotations.SerializedName;

public class Snapshot {

    @SerializedName("_id")
    private String id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("date")
    private String date;

    public Snapshot(String id, String imageUrl, String date) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }
}
