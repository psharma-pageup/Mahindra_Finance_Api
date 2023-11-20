package com.app.mahindrafinancemfact.adaptors;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mahindrafinancemfact.activities.AssetListScreenActivity;
import com.app.mahindrafinancemfact.databinding.BranchListItemBinding;
import com.app.mahindrafinancemfact.models.BranchModel;

import java.util.ArrayList;
import java.util.List;

public class BranchListAdaptor extends RecyclerView.Adapter<BranchListAdaptor.branchViewHolder>{
    List<BranchModel> branchList;
    Context context;
    public int position;
    String aid;
    String branch;
    public BranchListAdaptor(Context context, ArrayList<BranchModel> branchList) {
        this.branchList = branchList;
        this.context = context;
     }
    @NonNull
    @Override
    public BranchListAdaptor.branchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        com.app.mahindrafinancemfact.databinding.BranchListItemBinding branchListItemBinding = BranchListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new BranchListAdaptor.branchViewHolder(branchListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchListAdaptor.branchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.position = position;
        holder.branchListItemBinding.tvBranchName.setText(branchList.get(position).branch);
        holder.branchListItemBinding.tvAuditId.setText(branchList.get(position).aid);
        setOnClickListener (holder, position);
    }
    private void setOnClickListener(branchViewHolder holder, int position) {
        holder.branchListItemBinding.llMain.setOnClickListener (v -> {
            Intent intent = new Intent (context, AssetListScreenActivity.class);
            aid = branchList.get(position).aid;
            branch = branchList.get(position).branch;
            SharedPreferences sharedPreferences = context.getSharedPreferences("AID", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("aid", aid);
            myEdit.apply();
            SharedPreferences sharedPref = context.getSharedPreferences("BRANCHLOC", MODE_PRIVATE);
            SharedPreferences.Editor Edit = sharedPref.edit();
            Edit.putString("loc", branch);
            Edit.apply();
            context.startActivity (intent);
        });
    }

    @Override
    public int getItemCount() {
        return branchList.size ();
    }
    public static class branchViewHolder extends RecyclerView.ViewHolder {
        private final BranchListItemBinding branchListItemBinding;
        branchViewHolder (BranchListItemBinding branchListItemBinding) {
            super (branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;
        }
    }
}
