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
    Serializable imeiResponse;
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
        imeiResponse = getIntent().getSerializableExtra("imeiresponse");
        setMessage();

    }

    /**
     * initialize apiinterface
     * */
    public void init(){
        try {
            context = this;
            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * According to the response received through intent from splash screen activity relevant messages are shown
     * */
    public void setMessage(){
        if(imeiResponse != null) {
            if (imeiResponse.equals(4)) {
                activityMessageBinding.tvMessage.setText(R.string.your_account_has_been_locked_kindly_resend_registration_request);
                activityMessageBinding.btnMessage.setText(R.string.resend_request);
                activityMessageBinding.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getImei();
                    }
                });
            } else if (imeiResponse.equals(0)) {
                activityMessageBinding.tvMessage.setText(R.string.application_under_maintainence);
                activityMessageBinding.btnMessage.setText(R.string.exit_application);
                activityMessageBinding.btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishAffinity();
                    }
                });

            } else if (imeiResponse.equals(5)) {
                activityMessageBinding.tvMessage.setText(R.string.account_has_been_locked_due_to_inactive_employee);
                activityMessageBinding.btnMessage.setText(R.string.exit_application);
                activityMessageBinding.btnMessage.setOnClickListener(v -> finishAffinity());
            }
        }else{
            Toast.makeText(context, R.string.imresponse_is_null, Toast.LENGTH_SHORT).show();
        }
    }

/**
 * getting the imei number from the device
 * */
    public void getImei(){
        SharedPreferences imeinumber = getSharedPreferences("IMEI", MODE_PRIVATE);
        String imei = imeinumber.getString("imei", "");
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            imei1 = imei;
            sendImei();

        } else if (manager.getPhoneCount() == 2) {
            imei1 = imei;
            imei2 = imei;
            sendImei();

        }
    }
    public void sendImei(){
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
                                Toast.makeText(MessageActivity.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            } else if (imeiresponse.data.data == 0) {
                                new DialogSheet(MessageActivity.this,true)
                                        .setTitle(R.string.registration_failed)
                                        .setColoredNavigationBar(true)
                                        .setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark))
                                        .setTitleTextSize(30)
                                        .setCancelable(true)
                                        .setBackgroundColor(getResources().getColor(R.color.bgcolor))
                                        .setNegativeButton(R.string.cancel, new DialogSheet.OnNegativeClickListener() {
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