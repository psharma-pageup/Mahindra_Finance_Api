package com.example.mahindrafinanceapi.adaptors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.activities.AuditDetailsScreenActivity;
import com.example.mahindrafinanceapi.databinding.BranchListItemBinding;
import com.example.mahindrafinanceapi.models.AssetModel;
import com.example.mahindrafinanceapi.models.AssetObjectModel;
import com.example.mahindrafinanceapi.models.BranchModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssetListAdaptor extends RecyclerView.Adapter<AssetListAdaptor.assetViewHolder>{
    ArrayList assetList = new ArrayList<>();
    Activity activity;
    Context context;
    public boolean menu = true;
    private RecyclerView.ViewHolder holder;
    public int position;
    private BranchListItemBinding branchListItemBinding;

    public AssetListAdaptor(Context context, ArrayList assetList) {
        this.assetList = assetList;
        this.context = context;
    }


    public void AssetListAdaptor (Activity activity, ArrayList assetList) {
        this.assetList = assetList;
        this.activity = activity;
    }
    @NonNull
    @Override
    public AssetListAdaptor.assetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = null;
//        view = LayoutInflater.from (parent.getContext ())
//                .inflate (R.layout.branch_list_item, parent, false);
//        assetViewHolder professionViewHolder = new assetViewHolder (view);
//        return professionViewHolder;
        branchListItemBinding = BranchListItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new AssetListAdaptor.assetViewHolder(branchListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetListAdaptor.assetViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.holder = holder;
        this.position = position;
//        AssetModel assetModel = assetList.get (position);
//        AssetObjectModel assetObjectModel = assetList.get(position);
        //     holder.tvBranchName.setText (branchModel.getBranch_name ());
     //   setOnClickListener (holder, assetModel, position);



        holder.branchListItemBinding.tvBranchName.setText(assetList.get(position).toString());
    }


//    private void setOnClickListener(assetViewHolder holder, AssetModel assetModel, int position) {
//        holder.branchListItemBinding.llMain.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick (View v) {
//                Intent intent = new Intent (activity, AuditDetailsScreenActivity.class);
//                intent.putExtra ("BRANCH_DATA", (Serializable) assetModel);
//                activity.startActivity (intent);
//                activity.finish ();
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return assetList.size ();
    }
    public static class assetViewHolder extends RecyclerView.ViewHolder {

        private BranchListItemBinding branchListItemBinding;



        public assetViewHolder(@NonNull BranchListItemBinding branchListItemBinding) {
            super(branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;
        }
    }
}
