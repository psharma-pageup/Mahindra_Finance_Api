package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileModal implements Serializable {
    @SerializedName("name")
    public String name = "";

    @SerializedName("empCode")
    public String empCode;
}
