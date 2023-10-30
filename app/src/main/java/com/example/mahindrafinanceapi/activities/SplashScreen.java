package com.example.mahindrafinanceapi.activities;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivitySplashScreenBinding;
import com.example.mahindrafinanceapi.models.ImeiModel;
import com.example.mahindrafinanceapi.models.QRResponseModel;
import com.example.mahindrafinanceapi.models.Test;
import com.example.mahindrafinanceapi.utility.ApiClient;
import com.example.mahindrafinanceapi.utility.ApiInterface;
import com.example.mahindrafinanceapi.utility.UtilityMethods;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.marcoscg.dialogsheet.DialogSheet;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private SharedPreferences permissionStatus;
    private static final int PERMISSION_CALLBACK_CONSTANT = 101;
    private static final int REQUEST_PERMISSION_SETTING = 102;


    //    HashMap<String, Object> input;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    // private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //    mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivitySplashScreenBinding binding;
    private ApiInterface apiInterface;
    Context context;
    String imei1;
    String imei2;
    LocationManager locationManager;
    String r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mContentView = binding.fullscreenContent;

        // Set up the user interaction to manually show or hide the system UI.

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //   Const.input = new HashMap<>();
        init();
       // gps();


        setView();


    }

    private void gps() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(context, (int)latitude, Toast.LENGTH_SHORT).show();


            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }


    public void init() {
        try {
            context = this;
            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void setView() {
        binding.btnGettingStarted.setOnClickListener(this::onClick);
        binding.btnQrDetails.setOnClickListener(this::onClick);
        checkPermission();
    }
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGettingStarted) {
            getImei();
            SendImei();
        } else if (id == R.id.btnQrDetails) {
            ScanCode();
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
                QRServiceCall();
            }else{
                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        }
    });
    private void QRServiceCall() {
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
//                HashMap<String, String> params = new HashMap<>();
//                params.put("QRCode", r);
                binding.llinternet.setVisibility(View.GONE);
                binding.net.setVisibility(View.VISIBLE);

                String params = r;
                Call<QRResponseModel> call = apiInterface.qrdetails(params);
                call.enqueue(new Callback<QRResponseModel>() {
                    @Override
                    public void onResponse(Call<QRResponseModel> call, retrofit2.Response<QRResponseModel> response) {
                        QRResponseModel qrResponseModel = response.body();
                        if (qrResponseModel != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                            builder.setTitle("Customer Information");
                            builder.setMessage(qrResponseModel.data.customerInfo);
                            builder.show();
                        } else
                        {
                            Toast.makeText(SplashScreen.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<QRResponseModel> call, Throwable t) {

                        Toast.makeText(SplashScreen.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SplashScreen.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();
                binding.llinternet.setVisibility(View.VISIBLE);
                binding.net.setVisibility(View.GONE);

            }
        }
        catch (Exception e) {
            Toast.makeText(SplashScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
 public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
         //   imei1 = manager.getDeviceId();
           imei1 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else if (manager.getPhoneCount() == 2) {
         //   imei1 = manager.getDeviceId(0);
           // imei2 = manager.getDeviceId(1);
            imei1 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            imei2 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }
    public void SendImei(){
    serviceCall();
    }
    public void serviceCall(){
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                binding.pbLoading.setVisibility(View.VISIBLE);
                binding.llinternet.setVisibility(View.GONE);
                binding.net.setVisibility(View.VISIBLE);
                HashMap<String, String> params = new HashMap<>();
                params.put("imeI1", imei1);
                params.put("imeI2", imei2);
                Call<ImeiModel> call = apiInterface.imeisend(params);
                call.enqueue(new Callback<ImeiModel>() {
                    @Override
                    public void onResponse(Call<ImeiModel> call, retrofit2.Response<ImeiModel> response) {
                        ImeiModel imeiresponse = response.body();
                        binding.pbLoading.setVisibility(View.GONE);

                        if (imeiresponse != null) {

                            if((imeiresponse.data == 0) || (imeiresponse.data == 4) || (imeiresponse.data == 5)){
                                Intent intent = new Intent(SplashScreen.this,MessageActivity.class);
                                intent.putExtra("imeiresponse",imeiresponse);
                                startActivity(intent);
                            } else if (imeiresponse.data == 2) {
                                Intent intent = new Intent(SplashScreen.this, Registration.class);
                                startActivity(intent);
                            } else if (imeiresponse.data == 3) {
                                Intent intent = new Intent(SplashScreen.this,WelcomeActivity.class);
                                startActivity(intent);
                            }
                        } else
                        {
                            Toast.makeText(SplashScreen.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ImeiModel> call, Throwable t) {

                        Toast.makeText(SplashScreen.this,  getResources().getString(R.string.server_error_msg), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SplashScreen.this,  getResources().getString(R.string.no_net_msg), Toast.LENGTH_SHORT).show();
                binding.llinternet.setVisibility(View.VISIBLE);
                binding.net.setVisibility(View.GONE);
            }
        }
        catch (Exception e) {
            Toast.makeText(SplashScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


private void checkPermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        permissionStatus = getSharedPreferences("permissionStatus", this.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.grant_permission));
                builder.setMessage(getString(R.string.permission_camera));
                builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CALLBACK_CONSTANT);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.CAMERA, false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.grant_permission));
                builder.setMessage(getString(R.string.permission_camera));
                builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.CAMERA, true);
            editor.commit();
        } else {

        }
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionStatus = getSharedPreferences("permissionStatus", this.MODE_PRIVATE);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

//            if (allgranted || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
////                input = Utility.getIMEI(SplashscreenActivity.this);
////                if (btnClicked) {
////                    btnGettingStarted.performClick();
////                    btnClicked = false;
////                }
//            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}