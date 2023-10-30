package com.example.mahindrafinanceapi.utility;

import com.example.mahindrafinanceapi.models.AssetObjectResponseModel;
import com.example.mahindrafinanceapi.models.BranchResponseModel;
import com.example.mahindrafinanceapi.models.GenericResponseModel;
import com.example.mahindrafinanceapi.models.ImeiModel;
import com.example.mahindrafinanceapi.models.ProfileObjectModel;
import com.example.mahindrafinanceapi.models.QRResponseModel;
import com.example.mahindrafinanceapi.models.ProfileModal;
import com.example.mahindrafinanceapi.models.QRResponseObjectModel;
import com.example.mahindrafinanceapi.models.RegistrationModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("otp_login")
Call<QRResponseModel> login_Service (@FieldMap HashMap<String, String> params, @Header("Authorization") String token);

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/Asset/CustomerInfo")
Call<ProfileObjectModel> getProfile(@FieldMap HashMap<String, String> params);

@Headers("Accept:application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/Check")
Call<ImeiModel> imeisend (@FieldMap HashMap<String, String> params);

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/IMEI")
Call<RegistrationModel> Register_Service (@FieldMap HashMap<String, String> params);

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/Resend")
Call<ImeiModel>Resend_request(@FieldMap HashMap<String, String> params);

@Headers("Accept:application/json")
@FormUrlEncoded
@POST("api/QR/Detail")
Call<QRResponseModel> qrdetails (@Field("QRCode")String params );

@Headers("Accept:application/json")
@FormUrlEncoded
@POST("api/Asset/Branch")
Call<BranchResponseModel> branchdetails (@Field("empCode")String params );

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/Asset/AssetList")
Call<AssetObjectResponseModel>Asset_request(@FieldMap HashMap<String, String> params);

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/ItemCheck")
Call<ImeiModel>Final_request(@FieldMap HashMap<String, String> params);
}

