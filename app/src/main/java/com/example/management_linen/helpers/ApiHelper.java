package com.example.management_linen.helpers;

import com.example.management_linen.models.*;
import com.example.management_linen.models.ResponseInfo;
import com.example.management_linen.requests.LogInRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiHelper {
    @POST("api/login/")
    Call<LogIn> logIn(@Body LogInRequest request);

    @POST("api/android/inventory/")
    Call<Void> inventory(
            @Body List<String> request,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @POST("api/android/inventory_detail/")
    Call<List<Details>> inventory_detail(
            @Body List<String> hardcodedData,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @GET("api/user/")
    Call<ResponseData> getResponseData(
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @GET("api/ruangan/{id}")
    Call<ResponseDataRuangan> getRuangan(
            @Path("id") int idPerusahaan,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @POST("api/android/scan_sto/")
    Call<ResponseScanSTO> scan_sto(
            @Body ScanSTOResponse scanSTOResponse,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @POST("api/android/search_unknown_linen/")
    Call<ResponseUnknownLinen> search_unknown_linen(
            @Body RfidRequest rfidRequest,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );

    @POST("api/android/linen_room_summary/")
    Call<ResponseInfo> search_info(
            @Body RfidRequest rfidRequest,
            @Header ("Authorization") String token,
            @Header ("Content-Type") String contentType,
            @Header ("Accept") String accept
    );
}