package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegistrationModel implements Serializable {
    @SerializedName("sapCode")
    public String sapCode;

    @SerializedName("imeI1")
    public String imeI1;

    @SerializedName("imeI2")
    public String imeI2;

    @SerializedName("data")
    public int data;
}
