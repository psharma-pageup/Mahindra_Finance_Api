package com.example.mahindrafinanceapi.models;

import com.google.gson.annotations.SerializedName;

public class QRResponseModel {
    @SerializedName("data")
    public QRResponseObjectModel data = new QRResponseObjectModel();

    @SerializedName("QRCode")
    public String QRCode;
}
