package com.app.mahindrafinancemfact.activities;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.databinding.ActivitySplashScreenBinding;
import com.app.mahindrafinancemfact.models.ImeiModel;
import com.app.mahindrafinancemfact.models.QRResponseModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
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
    private ActivitySplashScreenBinding binding;
    private ApiInterface apiInterface;
    Context context;
    String imei1;
    String imei2;
    String qrResponse;
    private boolean isClickable = true;
    boolean btnpressstarted = false;
    boolean btnpressqr = false;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
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
    private final Runnable mShowPart2Runnable = () -> {
        // Delayed display of UI elements
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mVisible = true;
        mContentView = binding.fullscreenContent;
        mContentView.setOnClickListener(view -> toggle());
        init();
        setView();
    }

    /**
     * initialize apiinterface
     * */
    public void init() {
        try {
            context = this;
            apiInterface = ApiClient.getClient(context).create(ApiInterface.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * binding the clicklisteners
     * */
    public void setView() {
        binding.btnGettingStarted.setOnClickListener(this::onClick);
        binding.btnQrDetails.setOnClickListener(this::onClick);
        binding.btnRetry.setOnClickListener(this::onClick);
        checkPermission();
    }
    public void onClick(View v) {
        int id = v.getId();
        long CLICK_DELAY = 1000;
        if (id == R.id.btnGettingStarted) {
            if (isClickable) {
                btnpressstarted = true;
                btnpressqr = false;
                getImei();
                sendImei();
                isClickable = false;
                Handler handler = new Handler();
                handler.postDelayed(() -> isClickable = true, CLICK_DELAY);
            }

        } else if (id == R.id.btnQrDetails) {
            if (isClickable) {
                btnpressstarted = false;
                btnpressqr = true;
                scanCode();
                isClickable = false;
                Handler handler = new Handler();
                handler.postDelayed(() -> isClickable = true, CLICK_DELAY);
            }

        } else if (id == R.id.btnRetry) {
            if(btnpressstarted){
                serviceCall();
            } else if (btnpressqr) {
                qrservicecall();
            }
        }
    }

    /**
     * QR Scan
     * */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.volume_up_to_flash_on));
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivity.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{
        if(result.getContents() != null)
        {
            qrResponse = result.getContents();
            if(qrResponse.length() == 30){
                qrservicecall();
            }else{
                Toast.makeText(context, R.string.invalid_qr_code,LENGTH_SHORT).show();
            }
        }
    });

    /**
     * passing the string received after scanning qr and bearer token as params and receive customer information
     * */
    private void qrservicecall() {
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                showMsgView(View.VISIBLE,View.GONE,View.GONE);
                SharedPreferences tok = getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                String params = qrResponse;
                Call<QRResponseModel> call = apiInterface.qrdetails(params,"Bearer " + token);
                call.enqueue(new Callback<QRResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<QRResponseModel> call, @NonNull retrofit2.Response<QRResponseModel> response) {
                        QRResponseModel qrResponseModel = response.body();
                        if (qrResponseModel != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                            builder.setTitle(R.string.customer_information);
                            builder.setMessage(qrResponseModel.data.customerInfo);
                            builder.show();
                        } else
                        {
                            showMsgView(View.GONE,View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<QRResponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.GONE,View.VISIBLE,View.GONE);
            }
        }
        catch (Exception e) {
            Toast.makeText(SplashScreen.this, e.getMessage(), LENGTH_SHORT).show();
        }
    }
 @SuppressLint("HardwareIds")
 public void getImei(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneCount() == 1){
           imei1 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else if (manager.getPhoneCount() == 2) {
            imei1 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            imei2 =  Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
     SharedPreferences sharedPreferences = getSharedPreferences("IMEI", MODE_PRIVATE);
     SharedPreferences.Editor myEdit = sharedPreferences.edit();
     myEdit.putString("imei", imei1);
     myEdit.apply();
    }
    public void sendImei(){
    serviceCall();
    }
    public void serviceCall(){
        try {
            if (UtilityMethods.isConnectingToInternet(context)) {
                binding.pbLoading.setVisibility(View.VISIBLE);
                showMsgView(View.VISIBLE,View.GONE,View.GONE);
                HashMap<String, String> params = new HashMap<>();
                params.put("imeI1", imei1);
                params.put("imeI2", imei2);
                Call<ImeiModel> call = apiInterface.imeisend(params);
                call.enqueue(new Callback<ImeiModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ImeiModel> call, @NonNull retrofit2.Response<ImeiModel> response) {
                        ImeiModel imeiresponse = response.body();
                        binding.pbLoading.setVisibility(View.GONE);
                        if (imeiresponse != null) {
                            if((imeiresponse.data.data == 0) || (imeiresponse.data.data == 4) || (imeiresponse.data.data == 5)){
                                Intent intent = new Intent(SplashScreen.this,MessageActivity.class);
                                intent.putExtra("imeiresponse",imeiresponse.data.data);
                                startActivity(intent);
                            } else if (imeiresponse.data.data == 2) {
                                Intent intent = new Intent(SplashScreen.this, Registration.class);
                                startActivity(intent);
                            } else if (imeiresponse.data.data == 3) {
                                String token = imeiresponse.data.token;
                                SharedPreferences sharedPreferences = getSharedPreferences("Token", MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString("token", token);
                                myEdit.apply();
                                Intent intent = new Intent(SplashScreen.this, ProfileActivity.class);
                                startActivity(intent);
                            }
                        } else
                        {
                            showMsgView(View.VISIBLE,View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ImeiModel> call, @NonNull Throwable t) {
                        showMsgView(View.VISIBLE,View.GONE,View.VISIBLE);
                    }
                });
            } else {
                showMsgView(View.GONE,View.VISIBLE,View.GONE);
            }
        }
        catch (Exception e) {
            Toast.makeText(SplashScreen.this, e.getMessage(), LENGTH_SHORT).show();
        }

    }
private void checkPermission() {
    permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.grant_permission));
            builder.setMessage(getString(R.string.permission_camera));
            builder.setPositiveButton(R.string.grant, (dialog, which) -> {
                dialog.cancel();
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CALLBACK_CONSTANT);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
        } else if (permissionStatus.getBoolean(Manifest.permission.CAMERA, false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.grant_permission));
            builder.setMessage(getString(R.string.permission_camera));
            builder.setPositiveButton(R.string.grant, (dialog, which) -> {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CALLBACK_CONSTANT);
        }
        SharedPreferences.Editor editor = permissionStatus.edit();
        editor.putBoolean(Manifest.permission.CAMERA, true);
        editor.apply();
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                } else {
                    break;
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide();
    }
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }
    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 100);
    }
    void showMsgView(int containerVisibility, int nointernetvisibillity, int srvrerr) {
        try {
            binding.llinternet.setVisibility(nointernetvisibillity);
            binding.net.setVisibility(containerVisibility);
            binding.servererror.setVisibility(srvrerr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}