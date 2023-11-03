package com.app.mahindrafinancemfact.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityMessageBinding;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.app.mahindrafinancemfact.models.ImeiModel;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.marcoscg.dialogsheet.DialogSheet;

import java.io.Serializable;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding activityMessageBinding;
    Serializable imeiresponse;
    ApiInterface apiInterface;
    Context context;
    String imei1;
    String imei2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(activityMessageBinding.getRoot());
        init();
        imeiresponse = getIntent().getSerializableExtra("imeiresponse");
        setMessage();

    }
    public void init(){
        try {
            context = this;
            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMessage(){
        if(imeiresponse != null) {
            if (imeiresponse.equals(4)) {
                activityMessageBinding.tvMessage.setText("Your Account Has Been locked, Kindly Resend Registration Request");
                activityMessageBinding.btnMessage.setText("Resend Request");
                activityMessageBinding.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getImei();
                    }
                });
            } else if (imeiresponse.equals(0)) {
                activityMessageBinding.tvMessage.setText("Application Under Maintainence");
                activityMessageBinding.btnMessage.setText("Exit Application");
                activityMessageBinding.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishAffinity();
                    }
                });

            } else if (imeiresponse.equals(5)) {
                activityMessageBinding.tvMessage.setText("Account has been locked due to inactive employee");
                activityMessageBinding.btnMessage.setText("Exit Application");
                activityMessageBinding.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishAffinity();
                    }
                });
            }
        }else{
            Toast.makeText(context, "imresponse is null", Toast.LENGTH_SHORT).show();
        }
    }


    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            imei1 = manager.getDeviceId();
            SendImei();

        } else if (manager.getPhoneCount() == 2) {

            imei1 = manager.getDeviceId(0);
            imei2 = manager.getDeviceId(1);
            SendImei();

        }
    }
    public void SendImei(){
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {

                HashMap<String, String> params = new HashMap<>();
                params.put("imeI1", imei1);
                params.put("imeI2", imei2);
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<ImeiModel> call = apiInterface.Resend_request(params,"Bearer " + token);
                call.enqueue(new Callback<ImeiModel>() {
                    @Override
                    public void onResponse(Call<ImeiModel> call, retrofit2.Response<ImeiModel> response) {
                        ImeiModel imeiresponse = response.body();
                        if (imeiresponse != null) {


                            if(imeiresponse.data.data == 1){
                                Toast.makeText(MessageActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            } else if (imeiresponse.data.data == 0) {
                                new DialogSheet(MessageActivity.this,true)
                                        .setTitle("Registration Failed")
                                        .setColoredNavigationBar(true)
                                        .setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark))
                                        .setTitleTextSize(30)
                                        .setCancelable(true)
                                        .setBackgroundColor(getResources().getColor(R.color.bgcolor))
                                        .setNegativeButton("Cancel", new DialogSheet.OnNegativeClickListener() {
                                            @Override
                                            public void onClick(@Nullable View view) {
                                                finishAffinity();
                                            }
                                        })
                                        .show();
                            }


                        } else
                        {
                            Toast.makeText(MessageActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ImeiModel> call, Throwable t) {

                        Toast.makeText(MessageActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MessageActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception e) {
            Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }



}