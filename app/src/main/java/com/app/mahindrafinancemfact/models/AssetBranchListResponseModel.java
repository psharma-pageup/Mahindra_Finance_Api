package com.app.mahindrafinancemfact.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AssetBranchListResponseModel implements Serializable {
    @SerializedName("data")
    public ArrayList<AssetBranchListModel> assetbranch;
}
