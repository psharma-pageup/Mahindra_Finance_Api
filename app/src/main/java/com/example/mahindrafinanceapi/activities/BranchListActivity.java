package com.example.mahindrafinanceapi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.adaptors.BranchListAdaptor;
import com.example.mahindrafinanceapi.databinding.ActivityBranchListBinding;
import com.example.mahindrafinanceapi.models.BranchModel;
import com.example.mahindrafinanceapi.models.BranchResponseModel;
import com.example.mahindrafinanceapi.models.GenericResponseModel;
import com.example.mahindrafinanceapi.utility.ApiClient;
import com.example.mahindrafinanceapi.utility.ApiInterface;
import com.example.mahindrafinanceapi.utility.UtilityMethods;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class BranchListActivity extends AppCompatActivity {

    private ActivityBranchListBinding activityBranchListBinding;
    ArrayList<BranchModel> branchList = new ArrayList<>();
    BranchModel b = new BranchModel();
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
        getBranchList();
    }
//    private void setClickListener() {
//        activityBranchListBinding.btnRetry.setOnClickListener(this::onClick);
//    }
//    public void onClick(View v) {
//        int id = v.getId();
//
//        if(id == R.id.btnRetry) {
//            if (!UtilityMethods.isConnectingToInternet(this)) {
//                activityBranchListBinding.pbLoading.setVisibility(View.GONE);
//                activityBranchListBinding.tvPleaseWait.setVisibility(View.VISIBLE);
//                activityBranchListBinding.tvPleaseWait.setText(getResources().getString(R.string.no_net_msg));
//                return;
//            }
//            activityBranchListBinding.btnRetry.setVisibility(View.GONE);
//             GetBranchListTask();
//            activityBranchListBinding.pbLoading.setVisibility(View.VISIBLE);
//            activityBranchListBinding.tvPleaseWait.setVisibility(View.VISIBLE);
//            activityBranchListBinding.tvPleaseWait.setText(getResources().getString(R.string.please_wait_branch_list_getting_msg));
//            activityBranchListBinding.rvBranchList.setVisibility (View.GONE);
//
//
//        }
//
//
//
//    }

//    private void GetBranchListTask() {
//
//        BranchListAdaptor adapter = new BranchListAdaptor(this,branchList);
//        activityBranchListBinding.rvBranchList.setHasFixedSize(true);
//        activityBranchListBinding.rvBranchList.setLayoutManager(new LinearLayoutManager(this));
//        activityBranchListBinding.rvBranchList.setAdapter(adapter);
//    }

    private void getBranchList() {
        try {

            if (UtilityMethods.isConnectingToInternet(context)) {

                SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                String s1 = sh.getString("empCode", "");

                Call<BranchResponseModel> call = apiInterface.branchdetails(s1);
                call.enqueue(new Callback<BranchResponseModel>() {
                    @Override
                    public void onResponse(Call<BranchResponseModel> call, retrofit2.Response<BranchResponseModel> response) {
                        BranchResponseModel responseType = response.body();


                        if (responseType != null) {

                            if(responseType.branchresponse != null) {


                                branchList = responseType.branchresponse;
                                BranchListAdaptor adapter = new BranchListAdaptor(context, branchList);
                                adapter.notifyItemInserted(branchList.size() - 1);
                                activityBranchListBinding.rvBranchList.scrollToPosition(branchList.size() - 1);
                                activityBranchListBinding.rvBranchList.setHasFixedSize(true);
                                activityBranchListBinding.rvBranchList.setLayoutManager(new LinearLayoutManager(context));
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
                Toast.makeText(BranchListActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();


            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


}