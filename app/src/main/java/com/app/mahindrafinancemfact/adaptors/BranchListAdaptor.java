package com.app.mahindrafinancemfact.adaptors;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mahindrafinancemfact.activities.AuditDetailsScreenActivity;
import com.app.mahindrafinancemfact.databinding.BranchListItemBinding;
import com.app.mahindrafinancemfact.models.BranchModel;

import java.util.ArrayList;
import java.util.List;

public class BranchListAdaptor extends RecyclerView.Adapter<BranchListAdaptor.branchViewHolder>{
    List<BranchModel> branchList = new ArrayList<>();
    Context context;

    private RecyclerView.ViewHolder holder;
    public int position;
    String aid;
    String branch;

    private BranchListItemBinding branchListItemBinding;

    public BranchListAdaptor(Context context, ArrayList<BranchModel> branchList) {
        this.branchList = branchList;
        this.context = context;
     }



    @NonNull
    @Override
    public BranchListAdaptor.branchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        branchListItemBinding = BranchListItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new BranchListAdaptor.branchViewHolder(branchListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchListAdaptor.branchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        this.holder = holder;
        this.position = position;
        BranchModel branchModel = branchList.get(position);

        holder.branchListItemBinding.tvBranchName.setText(branchList.get(position).branch);


        setOnClickListener (holder, branchModel, position);

    }

    private void setOnClickListener(branchViewHolder holder, BranchModel branchModel, int position) {
        holder.branchListItemBinding.llMain.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent (context, AuditDetailsScreenActivity.class);
                aid = branchList.get(position).aid;
                branch = branchList.get(position).branch;
                SharedPreferences sharedPreferences = context.getSharedPreferences("AID", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("aid", aid);
                myEdit.apply();
                intent.putExtra ("aid", aid);
                intent.putExtra("branch",branch);
                context.startActivity (intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return branchList.size ();
    }
    public static class branchViewHolder extends RecyclerView.ViewHolder {

        private BranchListItemBinding branchListItemBinding;


        branchViewHolder (BranchListItemBinding branchListItemBinding) {
            super (branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;


        }
    }
}
