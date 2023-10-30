package com.example.mahindrafinanceapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AssetObjectModel {
    @SerializedName("total")
    public int total;
    @SerializedName("completed")
    public int completed;
    @SerializedName("pending")
    public int pending;
    @SerializedName("assetList")
    public ArrayList assetlist = new ArrayList();
}
