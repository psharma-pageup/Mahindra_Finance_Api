package com.example.mahindrafinanceapi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivityMessageBinding;
import com.example.mahindrafinanceapi.models.ImeiModel;
import com.example.mahindrafinanceapi.utility.ApiClient;
import com.example.mahindrafinanceapi.utility.ApiInterface;
import com.example.mahindrafinanceapi.utility.UtilityMethods;
import com.marcoscg.dialogsheet.DialogSheet;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class MessageActivity extends AppCompatActivity {

    private ActivityMessageBinding activityMessageBinding;
    String imeiresponse;
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
        imeiresponse = getIntent().getStringExtra("imeiresponse");
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
        if(imeiresponse.equals(4)){
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
    }

    private void ServiceCall() {


    }
    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            // activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId());
            imei1 = manager.getDeviceId();
            SendImei();

        } else if (manager.getPhoneCount() == 2) {
//            activityRegistrationBinding.llimeitwo.setVisibility(View.VISIBLE);
//            activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId(0));
//            activityRegistrationBinding.tvImeiTwo.setText(manager.getDeviceId(1));
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
                Call<ImeiModel> call = apiInterface.Resend_request(params);
                call.enqueue(new Callback<ImeiModel>() {
                    @Override
                    public void onResponse(Call<ImeiModel> call, retrofit2.Response<ImeiModel> response) {
                        ImeiModel imeiresponse = response.body();
                        if (imeiresponse != null) {

//                                    LoginModel loginModel = (LoginModel) loginResponse.data.get(0);
//                                    Gson gson = new Gson();
//                                    Intent intent = new Intent(Registration.this,WelcomeActivity.class);
////                                    Intent intent = new Intent(context, VerifyActivity.class);
//                                    intent.putExtra("LoginModel", loginModel.toString());
//                                    startActivity(intent);
//                            loginModel.sapcode = activityRegistrationBinding.etSapCode.getText().toString();
//                            isAllFieldsChecked = CheckAllFields();
//                            if(isAllFieldsChecked){
//                                SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
//                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
//
//
//                                myEdit.putString("sapcode", activityRegistrationBinding.etSapCode.getText().toString());
//                                myEdit.putString("imei1", activityRegistrationBinding.tvImeiOne.getText().toString());
//                                myEdit.putString("imei2", activityRegistrationBinding.tvImeiTwo.getText().toString());
//                                myEdit.apply();
//                                SharedPreferencesMethod.setAuthToken(context,loginResponse.data.access_token);
//                                Intent intent = new Intent(Registration.this,WelcomeActivity.class);
//                                startActivity(intent);
//                            }

                            if(imeiresponse.data == 1){
                                Toast.makeText(MessageActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            } else if (imeiresponse.data == 0) {
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