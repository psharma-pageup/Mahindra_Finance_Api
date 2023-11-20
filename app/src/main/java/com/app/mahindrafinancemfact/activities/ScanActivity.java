package com.app.mahindrafinancemfact.activities;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.app.mahindrafinancemfact.R;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class ScanActivity extends CaptureActivity {
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
    View view;
    LinearLayout llinternet;
    LinearLayout servererror;
    int targetSizeKB = 30 * 1024;
    int quality = 100;
/**
 * tackles with the result we get after scanning barcode as well as getting the image of barcode that we scanned and conversion of that image to base 64
 * */

    private final BarcodeCallback callback = new BarcodeCallback() {
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
      //      long imageSizeKB = byteArray.length / 1024;
            while (byteArray.length  > targetSizeKB && quality > 0) {
                byteArrayOutputStream.reset();
                image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                quality -= 10;



            }
            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            byte[] b64 = Base64.decode(base64Image,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b64, 0, b64.length);
            try {
                saveBitmapToFile(context, bitmap);
            } catch (Exception ex) {

            }
            int sizeInKB = b64.length / 1024;
            trial();
        }





        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }

    };

    //
    private static void saveBitmapToFile(Context context, Bitmap bitmap) throws IOException {
        try {

            File imageFile = createTempImageFile(context);

            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                // Compress the bitmap to JPEG format
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }

            // Notify the media scanner about the new file
            android.media.MediaScannerConnection.scanFile(
                    context,
                    new String[]{imageFile.getAbsolutePath()},
                    null,
                    null);
        }catch(Exception ex)

        {
            ex.printStackTrace();
        }
        // Optionally, you can delete the temporary file if you don't need it anymore
        // imageFile.delete();
    }

    private static File createTempImageFile(Context context) throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    //
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomScannerTheme);
        setContentView(R.layout.activity_test_scan);
        context = this;
        barcodeView = findViewById(R.id.barcode_scanner);
        view = findViewById(R.id.centerHorizont);
        llinternet = findViewById(R.id.llinternet);
        pbloading = findViewById(R.id.pbLoading);
        servererror = findViewById(R.id.servererror);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        init();
    }
    /**
     * initialize apiinterface
     * */
    public void init(){
        apiInterface = ApiClient.getClient(context).create(ApiInterface.class);
    }
    /**
     * get the location and access latitude and longitude co-ordinates
     * */
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_location);
            builder.setMessage(R.string.please_turn_on_your_location);
            builder.setPositiveButton(R.string.settings, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                dialog.cancel();
                finish();
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
            assert mLastLocation != null;
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
/**
 * if ( lengh of result received after scanning the qr code is = 30)
 * {
 *     then show a popup with 4 options
 *     which ever option the user clicks on store the value of that function in Atag and call the function ServiceCall()
 * <p>
 *     if user clicks on AssetList option then call QRSerciceCall() function
 * }
 * */

public void trial(){
    if(lastText != null) {

        if (lastText.length() == 30) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.choose_a_condition);

            String[] status = {getString(R.string.working_condition), getString(R.string.not_in_use), getString(R.string.scrap), getString(R.string.not_in_working_condition)};
            builder.setItems(status, (dialog, which) -> {
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

                serviceCall();
            });
            builder.setPositiveButton(R.string.assetlist, (dialog, which) -> qrservicecall());
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                dialog.cancel();
                finish();

            });


            AlertDialog dialog = builder.create();
            dialog.show();

        } else {

            Toast.makeText(context, R.string.invalid_qr, LENGTH_SHORT).show();

        }
    }else{
        Toast.makeText(context, R.string.please_scan_qr_code_first,LENGTH_SHORT).show();
    }
}

    /**
     * takes bearer token and the string retrived after scanning the qr as params and returns customer information as response
     * */
    private void qrservicecall() {
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.VISIBLE,View.GONE);
                String params = lastText;
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<QRResponseModel> call = apiInterface.qrdetails(params,"Bearer " + token);
                call.enqueue(new Callback<QRResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<QRResponseModel> call, @NonNull retrofit2.Response<QRResponseModel> response) {
                        QRResponseModel qrResponseModel = response.body();
                        showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE);
                        if (qrResponseModel != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                            builder.setTitle(getString(R.string.customer_information));
                            builder.setMessage(qrResponseModel.data.customerInfo);
                            builder.setPositiveButton(getString(R.string.OK), (dialog, which) -> finish());
                            builder.show();
                        } else
                        {
                            showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<QRResponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.VISIBLE);

                    }
                });
            } else {
                showMsgView(View.GONE,View.GONE,View.GONE,View.VISIBLE,View.GONE,View.GONE);

            }
        }
        catch (Exception e) {
            Toast.makeText(ScanActivity.this, e.getMessage(), LENGTH_SHORT).show();

        }

    }

    /**
     * takes sapcode,audit id,string received after scanning qr code, latitude,longitude,Atag and base 64 image as parameters and returns sucessful/unsuccessful as response
     * */
    private void serviceCall() {

        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.VISIBLE,View.GONE);
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
                    public void onResponse(@NonNull Call<ImeiObjectModel> call, @NonNull retrofit2.Response<ImeiObjectModel> response) {
                        ImeiObjectModel imeiresponse = response.body();
                        showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE);

                        if (imeiresponse != null) {

                            if(imeiresponse.data == 0 ){
                                Toast.makeText(ScanActivity.this, R.string.unsuccessful, LENGTH_SHORT).show();
                                finish();
                            } else if (imeiresponse.data == 1) {
                                Toast.makeText(ScanActivity.this, R.string.successful, LENGTH_SHORT).show();
                                finish();
                            }
                        } else
                        {
                            showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ImeiObjectModel> call, @NonNull Throwable t) {

                        showMsgView(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.VISIBLE);

                    }
                });
            } else {
                showMsgView(View.GONE,View.GONE,View.GONE,View.VISIBLE,View.GONE,View.GONE);
            }
        }
        catch (Exception e) {
            Toast.makeText(ScanActivity.this, e.getMessage(), LENGTH_SHORT).show();
        }
    }

    void showMsgView(int barcodeview, int btnlyt,int v, int net, int load, int srvrerr ) {
        try {
            barcodeView.setVisibility(barcodeview);
            buttonslayout.setVisibility(btnlyt);
            view.setVisibility(v);
            llinternet.setVisibility(net);
            pbloading.setVisibility(load);
            servererror.setVisibility(srvrerr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}