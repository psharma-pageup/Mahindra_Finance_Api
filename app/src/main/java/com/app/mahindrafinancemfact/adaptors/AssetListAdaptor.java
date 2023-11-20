package com.app.mahindrafinancemfact.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.AssetListItemBinding;
import com.app.mahindrafinancemfact.databinding.ItemLoadingBinding;


import java.util.ArrayList;

public class AssetListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList assetList;
    Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public boolean isLoading = false;

    public AssetListAdaptor(Context context, ArrayList assetList) {
        this.assetList = assetList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            AssetListItemBinding branchListItemBinding = AssetListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AssetViewHolder(branchListItemBinding);
        } else {
            ItemLoadingBinding itemLoadingBinding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(itemLoadingBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AssetViewHolder) {
            populateItemRows((AssetViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {
        holder.itemLoadingBinding.progressBar.setVisibility(View.VISIBLE);
    }

    private void populateItemRows(AssetViewHolder holder, int position) {

            TextView tvbranchName = holder.itemView.findViewById(R.id.tvBranchName);
            String item = assetList.get(position).toString();
            tvbranchName.setText(item);

    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return (position == assetList.size()- 1 && isLoading) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;




    }


    public static class AssetViewHolder extends RecyclerView.ViewHolder {
        private final AssetListItemBinding branchListItemBinding;
        TextView tvbranchName;

        public AssetViewHolder(@NonNull AssetListItemBinding branchListItemBinding) {
            super(branchListItemBinding.getRoot());
            this.branchListItemBinding = branchListItemBinding;
            tvbranchName = branchListItemBinding.tvBranchName;
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        private final ItemLoadingBinding itemLoadingBinding;

        public LoadingViewHolder(@NonNull ItemLoadingBinding itemLoadingBinding) {
            super(itemLoadingBinding.getRoot());
            this.itemLoadingBinding = itemLoadingBinding;
        }
    }

}
