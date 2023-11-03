package com.app.mahindrafinancemfact.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.models.ImeiModel;
import com.app.mahindrafinancemfact.models.ImeiObjectModel;
import com.app.mahindrafinancemfact.models.QRResponseModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class TestScanActivity extends CaptureActivity {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private static final int REQUEST_LOCATION = 1;
    FusedLocationProviderClient mFusedLocationClient;
    String latitude;
    String longitude;

    Bitmap image;
    String base64Image;
    Context context;
    String Atag;
    String s1;
    String s2;
    ApiInterface apiInterface;

    LinearLayout buttonslayout;

    ProgressBar pbloading;
    ProgressBar pbloading2;
    View view;

    LinearLayout llinternet;



    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();



            image = result.getBitmapWithResultPoints(Color.YELLOW);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomScannerTheme);
        setContentView(R.layout.activity_test_scan);
        context = this;

        barcodeView = findViewById(R.id.barcode_scanner);
        buttonslayout = findViewById(R.id.buttonsLayout);
        view = findViewById(R.id.centerHorizont);
        llinternet = findViewById(R.id.llinternet);
        pbloading = findViewById(R.id.pbLoading);
        pbloading2 = findViewById(R.id.pbLoading2);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
        init();

    }
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);

    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {


            if (isLocationEnabled()) {


                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS Location");
            builder.setMessage("Please Turn On Your Location");
            builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();

                }
            });
            builder.show();

            }
        } else {

            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    };


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION"},REQUEST_LOCATION);
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void conclude(View view) {

        Intent intent = new Intent(TestScanActivity.this,AssetListScreenActivity.class);
        startActivity(intent);
    }

    public void scan(View view) {

        if(lastText != null) {

            if (lastText.length() == 30) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose a condition");

                String[] status = {"Working Condition", "Not in use", "Scrap", "Not in Working Condition"};
                builder.setItems(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                Atag = "Working Condition";
                                break;
                            }
                            case 1: {
                                Atag = "Not in use";
                                break;
                            }
                            case 2: {
                                Atag = "Scrap";
                                break;
                            }
                            case 3: {
                                Atag = "Not in Working Condition";
                                break;
                            }
                        }

                        ServiceCall();
                    }
                });
                builder.setPositiveButton("AssetList", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        QRServiceCall();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();

                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                Toast.makeText(context, "Invalid QR", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(context, "Please Scan QR Code first", Toast.LENGTH_SHORT).show();
        }
    }
    private void QRServiceCall() {
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                barcodeView.setVisibility(View.VISIBLE);
                buttonslayout.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                llinternet.setVisibility(View.GONE);
                pbloading.setVisibility(View.VISIBLE);
                String params = lastText;
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<QRResponseModel> call = apiInterface.qrdetails(params,"Bearer " + token);
                call.enqueue(new Callback<QRResponseModel>() {
                    @Override
                    public void onResponse(Call<QRResponseModel> call, retrofit2.Response<QRResponseModel> response) {
                        QRResponseModel qrResponseModel = response.body();
                        pbloading.setVisibility(View.GONE);
                        if (qrResponseModel != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TestScanActivity.this);
                            builder.setTitle("Customer Information");
                            builder.setMessage(qrResponseModel.data.customerInfo);
                            builder.show();
                        } else
                        {
                            Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<QRResponseModel> call, Throwable t) {

                        Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                barcodeView.setVisibility(View.GONE);
                buttonslayout.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                llinternet.setVisibility(View.VISIBLE);
                Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception e) {
            Toast.makeText(TestScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void ServiceCall() {

        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                barcodeView.setVisibility(View.VISIBLE);
                buttonslayout.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                llinternet.setVisibility(View.GONE);
                pbloading.setVisibility(View.VISIBLE);
                SharedPreferences sh = getSharedPreferences("SAPCODE", MODE_PRIVATE);
                s1 = sh.getString("empCode", "");

                SharedPreferences ai = getSharedPreferences("AID",MODE_PRIVATE);
                s2 = ai.getString("aid","");
                HashMap<String, String> params = new HashMap<>();
                params.put("Sapcode", s1);
                params.put("aid", s2);
                params.put("Ref",lastText);
                params.put("Longitude",longitude);
                params.put("Latitude",latitude);
                params.put("Atag",Atag);
                params.put("base64",base64Image);


                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");

                Call<ImeiObjectModel> call = apiInterface.Final_request(params,"Bearer "+token);
                call.enqueue(new Callback<ImeiObjectModel>() {
                    @Override
                    public void onResponse(Call<ImeiObjectModel> call, retrofit2.Response<ImeiObjectModel> response) {
                        ImeiObjectModel imeiresponse = response.body();
                        pbloading.setVisibility(View.GONE);

                        if (imeiresponse != null) {

                            if(imeiresponse.data == 0 ){
                                Toast.makeText(TestScanActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                            } else if (imeiresponse.data == 1) {
                                Toast.makeText(TestScanActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                            }
                        } else
                        {
                            Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ImeiObjectModel> call, Throwable t) {

                        Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                barcodeView.setVisibility(View.GONE);
                buttonslayout.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                llinternet.setVisibility(View.VISIBLE);
                Toast.makeText(TestScanActivity.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(TestScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}