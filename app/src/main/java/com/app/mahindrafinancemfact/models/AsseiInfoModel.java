package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AsseiInfoModel implements Serializable {
    @SerializedName("sscat")
    public String sscat ;


    @SerializedName("serialNumber")
    public String serialNumber ;

    @SerializedName("aOwner")
    public String aOwner ;

    @SerializedName("details")
    public String details ;

    @SerializedName("bcode")
    public String bcode ;
}
