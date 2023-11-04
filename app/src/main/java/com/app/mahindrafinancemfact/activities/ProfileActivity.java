package com.app.mahindrafinancemfact.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityProfileBinding;
import com.app.mahindrafinancemfact.models.ProfileObjectModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding activityWelcomeBinding;
    ApiInterface apiInterface;
    Context context;
    String imei1;
    String imei2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWelcomeBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityWelcomeBinding.getRoot());
        context = this;

        init();
        setToolbar();
        getImei();
        getProfile();
        clickListener();

    }
    /**
     * initialize apiinterface
     * */
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    /**
     * setup toolbar
     * */
    private void setToolbar() {
        setSupportActionBar(activityWelcomeBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityWelcomeBinding.toolbar.setNavigationIcon(R.drawable.back_button);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickListener(){
        activityWelcomeBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(ProfileActivity.this, BranchListActivity.class);
                    startActivity(intent);
            }
        });
    }
/**
 * get imei number of the device
 * */
    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
           imei1= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else if (manager.getPhoneCount() == 2) {

            imei1= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            imei2= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }
    /**
     * send imei number of device and bearer token as params to receive profile information as response
     * */
    private void getProfile() {
        try
        {
            if (UtilityMethods.isConnectingToInternet(context)) {

                showMsgView(View.GONE,View.GONE);

                HashMap<String, String> params = new HashMap<>();
                params.put("imeI1", imei1);
                params.put("imeI2", imei2);

                SharedPreferences sh = getSharedPreferences("Token", MODE_PRIVATE);
                String token = sh.getString("token", "");


                Call<ProfileObjectModel> call = apiInterface.getProfile(params,"Bearer " + token);
                call.enqueue(new Callback<ProfileObjectModel>(){
                    @Override
                    public void onResponse(Call<ProfileObjectModel> call, retrofit2.Response<ProfileObjectModel>response) {
                        ProfileObjectModel profileObjectModel = response.body();

                        if (profileObjectModel != null) {

                                    activityWelcomeBinding.Name.setText(profileObjectModel.data.name.toLowerCase());
                                    String empCode = profileObjectModel.data.empCode;
                                    SharedPreferences sharedPreferences = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("empCode", empCode);
                                    myEdit.apply();

                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<ProfileObjectModel> call, Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);
                    }
                });
            } else {

                showMsgView(View.VISIBLE,View.GONE);
            }
        } catch (Exception e) {

            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }
    void showMsgView(int containerVisibility,int srvr) {
        try {
            activityWelcomeBinding.llinternet.setVisibility(containerVisibility);
            activityWelcomeBinding.servererror.setVisibility(srvr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}