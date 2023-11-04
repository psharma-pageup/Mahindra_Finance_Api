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

    /** initialize the api interface and calls setToolbar() and getBranchList() functions */
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
        setToolbar();
        getBranchList();
        activityBranchListBinding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBranchList();
            }
        });
    }

    /** set up toolbar*/
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

    /**
     * sapcode and bearer token is passed as parameters to receive branchlist as response
     */

    private void getBranchList() {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.GONE,View.VISIBLE,View.GONE);

                SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                String s1 = sh.getString("empCode", "");

                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");


                Call<BranchResponseModel> call = apiInterface.branchdetails(s1,"Bearer " + token);
                call.enqueue(new Callback<BranchResponseModel>() {
                    @Override
                    public void onResponse(Call<BranchResponseModel> call, retrofit2.Response<BranchResponseModel> response) {
                        BranchResponseModel responseType = response.body();
                        showMsgView(View.GONE,View.GONE,View.GONE);
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
                            showMsgView(View.GONE,View.GONE,View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<BranchResponseModel> call, Throwable t) {
                        showMsgView(View.GONE,View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.VISIBLE,View.GONE,View.GONE);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    void showMsgView(int containerVisibility, int loadingVisibility, int srvr) {
        try {
            activityBranchListBinding.llinternet.setVisibility(containerVisibility);
            activityBranchListBinding.load.setVisibility(loadingVisibility);
            activityBranchListBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}