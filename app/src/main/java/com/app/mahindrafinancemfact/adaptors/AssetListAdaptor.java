package com.app.mahindrafinancemfact.adaptors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.mahindrafinancemfact.databinding.AssetListItemBinding;

import java.util.ArrayList;

public class AssetListAdaptor extends RecyclerView.Adapter<AssetListAdaptor.assetViewHolder>{
    ArrayList assetList = new ArrayList<>();
    Context context;

    private RecyclerView.ViewHolder holder;
    public int position;
    private AssetListItemBinding branchListItemBinding;

    public AssetListAdaptor(Context context, ArrayList assetList) {
        this.assetList = assetList;
        this.context = context;
    }



    @NonNull
    @Override
    public AssetListAdaptor.assetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        branchListItemBinding = AssetListItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new AssetListAdaptor.assetViewHolder(branchListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetListAdaptor.assetViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.holder = holder;
        this.position = position;

        holder.branchListItemBinding.tvBranchName.setText(assetList.get(position).toString());
    }



    @Override
    public int getItemCount() {
        return assetList.size ();
    }
    public static class assetViewHolder extends RecyclerView.ViewHolder {

        private AssetListItemBinding branchListItemBinding;



        public assetViewHolder(@NonNull AssetListItemBinding branchListItemBinding) {
            super(branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;
        }
    }
}
