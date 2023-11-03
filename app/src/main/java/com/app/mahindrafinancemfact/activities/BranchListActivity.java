package com.app.mahindrafinancemfact.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivityBranchListBinding;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.app.mahindrafinancemfact.adaptors.BranchListAdaptor;
import com.app.mahindrafinancemfact.models.BranchModel;
import com.app.mahindrafinancemfact.models.BranchResponseModel;
import com.app.mahindrafinancemfact.utility.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class BranchListActivity extends AppCompatActivity {

    private ActivityBranchListBinding activityBranchListBinding;
    ArrayList<BranchModel> branchList = new ArrayList<>();
    Context context;
   ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBranchListBinding = ActivityBranchListBinding.inflate(getLayoutInflater());
        setContentView(activityBranchListBinding.getRoot());
        context = this;
        init();
    }

    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        getBranchList();
    }

    private void setToolbar() {
        setSupportActionBar(activityBranchListBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityBranchListBinding.toolbar.setNavigationIcon(R.drawable.back_button);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBranchList() {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                activityBranchListBinding.llLogo.setVisibility(View.GONE);
                activityBranchListBinding.load.setVisibility(View.VISIBLE);
                activityBranchListBinding.llinternet.setVisibility(View.GONE);

                SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                String s1 = sh.getString("empCode", "");

                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");


                Call<BranchResponseModel> call = apiInterface.branchdetails(s1,"Bearer " + token);
                call.enqueue(new Callback<BranchResponseModel>() {
                    @Override
                    public void onResponse(Call<BranchResponseModel> call, retrofit2.Response<BranchResponseModel> response) {
                        BranchResponseModel responseType = response.body();
                        activityBranchListBinding.llLogo.setVisibility(View.VISIBLE);
                        activityBranchListBinding.load.setVisibility(View.GONE);


                        if (responseType != null) {

                            if(responseType.branchresponse != null) {
                                branchList = responseType.branchresponse;
                                BranchListAdaptor adapter = new BranchListAdaptor(context, branchList);
                                adapter.notifyItemInserted(branchList.size() - 1);
                                activityBranchListBinding.rvBranchList.scrollToPosition(branchList.size() - 1);
                                activityBranchListBinding.rvBranchList.setHasFixedSize(true);
                                activityBranchListBinding.rvBranchList.setLayoutManager(new GridLayoutManager(context,2));
                                activityBranchListBinding.rvBranchList.setAdapter(adapter);
                            }else{
                               activityBranchListBinding.llLogo.setVisibility(View.GONE);
                               activityBranchListBinding.NoAuditTask.setVisibility(View.VISIBLE);
                               activityBranchListBinding.exit.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       finishAffinity();
                                   }
                               });
                            }

                        } else {
                            Toast.makeText(BranchListActivity.this, getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<BranchResponseModel> call, Throwable t) {
                        Toast.makeText(BranchListActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                activityBranchListBinding.llinternet.setVisibility(View.VISIBLE);
                activityBranchListBinding.llLogo.setVisibility(View.GONE);
                Toast.makeText(BranchListActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();


            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}