package com.example.baseproject.utility;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilityMethods {

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo state : info) {
                    if (state.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
