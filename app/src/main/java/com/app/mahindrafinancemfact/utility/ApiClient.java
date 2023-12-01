package com.app.mahindrafinancemfact.utility;

import android.content.Context;
import android.util.Log;


import com.app.mahindrafinancemfact.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = "https://bams-mahindra.azurewebsites.net/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(interceptor);
        }
        OkHttpClient client = clientBuilder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        Log.e("restrofit ",retrofit.toString());
        return retrofit;
    }
}
