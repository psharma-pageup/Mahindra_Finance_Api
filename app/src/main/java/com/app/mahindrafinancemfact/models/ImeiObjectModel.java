package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImeiObjectModel implements Serializable {
    @SerializedName("data")
    public int data;

    @SerializedName("token")
    public String token;
}
