package com.example.mahindrafinanceapi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivityWelcomeBinding;
import com.example.mahindrafinanceapi.models.GenericResponseModel;
import com.example.mahindrafinanceapi.models.ProfileModal;
import com.example.mahindrafinanceapi.models.ProfileObjectModel;
import com.example.mahindrafinanceapi.utility.ApiClient;
import com.example.mahindrafinanceapi.utility.ApiInterface;
import com.example.mahindrafinanceapi.utility.SharedPreferencesMethod;
import com.example.mahindrafinanceapi.utility.UtilityMethods;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding activityWelcomeBinding;
    ApiInterface apiInterface;
    Context context;
    String imei1;
    String imei2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWelcomeBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(activityWelcomeBinding.getRoot());
        context = this;

        init();
        getImei();
        getProfile();
        clickListener();

    }
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }

    public void clickListener(){
        activityWelcomeBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(true) {
                    Intent intent = new Intent(WelcomeActivity.this, BranchListActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(WelcomeActivity.this, MessageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
         //   imei1 = manager.getDeviceId();
           imei1= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else if (manager.getPhoneCount() == 2) {
//            imei1 = manager.getDeviceId(0);
//            imei2 = manager.getDeviceId(1);

            imei1= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            imei2= Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }
    private void getProfile() {
        try
        {
            if (UtilityMethods.isConnectingToInternet(context)) {

                HashMap<String, String> params = new HashMap<>();
                params.put("imeI1", imei1);
                params.put("imeI2", imei2);


                Call<ProfileObjectModel> call = apiInterface.getProfile(params);
                call.enqueue(new Callback<ProfileObjectModel>(){
                    @Override
                    public void onResponse(Call<ProfileObjectModel> call, retrofit2.Response<ProfileObjectModel>response) {
                        ProfileObjectModel profileObjectModel = response.body();

                        if (profileObjectModel != null) {

                                    activityWelcomeBinding.tvName.setText(profileObjectModel.data.name);
                                    String empCode = profileObjectModel.data.empCode;
                                    SharedPreferences sharedPreferences = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("empCode", empCode);
                                    myEdit.apply();



                        } else {

                            Toast.makeText(WelcomeActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ProfileObjectModel> call, Throwable t) {

                        Toast.makeText(WelcomeActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {

                Toast.makeText(WelcomeActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

            Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

}