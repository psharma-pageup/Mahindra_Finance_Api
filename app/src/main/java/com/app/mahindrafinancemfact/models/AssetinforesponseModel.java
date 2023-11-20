package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssetinforesponseModel implements Serializable {
    @SerializedName("data")
    public AsseiInfoModel data = new AsseiInfoModel();
}
