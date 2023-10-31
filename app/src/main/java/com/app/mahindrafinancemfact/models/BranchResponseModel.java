package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BranchResponseModel {
    @SerializedName("data")
    public ArrayList<BranchModel> branchresponse = new ArrayList();
}
