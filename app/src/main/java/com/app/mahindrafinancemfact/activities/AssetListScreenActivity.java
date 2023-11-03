package com.app.mahindrafinancemfact.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityAssetListScreenBinding;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.app.mahindrafinancemfact.adaptors.AssetListAdaptor;
import com.app.mahindrafinancemfact.models.AssetObjectResponseModel;
import com.app.mahindrafinancemfact.utility.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class AssetListScreenActivity extends AppCompatActivity {
    private ActivityAssetListScreenBinding activityAssetListScreenBinding;
    ArrayList assetList = new ArrayList<>();
    ApiInterface apiInterface;

    Context context;
    String s1;
    String s2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAssetListScreenBinding = ActivityAssetListScreenBinding.inflate(getLayoutInflater());
        setContentView(activityAssetListScreenBinding.getRoot());
        context = this;
        SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
        s1 = sh.getString("empCode", "");
        SharedPreferences ai = getSharedPreferences("AID",MODE_PRIVATE);
        s2 = ai.getString("aid","");
        init();
    }
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        activityAssetListScreenBinding.tvAuditId.setText(s2);
        getAssetList();
        activityAssetListScreenBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityAssetListScreenBinding.btnAuditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =getSharedPreferences("SAPCODE",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                SharedPreferences pref =getSharedPreferences("AID",Context.MODE_PRIVATE);
                SharedPreferences.Editor edite = pref.edit();
                edite.clear();
                edite.apply();
                finishAffinity();

                Intent intent = new Intent(AssetListScreenActivity.this, SplashScreen.class);
                startActivity(intent);
            }
        });
    }
    private void setToolbar() {
        setSupportActionBar(activityAssetListScreenBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityAssetListScreenBinding.toolbar.setNavigationIcon(R.drawable.back_button);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAssetList() {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                activityAssetListScreenBinding.llLogo.setVisibility(View.VISIBLE);
                activityAssetListScreenBinding.llinternet.setVisibility(View.GONE);
                activityAssetListScreenBinding.load.setVisibility(View.VISIBLE);

                HashMap<String, String> params = new HashMap<>();
                params.put("empCode", s1);
                params.put("aid", s2);
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetObjectResponseModel> call = apiInterface.Asset_request(params,"Bearer " + token);
                call.enqueue(new Callback<AssetObjectResponseModel>() {
                    @Override
                    public void onResponse(Call<AssetObjectResponseModel> call, retrofit2.Response<AssetObjectResponseModel> response) {
                        AssetObjectResponseModel responseType = response.body();
                        activityAssetListScreenBinding.load.setVisibility(View.GONE);

                        if (responseType != null) {

                            assetList = responseType.data.assetlist;
                            activityAssetListScreenBinding.total.setText(String.valueOf(responseType.data.total));
                            activityAssetListScreenBinding.completed.setText(String.valueOf(responseType.data.completed));
                            activityAssetListScreenBinding.pending.setText(String.valueOf(responseType.data.pending));
                            AssetListAdaptor adapter = new AssetListAdaptor(context,assetList);
                            adapter.notifyItemInserted(assetList.size() - 1);
                            activityAssetListScreenBinding.rvAssetList.scrollToPosition(assetList.size() - 1);
                            activityAssetListScreenBinding.rvAssetList.setHasFixedSize(true);
                            activityAssetListScreenBinding.rvAssetList.setLayoutManager(new LinearLayoutManager(context));
                            activityAssetListScreenBinding.rvAssetList.setAdapter(adapter);
                        } else {
                            Toast.makeText(AssetListScreenActivity.this, getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AssetObjectResponseModel> call, Throwable t) {
                        Toast.makeText(AssetListScreenActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                activityAssetListScreenBinding.llLogo.setVisibility(View.GONE);
                activityAssetListScreenBinding.llinternet.setVisibility(View.VISIBLE);
                Toast.makeText(AssetListScreenActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}