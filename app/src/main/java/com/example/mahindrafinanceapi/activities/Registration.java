package com.example.mahindrafinanceapi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivityRegistrationBinding;
import com.example.mahindrafinanceapi.databinding.ActivitySplashScreenBinding;


public class Registration extends AppCompatActivity {

    private ActivityRegistrationBinding activityRegistrationBinding;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistrationBinding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(activityRegistrationBinding.getRoot());

       getImei();
       clickListener();



    }
    public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId());
        } else if (manager.getPhoneCount() == 2) {
            activityRegistrationBinding.llimeitwo.setVisibility(View.VISIBLE);
            activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId(0));
            activityRegistrationBinding.tvImeiTwo.setText(manager.getDeviceId(1));

        }
    }
    public void clickListener(){
        activityRegistrationBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAllFieldsChecked = CheckAllFields();
                if(isAllFieldsChecked){
                    SharedPreferences sharedPreferences = getSharedPreferences("Data", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();


                    myEdit.putString("sapcode", activityRegistrationBinding.etSapCode.getText().toString());
                    myEdit.putString("imei1", activityRegistrationBinding.tvImeiOne.getText().toString());
                    myEdit.putString("imei2", activityRegistrationBinding.tvImeiTwo.getText().toString());
                    myEdit.apply();
                    Intent intent = new Intent(Registration.this,WelcomeActivity.class);
                    startActivity(intent);
                }


            }
        });
    }
    private boolean CheckAllFields() {
        if (activityRegistrationBinding.etSapCode.length() == 0) {
            activityRegistrationBinding.etSapCode.setError("This field is required");
            return false;
        }
        return true;
    }
}