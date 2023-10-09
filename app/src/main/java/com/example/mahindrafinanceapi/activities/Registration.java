package com.example.mahindrafinanceapi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegistrationBinding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(activityRegistrationBinding.getRoot());

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
            activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId());
        } else if (manager.getPhoneCount() == 2) {
            activityRegistrationBinding.llimeitwo.setVisibility(View.VISIBLE);
            activityRegistrationBinding.tvImeiOne.setText(manager.getDeviceId(0));
            activityRegistrationBinding.tvImeiTwo.setText(manager.getDeviceId(1));
        }


    }
}