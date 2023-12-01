package com.app.mahindrafinancemfact.activities;

import static android.R.layout.simple_spinner_item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityTransferFormBinding;
import com.app.mahindrafinancemfact.models.AssetBranchListModel;
import com.app.mahindrafinancemfact.models.AssetBranchListResponseModel;
import com.app.mahindrafinancemfact.models.AssetinforesponseModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class TransferFormActivity extends AppCompatActivity {
    private ActivityTransferFormBinding activityTransferFormBinding;
    String selectedValue;
    String aid;
    Context context;
    ApiInterface apiInterface;
    ArrayList<AssetBranchListModel> entries;
    String selectedValue3;
    List<CharSequence> drop = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTransferFormBinding = ActivityTransferFormBinding.inflate(getLayoutInflater());
        setContentView(activityTransferFormBinding.getRoot());
        context = this;
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        spinnerasset();
        SharedPreferences stat = getSharedPreferences("BRANCHLOC", MODE_PRIVATE);
        String status = stat.getString("loc", "");
        activityTransferFormBinding.stat.setText(status);
        destination();
        clickListener();
    }

    public void clickListener(){
        activityTransferFormBinding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination();
            }
        });
    }

    private void destination() {
        SharedPreferences ai = getSharedPreferences("AID",MODE_PRIVATE);
        aid = ai.getString("aid","");
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE,View.GONE);
                String params = aid;
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetBranchListResponseModel> call = apiInterface.assetbranch(params,"Bearer " + token);
                call.enqueue(new Callback<AssetBranchListResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<AssetBranchListResponseModel> call, @NonNull retrofit2.Response<AssetBranchListResponseModel> response) {
                        AssetBranchListResponseModel responseType = response.body();
                        if (responseType != null) {
                            for(int i = 0; i< responseType.assetbranch.size();i++) {
                                entries = responseType.assetbranch;
                                drop.addAll(Collections.singleton(entries.get(i).branch));
                            }
                            ArrayAdapter adapter = new ArrayAdapter(context, simple_spinner_item, drop);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            activityTransferFormBinding.spinner3.setAdapter(adapter);
                            activityTransferFormBinding.spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    selectedValue3 = (String) parentView.getItemAtPosition(position);
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }
                            });
                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AssetBranchListResponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE,View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spinnerasset() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status,
                simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityTransferFormBinding.spinnerasset.setAdapter(adapter);
        activityTransferFormBinding.spinnerasset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedValue = (String) parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    void showMsgView(int containerVisibility,int srvr) {
        try {
            activityTransferFormBinding.llinternet.setVisibility(containerVisibility);
            activityTransferFormBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        setSupportActionBar(activityTransferFormBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activityTransferFormBinding.toolbar.setNavigationIcon(R.drawable.back_button);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}