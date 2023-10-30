package com.example.mahindrafinanceapi.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImeiModel implements Serializable {
    @SerializedName("imeI1")
    public String imeI1;

    @SerializedName("imeI2")
    public String imeI2;

    @SerializedName("data")
    public int data;
}
