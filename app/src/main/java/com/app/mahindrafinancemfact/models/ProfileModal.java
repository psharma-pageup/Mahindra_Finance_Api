package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileModal implements Serializable {
    @SerializedName("name")
    public String name = "";

    @SerializedName("empCode")
    public String empCode;

    @SerializedName("branch")
    public String branch;

    @SerializedName("company")
    public String company;

    @SerializedName("dept")
    public String dept;

    @SerializedName("mail")
    public String mail;

    @SerializedName("mobile")
    public String mobile;
}
