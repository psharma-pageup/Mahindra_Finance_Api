package com.app.mahindrafinancemfact.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.adaptors.ViewPagerAdapter;
import com.app.mahindrafinancemfact.databinding.ActivityAssetListScreenBinding;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.app.mahindrafinancemfact.adaptors.AssetListAdaptor;
import com.app.mahindrafinancemfact.models.AssetObjectResponseModel;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;

public class AssetListScreenActivity extends AppCompatActivity {
    private ActivityAssetListScreenBinding activityAssetListScreenBinding;
    ApiInterface apiInterface;
    Context context;
    String s1;
    String s2;
    int pageIndex = 1;
    String completed;
    String pending;
    String total;
    int pageSize = 10;

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
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        activityAssetListScreenBinding.viewPager.setOffscreenPageLimit(4);
        activityAssetListScreenBinding.viewPager.setAdapter(pagerAdapter);
        activityAssetListScreenBinding.tabLayout.setupWithViewPager(activityAssetListScreenBinding.viewPager);

    }
    /** Initialize apiinterface*/
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        SharedPreferences sh = getSharedPreferences("BRANCHLOC", MODE_PRIVATE);
        String loc = sh.getString("loc", "");
        activityAssetListScreenBinding.tvAuditId.setText(s2);
        activityAssetListScreenBinding.tvloc.setText(loc);
        getAssetList(pageIndex,pageSize);
        activityAssetListScreenBinding.btnBack.setOnClickListener(v -> finish());

        activityAssetListScreenBinding.btnAuditDone.setOnClickListener(v -> {
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
        });
        activityAssetListScreenBinding.qr.setOnClickListener(v -> {
            Intent intent = new Intent(AssetListScreenActivity.this,ScanActivity.class);
            startActivity(intent);
        });
        activityAssetListScreenBinding.btnRetry.setOnClickListener(v -> getAssetList(pageIndex,pageSize));
    }
    /**setup Toolbar*/
    private void setToolbar() {
        setSupportActionBar(activityAssetListScreenBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

    /** this function passes sapcode and audit id as params and we receive asset list and the count of total,completed and pending assets in response*/
    private void getAssetList(int pageIndex, int pageSize) {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                HashMap<String, String> params = new HashMap<>();
                params.put("empCode", s1);
                params.put("aid", s2);
                params.put("index", String.valueOf(pageIndex));
                params.put("records", String.valueOf(pageSize));
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetObjectResponseModel> call = apiInterface.Asset_request(params,"Bearer " + token);
                call.enqueue(new Callback<AssetObjectResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<AssetObjectResponseModel> call, @NonNull retrofit2.Response<AssetObjectResponseModel> response) {
                        AssetObjectResponseModel responseType = response.body();

                        if (responseType != null) {
                            total=String.valueOf(responseType.data.total);
                            completed = String.valueOf(responseType.data.completed);
                            pending = String.valueOf(responseType.data.pending);
                            activityAssetListScreenBinding.tabLayout.getTabAt(0).setText("Completed" + "(" + completed + ")" );
                            activityAssetListScreenBinding.tabLayout.getTabAt(1).setText("Pending" + "(" + pending + ")" );
                            activityAssetListScreenBinding.tabLayout.getTabAt(2).setText("Total" + "(" + total + ")" );

                        } else {
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AssetObjectResponseModel> call, @NonNull Throwable t) {
                    }
                });
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}