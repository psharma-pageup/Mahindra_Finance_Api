package com.example.mahindrafinanceapi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivityAuditDetailsScreenBinding;
import com.example.mahindrafinanceapi.models.BranchModel;

public class AuditDetailsScreenActivity extends AppCompatActivity {

    private ActivityAuditDetailsScreenBinding activityAuditDetailsScreenBinding;
//    BranchModel branchModel;
    String aid;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAuditDetailsScreenBinding = ActivityAuditDetailsScreenBinding.inflate(getLayoutInflater());
        setContentView(activityAuditDetailsScreenBinding.getRoot());
        init();
        clickListener();
        GetAuditId();
    }

    private void GetAuditId() {

        activityAuditDetailsScreenBinding.tvAuditId.setText(aid);
        activityAuditDetailsScreenBinding.tvLocation.setText(location);
    }

    private void clickListener() {
    activityAuditDetailsScreenBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent = new Intent(AuditDetailsScreenActivity.this, TestScanActivity.class);
        startActivity(intent);
        }
    });
    }

    public void init(){
       aid = getIntent().getStringExtra("aid");
       location = getIntent().getStringExtra("branch");
      //  activityAuditDetailsScreenBinding.tvLocation.setText(branchModel.getBranch_name());
    }
}