package com.example.mahindrafinanceapi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.example.mahindrafinanceapi.Barcode.Barcode.BarcodeReader;
import com.example.mahindrafinanceapi.GPS.GPSTracker;
import com.example.mahindrafinanceapi.R;
import com.example.mahindrafinanceapi.databinding.ActivityScanBinding;
import com.example.mahindrafinanceapi.utility.Const;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener, BarcodeReader.BarcodeReaderListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private ActivityScanBinding activityScanBinding;
    private BarcodeReader barcodeReader;
    boolean scanAvail = false;
    double latitude;
    double longitude;
    ProgressDialog progressSendingRequest;
    protected LocationRequest locationRequest;
    protected GoogleApiClient mGoogleApiClient;
    int REQUEST_CHECK_SETTINGS = 100;
    ProgressDialog progressDialog;
    Handler handler;
    Runnable runnable;
    GPSTracker gps;
    int isBackClickedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityScanBinding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(activityScanBinding.getRoot());

        init();
        setClickListener();

    }
    public void init(){
        barcodeReader = (BarcodeReader) getSupportFragmentManager ().findFragmentById (R.id.barcode_fragment);
        scanAvail = false;
        DisplayMetrics displayMetrics = getResources ().getDisplayMetrics ();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        activityScanBinding.soView.setRectHeight ((int) ( dpWidth / 2 ));
        activityScanBinding.soView.setRectWidth ((int) ( dpWidth / 2 ));
        getWindow ().setBackgroundDrawableResource (R.drawable.bg);
        Const.scanActivity = this;
    }
    private void setClickListener () {
        activityScanBinding.btnConclude.setOnClickListener (this::onClick);
        activityScanBinding.btnScan.setOnClickListener (this::onClick);
    }

    public void onClick (View v) {
        int id = v.getId();

            if(id ==  R.id.btnConclude) {
                startActivity(new Intent(this, AssetListScreenActivity.class));
            }
            else if(id ==  R.id.btnScan){
                activityScanBinding.pbLoadingScan.setVisibility (View.VISIBLE);
                activityScanBinding.btnScan.setText ("");
                scanAvail = true;
                activityScanBinding.soView.setLineWidth (4);
                activityScanBinding.btnScan.setEnabled (false);

                }

    }


    @Override
    public void onScanned (final Barcode barcode) {
        if (scanAvail && latitude != 0 && longitude != 0) {
            scanAvail = false;
            barcodeReader.playBeep ();
            runOnUiThread (new Runnable () {
                @Override
                public void run () {
                    if (barcode.displayValue.length () == 30) {
                        //Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
                        Log.e ("TAG", "" + barcode.displayValue.length ());
                        barcodeReader.onPause ();
                        activityScanBinding.btnScan.setEnabled (false);
                        activityScanBinding.pbLoadingScan.setVisibility (View.GONE);
                        activityScanBinding.btnScan.setText (getResources ().getString (R.string.scan_qr_code_heading));
                        progressSendingRequest = new ProgressDialog(ScanActivity.this, R.style.AlertDialogTheme);
                        progressSendingRequest.setMessage (getResources ().getString (R.string.gettting_details_msg));
                        progressSendingRequest.setCancelable (false);
                        progressSendingRequest.show ();
                        CheckQrCode ();
                        activityScanBinding.soView.setLineWidth (0);

                    } else {
                        scanAvail = true;
                        Toast.makeText (getApplicationContext (), getResources ().getString (R.string.wrong_qr_code_scanned_msg), Toast.LENGTH_SHORT).show ();
                    }
                }
            });
        } else {
            runOnUiThread (new Runnable () {
                @Override
                public void run () {
                    activityScanBinding.btnScan.setEnabled (true);
                    activityScanBinding.pbLoadingScan.setVisibility (View.GONE);
                    activityScanBinding.btnScan.setText (getResources ().getString (R.string.scan_qr_code_heading));
                    scanAvail = false;
                }
            });
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied () {
        Toast.makeText (getApplicationContext (), "Permission denied!", Toast.LENGTH_LONG).show ();
        finish ();
    }

    @Override
    public void getLocation() {

    }

    private void CheckQrCode() {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder ()
                .addLocationRequest (locationRequest);
        builder.setAlwaysShow (true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings (
                        mGoogleApiClient,
                        builder.build ()
                );

        result.setResultCallback (this);

    }

    @Override
    public void onConnectionSuspended(@NonNull int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus ();
        switch (status.getStatusCode ()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult (this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }

    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                Toast.makeText (getApplicationContext (), "GPS enabled", Toast.LENGTH_LONG).show ();
                Log.e ("TAG", "..........GPS enabled..............");

                //TODO Important When Gps Enable Just Now Need Some Time ..........otherwise lat long 0.0 fetch
                // TODO Need Some time to call GPS Tracker Service


                locationEnabled_or_Not ();

            } else {
                callLocationDialog ();
                Log.e ("TAG", "..........GPS not enabled..............");
                Toast.makeText (getApplicationContext (), "GPS is not enabled", Toast.LENGTH_LONG).show ();
            }

        }
    }

    private void callLocationDialog() {
        Log.e ("TAG", "callLocationDialog Step 3 Popup called ...........");
        //barcodeReader.pauseScanning();
        activityScanBinding.btnScan.setEnabled (false);
        activityScanBinding.pbLoadingScan.setVisibility (View.GONE);
        activityScanBinding.btnScan.setText (getResources ().getString (R.string.scan_qr_code_heading));
        activityScanBinding.soView.setLineWidth (0);
        scanAvail = false;
        mGoogleApiClient = new GoogleApiClient.Builder (this)
                .addApi (LocationServices.API)
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener (this).build ();
        mGoogleApiClient.connect ();

        locationRequest = LocationRequest.create ();
        locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval (30 * 1000);
        locationRequest.setFastestInterval (5 * 1000);
    }

    private void locationEnabled_or_Not() {
        Log.e ("TAG", "locationEnabled_or_Not Step 2 Pass...........");
        final LocationManager manager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        if (! manager.isProviderEnabled (LocationManager.GPS_PROVIDER) && hasGPSDevice (this)) {
            Log.e ("TAG", "Gps not enabled");
            callLocationDialog ();
        } else {


            progressDialog = new ProgressDialog (this, R.style.AlertDialogTheme);
            progressDialog.setMessage (getResources ().getString (R.string.getting_location_msg));
            progressDialog.setCancelable (false);
            progressDialog.show ();


            Log.e ("TAG", "Gps already enabled");
            handler = new Handler();

            runnable = new Runnable () {
                @Override
                public void run () {
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            callLocationGpsTracker ();           // TODO When Gps already enabled call direct GPS Tracker
                            handler.postDelayed (runnable, 1000);
                        }
                    });
                }
            };
            handler.post (runnable);
        }
    }

    private void callLocationGpsTracker() {
        gps = new GPSTracker(this);

        // check if GPS enabled
        if (gps.canGetLocation ()) {

            latitude = gps.getLatitude ();
            longitude = gps.getLongitude ();


            if (longitude != 0.0 && progressDialog != null) {
                //barcodeReader.resumeScanning();
                activityScanBinding.btnScan.setEnabled (true);
                progressDialog.dismiss ();
                progressDialog = null;
            }

            /*Log.e("MainActivity", "latitude -> " + latitude);
            Log.e("MainActivity", "longitude -> " + longitude);*/


        } else {
            // TODO can't get location
            // TODO GPS or Network is not enabled
            // TODO Ask user to enable GPS/network in settings

            //TODO need again call Locaton Dialog
            if (progressDialog != null) {
                progressDialog.dismiss ();
                progressDialog = null;
            }
            handler.removeCallbacksAndMessages (null);
            runnable = null;
            callLocationDialog ();
        }
    }

    public boolean hasGPSDevice (Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService (Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List <String> providers = mgr.getAllProviders ();
        if (providers == null)
            return false;
        return providers.contains (LocationManager.GPS_PROVIDER);
    }
    @Override
    protected void onDestroy () {
        super.onDestroy ();
        if (handler != null) {
            handler.removeCallbacksAndMessages (null);
            runnable = null;
        }
    }

    @Override
    public void onBackPressed () {
        isBackClickedCount++;
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run () {
                isBackClickedCount = 0;
            }
        }, 4000);
        if (isBackClickedCount >= 2) {
            super.onBackPressed ();
        } else {
            Toast.makeText (this, getResources ().getString (R.string.press_back_again_msg), Toast.LENGTH_SHORT).show ();
        }
    }
}