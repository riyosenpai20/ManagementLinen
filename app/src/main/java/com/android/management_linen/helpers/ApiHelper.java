package com.android.management_linen.helpers;

import com.android.management_linen.models.Details;
import com.android.management_linen.models.DetailsScanBersih;
import com.android.management_linen.models.LogIn;
import com.android.management_linen.models.ResponseData;
import com.android.management_linen.models.ResponseDataRuangan;
import com.android.management_linen.models.ResponseScanBersih;
import com.android.management_linen.models.ResponseScanSTO;
import com.android.management_linen.models.ScanBersihResponse;
import com.android.management_linen.models.ScanSTOResponse;
import com.android.management_linen.requests.LogInRequest;

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

    @POST("api/android/inventory_detail_scan/")
    Call<List<DetailsScanBersih>> inventory_detail_scan(
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

    @POST("api/android/scan_bersih/")
    Call<ResponseScanBersih> scan_bersih(
            @Body ScanBersihResponse scanBersihResponse,
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

}
