package com.app.mahindrafinancemfact.activities;

import static android.R.layout.simple_spinner_item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.adaptors.CustomSpinnerAdapter;
import com.app.mahindrafinancemfact.databinding.ActivityFormBinding;
import com.app.mahindrafinancemfact.models.AssetBranchListModel;
import com.app.mahindrafinancemfact.models.AssetBranchListResponseModel;
import com.app.mahindrafinancemfact.models.AssetinforesponseModel;
import com.app.mahindrafinancemfact.models.FormModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;

import org.apache.http.conn.scheme.LayeredSocketFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class FormActivity extends AppCompatActivity {
    Spinner spinnerDropdown;
    Spinner spinnerDropdown2;
    Spinner spinnerDropdown3;
    Context context;
    String remark;
    String receipt;
    String concat;
    ArrayList<AssetBranchListModel> entries;
    ApiInterface apiInterface;
    String selectedValue;
    List<CharSequence> drop = new ArrayList<>();

    String selectedValue2;
    String selectedValue3;
    private ActivityFormBinding activityFormBinding;
    String Hocode;
    String bcode;
    String aid;
    String sapcode;
    String branchcode;
    LayerDrawable backgroundDrawable;
    LayerDrawable destbg;
    Drawable destbgdraw;
    Drawable bodyDrawable;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFormBinding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(activityFormBinding.getRoot());
        context = this;
        spinnerDropdown = findViewById(R.id.spinner);
        spinnerDropdown2 = findViewById(R.id.spinner2);
        spinnerDropdown3 = findViewById(R.id.spinner3);
        Hocode = getIntent().getStringExtra("hocode");
        activityFormBinding.nxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
        spinner();
        init();
        setData();
    }

    private void sendData() {
        String[] parts = bcode.split("-");
        String extracted = null;
        if (parts.length >= 1) {
            extracted = parts[0];
        } else {
            Toast.makeText(context, "No Delimiter", Toast.LENGTH_SHORT).show();
        }

        remark = activityFormBinding.remark.getText().toString();
        receipt = activityFormBinding.receipt.getText().toString();
        if(selectedValue2.equals("Sold"))
        {
            concat = receipt + " " + remark;
            branchcode = extracted;
        } else if (selectedValue2.equals("Transfer")) {
            String[] part = selectedValue3.split("-");
            String extract = null;
            if (part.length >= 1) {
                extract = part[0];
            } else {
                Toast.makeText(context, "No Delimiter", Toast.LENGTH_SHORT).show();
            }
            concat = remark;
            branchcode = extract;
        } else{
            concat = remark;
            branchcode = extracted;
        }


        try {
            SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
            sapcode = sh.getString("empCode", "");
            SharedPreferences ai = getSharedPreferences("AID",MODE_PRIVATE);
            aid = ai.getString("aid","");

            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE,View.GONE);

                HashMap<String, String> params = new HashMap<>();
                params.put("sapcode", sapcode);
                params.put("aid", aid);
                params.put("hocode", Hocode);
                params.put("astatus", selectedValue);
                params.put("atag",selectedValue2);
                params.put("remarks",concat);
                if (selectedValue2.equals("Transfer")){
                params.put("bcode",branchcode);
                }
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<FormModel> call = apiInterface.Form(params,"Bearer " + token);
                call.enqueue(new Callback<FormModel>() {
                    @Override
                    public void onResponse(@NonNull Call<FormModel> call, @NonNull retrofit2.Response<FormModel> response) {
                        FormModel responseType = response.body();
                    }
                    @Override
                    public void onFailure(@NonNull Call<FormModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE,View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        Intent intent = new Intent(this,AssetListScreenActivity.class);
        startActivity(intent);
        finishAndRemoveTask();
    }

    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        backgroundDrawable = (LayerDrawable) activityFormBinding.receipt.getBackground();
        destbg = (LayerDrawable) activityFormBinding.spinner3.getBackground();
        destbgdraw = destbg.getDrawable(0);
        bodyDrawable = backgroundDrawable.getDrawable(0);
        destbgdraw.setColorFilter(ContextCompat.getColor(FormActivity.this,R.color.diabled),PorterDuff.Mode.SRC_IN);
        bodyDrawable.setColorFilter(ContextCompat.getColor(FormActivity.this, R.color.diabled), PorterDuff.Mode.SRC_IN);
        activityFormBinding.receipt.setBackground(backgroundDrawable);
        activityFormBinding.spinner3.setBackground(destbg);
        activityFormBinding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAssetinfo();
            }
        });
        getAssetinfo();
    }
    private void getAssetinfo() {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE,View.GONE);
                String params = Hocode;
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetinforesponseModel> call = apiInterface.assetinfo(params,"Bearer " + token);
                call.enqueue(new Callback<AssetinforesponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<AssetinforesponseModel> call, @NonNull retrofit2.Response<AssetinforesponseModel> response) {
                        AssetinforesponseModel responseType = response.body();

                        if (responseType != null) {

                            activityFormBinding.sscat.setText(responseType.data.sscat);
                            activityFormBinding.serial.setText(responseType.data.serialNumber);
                            activityFormBinding.owner.setText(responseType.data.aOwner);
                            activityFormBinding.detail.setText(responseType.data.details);
                            bcode = responseType.data.bcode;

                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AssetinforesponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE,View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        activityFormBinding.assetcode.setText(Hocode);
    }
    private void setToolbar() {
        setSupportActionBar(activityFormBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activityFormBinding.toolbar.setNavigationIcon(R.drawable.back_button);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(FormActivity.this,AssetListScreenActivity.class);
            startActivity(intent);
            finishAndRemoveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void spinner2() {
        int entries;
        boolean conditionMet;

        if(selectedValue.equals("Available")){
            conditionMet = true;
        }else{
            conditionMet = false;
        }

        if (conditionMet) {
             entries = R.array.available;
        } else {
            entries = R.array.unavailable;
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                entries,
                simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDropdown2.setAdapter(adapter);
//        List<CharSequence> spinnerItems = Arrays.asList(getResources().getTextArray(entries));
//        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, R.layout.custom_form_layout_item, spinnerItems);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDropdown2.setAdapter(adapter);

        spinnerDropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedValue2 = (String) parentView.getItemAtPosition(position);
                if(selectedValue2.equals("Sold")){
                    activityFormBinding.receipt.setVisibility(View.VISIBLE);
                    activityFormBinding.tvreceipt.setVisibility(View.VISIBLE);
                    activityFormBinding.dest.setVisibility(View.GONE);
                    activityFormBinding.relativedest.setVisibility(View.GONE);
                    activityFormBinding.receipt.setEnabled(true);
                    activityFormBinding.tvreceipt.setText(R.string.receipt_no);
                    bodyDrawable.setColorFilter(ContextCompat.getColor(FormActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
                    activityFormBinding.receipt.setBackground(backgroundDrawable);
                    activityFormBinding.dest.setText("---");
                    destbgdraw.setColorFilter(ContextCompat.getColor(FormActivity.this,R.color.diabled),PorterDuff.Mode.SRC_IN);
                    activityFormBinding.spinner3.setBackground(destbg);
                    activityFormBinding.spinner3.setEnabled(false);


                } else if (selectedValue2.equals("Transfer")) {
                    activityFormBinding.receipt.setEnabled(false);
                    activityFormBinding.tvreceipt.setText("---");
                    activityFormBinding.receipt.setVisibility(View.GONE);
                    activityFormBinding.tvreceipt.setVisibility(View.GONE);
                    activityFormBinding.dest.setVisibility(View.VISIBLE);
                    activityFormBinding.relativedest.setVisibility(View.VISIBLE);
                    bodyDrawable.setColorFilter(ContextCompat.getColor(FormActivity.this, R.color.diabled), PorterDuff.Mode.SRC_IN);
                    activityFormBinding.receipt.setBackground(backgroundDrawable);
                    activityFormBinding.spinner3.setEnabled(true);

                    spinner3();
                } else {
                    activityFormBinding.receipt.setEnabled(false);
                    activityFormBinding.tvreceipt.setText("---");
                    activityFormBinding.receipt.setVisibility(View.GONE);
                    activityFormBinding.tvreceipt.setVisibility(View.GONE);
                    activityFormBinding.dest.setVisibility(View.GONE);
                    activityFormBinding.relativedest.setVisibility(View.GONE);
                    bodyDrawable.setColorFilter(ContextCompat.getColor(FormActivity.this, R.color.diabled), PorterDuff.Mode.SRC_IN);
                    activityFormBinding.receipt.setBackground(backgroundDrawable);
                    activityFormBinding.dest.setText("---");
                    destbgdraw.setColorFilter(ContextCompat.getColor(FormActivity.this,R.color.diabled),PorterDuff.Mode.SRC_IN);
                    activityFormBinding.spinner3.setBackground(destbg);
                    activityFormBinding.spinner3.setEnabled(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void spinner3() {

        activityFormBinding.dest.setText(R.string.destination);
        destbgdraw.setColorFilter(ContextCompat.getColor(FormActivity.this,R.color.white),PorterDuff.Mode.SRC_IN);
        activityFormBinding.spinner3.setBackground(destbg);

        SharedPreferences ai = getSharedPreferences("AID",MODE_PRIVATE);
        aid = ai.getString("aid","");
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE,View.GONE);
                String params = aid;
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetBranchListResponseModel> call = apiInterface.assetbranch(params,"Bearer " + token);
                call.enqueue(new Callback<AssetBranchListResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<AssetBranchListResponseModel> call, @NonNull retrofit2.Response<AssetBranchListResponseModel> response) {
                        AssetBranchListResponseModel responseType = response.body();

                        if (responseType != null) {

                            for(int i = 0; i< responseType.assetbranch.size();i++) {
                                entries = responseType.assetbranch;
                                drop.addAll(Collections.singleton(entries.get(i).branch));
                            }
                            ArrayAdapter adapter = new ArrayAdapter(context, simple_spinner_item, drop);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnerDropdown3.setAdapter(adapter);
//                            List<CharSequence> spinnerItems = drop;
//                            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, R.layout.custom_form_layout_item, spinnerItems);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            spinnerDropdown3.setAdapter(adapter);

                            spinnerDropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    selectedValue3 = (String) parentView.getItemAtPosition(position);

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }
                            });
                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AssetBranchListResponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE,View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void spinner(){
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.status,
//                simple_spinner_item
//        );
//        Drawable customSpinnerBackground = getResources().getDrawable(R.drawable.textview_resource_file);
//        spinnerDropdown.setBackground(customSpinnerBackground);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerDropdown.setAdapter(adapter);
//        Drawable customSpinnerBackground = getResources().getDrawable(R.drawable.textview_resource_file);
//
//        List<CharSequence> spinnerItems = Arrays.asList(getResources().getTextArray(R.array.status));
//        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(context, R.layout.custom_form_layout_item, spinnerItems);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDropdown.setBackground(customSpinnerBackground);
//        spinnerDropdown.setAdapter(adapter);

        spinnerDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedValue = (String) parentView.getItemAtPosition(position);
                spinner2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


    }
    void showMsgView(int containerVisibility,int srvr) {
        try {
            activityFormBinding.llinternet.setVisibility(containerVisibility);
            activityFormBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(FormActivity.this, AssetListScreenActivity.class));
        finish();

    }
}