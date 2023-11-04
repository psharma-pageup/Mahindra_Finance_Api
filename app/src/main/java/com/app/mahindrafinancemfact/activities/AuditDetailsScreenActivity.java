package com.app.mahindrafinancemfact.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityAuditDetailsScreenBinding;

public class AuditDetailsScreenActivity extends AppCompatActivity {
    private ActivityAuditDetailsScreenBinding activityAuditDetailsScreenBinding;
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
        Intent intent = new Intent(AuditDetailsScreenActivity.this, ScanActivity.class);
        startActivity(intent);
        }
    });
    }
    public void init(){
       aid = getIntent().getStringExtra("aid");
       location = getIntent().getStringExtra("branch");
       setToolbar();
    }
    private void setToolbar() {
        setSupportActionBar(activityAuditDetailsScreenBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityAuditDetailsScreenBinding.toolbar.setNavigationIcon(R.drawable.back_button);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}