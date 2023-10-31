package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GenericResponseModel<T> {
    @SerializedName("status")
    public boolean status = false;

    @SerializedName ("message")
    public String msg = "";

    public GenericResponseModel() {

    }

    @SerializedName ("data")
    public ArrayList<T> data=new ArrayList<>();

}
