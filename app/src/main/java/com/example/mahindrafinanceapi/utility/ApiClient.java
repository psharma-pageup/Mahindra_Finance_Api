package com.example.mahindrafinanceapi.utility;

import android.content.Context;
import android.util.Log;


import com.example.mahindrafinanceapi.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = "http://192.168.29.250:8000/api/";
    public static String BASE_IMAGE_URL = "http://192.168.29.250:8000/";
    public static String BASE_PRODUCT_IMAGE_URL = BASE_IMAGE_URL + "";


    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);


//        clientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                Request updatedRequest = request.newBuilder().addHeader("lang", SharedPreferencesMethod.getDefaultLanguage(context)).addHeader("Authorization", SharedPreferencesMethod.getAuthToken(context)).addHeader("id", SharedPreferencesMethod.getUserId(context)).build();
//                Log.e("restrofit intercept",updatedRequest.toString());
//                return chain.proceed(updatedRequest);
//            }
//        });


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
