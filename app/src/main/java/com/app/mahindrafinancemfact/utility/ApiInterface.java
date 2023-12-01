package com.app.mahindrafinancemfact.utility;

import com.app.mahindrafinancemfact.models.AsseiInfoModel;
import com.app.mahindrafinancemfact.models.AssetBranchListResponseModel;
import com.app.mahindrafinancemfact.models.AssetObjectResponseModel;
import com.app.mahindrafinancemfact.models.AssetinforesponseModel;
import com.app.mahindrafinancemfact.models.BranchResponseModel;
import com.app.mahindrafinancemfact.models.FormModel;
import com.app.mahindrafinancemfact.models.ImeiModel;
import com.app.mahindrafinancemfact.models.ImeiObjectModel;
import com.app.mahindrafinancemfact.models.ProfileObjectModel;
import com.app.mahindrafinancemfact.models.QRResponseModel;
import com.app.mahindrafinancemfact.models.RegistrationModel;

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
import retrofit2.http.Query;

public interface ApiInterface {

@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/Asset/CustomerInfo")
Call<ProfileObjectModel> getProfile(@FieldMap HashMap<String, String> params,@Header("Authorization") String token);
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
Call<ImeiModel>Resend_request(@FieldMap HashMap<String, String> params,@Header("Authorization") String token);
@Headers("Accept:application/json")
@FormUrlEncoded
@POST("api/QR/Detail")
Call<QRResponseModel> qrdetails (@Field("QRCode")String params,@Header("Authorization") String token );
@Headers("Accept:application/json")
@FormUrlEncoded
@POST("api/Asset/Branch")
Call<BranchResponseModel> branchdetails (@Field("empCode")String params,@Header("Authorization") String token );
@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/Asset/AssetList")
Call<AssetObjectResponseModel>Asset_request(@FieldMap HashMap<String, String> params,@Header("Authorization") String token);
@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/ItemCheck")
Call<ImeiObjectModel>Final_request(@FieldMap HashMap<String, String> params, @Header("Authorization") String token);
@Headers("Accept: application/json")
@GET("api/Asset/AssetInformation")
Call<AssetinforesponseModel> assetinfo (@Query("hocode") String hocode, @Header("Authorization") String token);
@Headers("Accept: application/json")
@GET("api/Asset/BranchList")
Call<AssetBranchListResponseModel> assetbranch (@Query("aid") String aid, @Header("Authorization") String token);
@Headers("Accept: application/json")
@GET("api/Asset/Logout")
Call<ProfileObjectModel> logout (@Query("imei1") String imei, @Header("Authorization") String token);
@Headers("Accept: application/json")
@FormUrlEncoded
@POST("api/AuditRegistration/FormFill")
Call<FormModel> Form(@FieldMap HashMap<String, String> params, @Header("Authorization") String token);
}

