package com.app.mahindrafinancemfact.adaptors;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.activities.BranchListActivity;
import com.app.mahindrafinancemfact.activities.FormActivity;
import com.app.mahindrafinancemfact.activities.ProfileActivity;
import com.app.mahindrafinancemfact.databinding.AssetListItemBinding;
import com.app.mahindrafinancemfact.databinding.ItemLoadingBinding;
import com.app.mahindrafinancemfact.databinding.PendinglistitemBinding;

import java.util.ArrayList;

public class PendingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList assetList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public PendingListAdapter(Context context, ArrayList assetList) {
        this.assetList = assetList;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {

            com.app.mahindrafinancemfact.databinding.PendinglistitemBinding branchListItemBinding = PendinglistitemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new assetViewHolder(branchListItemBinding);
        }else{
            ItemLoadingBinding itemLoadingBinding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new loadingViewHolder(itemLoadingBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PendingListAdapter.assetViewHolder) {

            populateItemRows((PendingListAdapter.assetViewHolder) holder, position);
        } else if (holder instanceof PendingListAdapter.loadingViewHolder) {
            showLoadingView((PendingListAdapter.loadingViewHolder) holder, position);
        }

    }

    private void showLoadingView(loadingViewHolder holder, int position) {
    }

    private void populateItemRows(PendingListAdapter.assetViewHolder holder, int position) {
        TextView tvbranchName = holder.itemView.findViewById(R.id.tvBranchName);
        String item = assetList.get(position).toString();

        tvbranchName.setText(item);

        SharedPreferences sharedPreferences = context.getSharedPreferences("Status", MODE_PRIVATE);
        String status = sharedPreferences.getString("stat", "");

       tvbranchName.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String Hocode = assetList.get(position).toString();
               String[] parts = Hocode.split(" - ");

               String extracted = null;
               if (parts.length >= 1) {
                   extracted = parts[0];
               } else {
                   Toast.makeText(context, "No Delimiter", Toast.LENGTH_SHORT).show();
               }
               Intent intent = new Intent(context, FormActivity.class);
               intent.putExtra("hocode", extracted);
               context.startActivity(intent);
               ((Activity)context).finishAndRemoveTask();
           }
       });
    }

    @Override
    public int getItemCount() {
        return assetList.size ();
    }
    @Override
    public int getItemViewType(int position) {
        return assetList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public static class assetViewHolder extends RecyclerView.ViewHolder {
        private final PendinglistitemBinding branchListItemBinding;
        TextView tvbranchName;

        public assetViewHolder(@NonNull PendinglistitemBinding branchListItemBinding) {
            super(branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;
            tvbranchName = branchListItemBinding.tvBranchName;

        }

    }
    private class loadingViewHolder extends RecyclerView.ViewHolder {

        private final ItemLoadingBinding itemLoadingBinding;

        public loadingViewHolder(@NonNull ItemLoadingBinding itemLoadingBinding) {
            super(itemLoadingBinding.getRoot());
            this.itemLoadingBinding = itemLoadingBinding;
        }
    }
}
