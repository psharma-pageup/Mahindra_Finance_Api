package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImeiModel implements Serializable {


    @SerializedName("data")
    public ImeiObjectModel data = new ImeiObjectModel();
}
