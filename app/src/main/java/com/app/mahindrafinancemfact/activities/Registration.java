package com.app.mahindrafinancemfact.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityRegistrationBinding;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.app.mahindrafinancemfact.models.RegistrationModel;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;


public class Registration extends AppCompatActivity {

    private ActivityRegistrationBinding activityRegistrationBinding;
    boolean isAllFieldsChecked = false;
    ApiInterface apiInterface;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistrationBinding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(activityRegistrationBinding.getRoot());
        init();
       getImei();
       clickListener();
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
     * getting the imei number from device
     * */
    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            activityRegistrationBinding.tvImeiOne.setText(Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));



        } else if (manager.getPhoneCount() == 2) {

            activityRegistrationBinding.tvImeiOne.setText(Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            activityRegistrationBinding.tvImeiTwo.setText(Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));

        }
    }

    /**
     * calling the serviceCall() function on the click of submit button
     * */
    public void clickListener(){
        activityRegistrationBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAllFieldsChecked = checkAllFields();
                if(isAllFieldsChecked){
                    serviceCall();

                }


            }
        });
    }

    /**
     * validations on edittext for sapcode
     * */
    private boolean checkAllFields() {
        if (activityRegistrationBinding.etSapCode.length() == 0) {
            activityRegistrationBinding.etSapCode.setError(getString(R.string.this_field_is_required));
            return false;
        }
        return true;
    }

    /**
     * Passing Sapcode and imei number to register an employee
     * */
    public void serviceCall(){
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE, View.VISIBLE,View.GONE);
                HashMap<String, String> params = new HashMap<>();
                params.put("sapCode", activityRegistrationBinding.etSapCode.getText().toString());
                params.put("imeI1", activityRegistrationBinding.tvImeiOne.getText().toString());
                params.put("imeI2", activityRegistrationBinding.tvImeiTwo.getText().toString());

                Call<RegistrationModel> call = apiInterface.Register_Service(params);
                call.enqueue(new Callback<RegistrationModel>() {
                    @Override
                    public void onResponse(Call<RegistrationModel> call, retrofit2.Response<RegistrationModel> response) {
                        RegistrationModel registerresponse = response.body();
                        showMsgView(View.GONE, View.GONE,View.GONE);
                        if (registerresponse != null) {

                            if(registerresponse.data == 1){
                                activityRegistrationBinding.inactive.setVisibility(View.GONE);
                                SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString("sapcode", activityRegistrationBinding.etSapCode.getText().toString());
                                myEdit.putString("imei1", activityRegistrationBinding.tvImeiOne.getText().toString());
                                myEdit.putString("imei2", activityRegistrationBinding.tvImeiTwo.getText().toString());
                                myEdit.apply();
                                Toast.makeText(Registration.this, R.string.registration_successfull, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (registerresponse.data == 0) {
                                activityRegistrationBinding.inactive.setVisibility(View.VISIBLE);
                            }
                        } else
                        {
                            showMsgView(View.GONE, View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<RegistrationModel> call, Throwable t) {

                        showMsgView(View.GONE, View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE, View.GONE,View.GONE);

            }
        }
        catch (Exception e) {
            Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * loader
     * */
    void showMsgView(int containerVisibility,int loading,int srvr) {
        try {
            activityRegistrationBinding.llinternet.setVisibility(containerVisibility);
            activityRegistrationBinding.pbLoading.setVisibility(loading);
            activityRegistrationBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}