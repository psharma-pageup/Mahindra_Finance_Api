package com.app.mahindrafinancemfact.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class TestQRActivity extends AppCompatActivity {
    Button btnScan;
    String r;
    private ApiInterface apiInterface;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_qractivity);
        btnScan = (Button) findViewById(R.id.btnScan);
        init();
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanCode();
            }
        });
    }

    public void init() {
        try {
            context = this;
            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ScanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if(result.getContents() != null)
        {


            
            r = result.getContents();
            if(r.length() == 30){
              //  ServiceCall();
            }else{
                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
          //  ServiceCall();
        }
    });

//    private void ServiceCall() {
//        try {
//            if (UtilityMethods.isConnectingToInternet(context)) {
//
//                HashMap<String, String> params = new HashMap<>();
//                params.put("qrstring", r);
//
//                Call<QRResponseModel> call = apiInterface.qrdetails(params);
//                call.enqueue(new Callback<QRResponseModel>() {
//                    @Override
//                    public void onResponse(Call<QRResponseModel> call, retrofit2.Response<QRResponseModel> response) {
//                        QRResponseModel qrResponseModel = response.body();
//
//                        if (qrResponseModel != null) {
//
//
//
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(TestQRActivity.this);
//                            builder.setTitle("Customer Information");
//                            builder.setMessage(qrResponseModel.data.customerInfo);
//                            builder.show();
////                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                            @Override
////                             public void onClick(DialogInterface dialogInterface, int i) {
////                                dialogInterface.dismiss();
////                             }
////                          }).show();
//
//
//
//
//
//                        } else
//                        {
//                            Toast.makeText(TestQRActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<QRResponseModel> call, Throwable t) {
//
//                        Toast.makeText(TestQRActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(TestQRActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();
//
//            }
//        }
//        catch (Exception e) {
//            Toast.makeText(TestQRActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//
//    }
}