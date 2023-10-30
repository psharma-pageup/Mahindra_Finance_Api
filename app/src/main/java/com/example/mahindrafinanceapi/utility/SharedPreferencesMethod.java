package com.example.mahindrafinanceapi.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesMethod {
    private static String PREFERENCE = "mahindra_finance_preferences";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static String getauthtoken(Context context){
        return getString(context,AUTH_TOKEN);
    }
    public static String getString(Context context, String name) {
        SharedPreferences sharedPreferences = getSharedPreference(context);
        return sharedPreferences.getString(name, "");
    }
    public static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences;
    }

    public static void setAuthToken(Context context, String token){
        setString(context, AUTH_TOKEN, token);
    }
    public static void setString(Context context, String name, String value) {


        SharedPreferences.Editor myEdit = getSharedPreference(context).edit();


        myEdit.putString(name,value);


        myEdit.commit();
    }
    public static String getAuthToken(Context context){
        return getString(context,AUTH_TOKEN);
    }

   }


