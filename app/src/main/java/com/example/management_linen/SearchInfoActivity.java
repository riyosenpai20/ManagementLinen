package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.management_linen.helpers.ApiClient;
import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.DetailScanInfo;
import com.example.management_linen.models.ResponseInfo;
import com.example.management_linen.models.RfidRequest;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchInfoActivity extends AppCompatActivity {

    private CardView cardSummary, cardDetail;
    private List<String> tagList;
    private String namaRuang;
    private ProgressBar progressBar;
    
    // API dan SharedPreferences
    private ApiHelper apiHelper;
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private String token;
    private int idRuang;
    
    // Response data
    private ResponseInfo responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);

        // Inisialisasi views
        cardSummary = findViewById(R.id.card_summary);
        cardDetail = findViewById(R.id.card_detail);
        progressBar = findViewById(R.id.progressBar);
        
        // Inisialisasi SharedPreferences
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        idRuang = sharedpreferences.getInt("idRuang", 0);

        // Inisialisasi API
        apiHelper = ApiClient.getClient(this).create(ApiHelper.class);

        // Mendapatkan data dari intent
        Intent intent = getIntent();
        if (intent != null) {
            tagList = (List<String>) intent.getSerializableExtra("tagList");
            namaRuang = intent.getStringExtra("namaRuang");
            
            // Kirim data ke API saat activity dibuka
            if (tagList != null && !tagList.isEmpty()) {
                kirimDataKeServer();
            } else {
                Toast.makeText(this, "Tidak ada data tag yang ditemukan", Toast.LENGTH_SHORT).show();
            }
        }

        // Set listener untuk card Summary
        cardSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseData != null) {
                    // Buka activity untuk menampilkan summary dengan data response
                    Intent summaryIntent = new Intent(SearchInfoActivity.this, PreviewSearchInfoActivity.class);
                    summaryIntent.putExtra("responseData", (Serializable) responseData);
                    summaryIntent.putExtra("namaRuang", namaRuang);
                    summaryIntent.putExtra("mode", "summary");
                    startActivity(summaryIntent);
                } else {
                    Toast.makeText(SearchInfoActivity.this, "Data belum tersedia, silakan tunggu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set listener untuk card Detail
        cardDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseData != null) {
                    // Buka activity untuk menampilkan detail dengan data response
                    Intent detailIntent = new Intent(SearchInfoActivity.this, PreviewSearchActivity.class);
                    detailIntent.putExtra("responseData", (Serializable) responseData);
                    detailIntent.putExtra("namaRuang", namaRuang);
                    detailIntent.putExtra("mode", "detail");
                    startActivity(detailIntent);
                } else {
                    Toast.makeText(SearchInfoActivity.this, "Data belum tersedia, silakan tunggu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void kirimDataKeServer() {
        if (tagList == null || tagList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk dikirim", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Tampilkan loading
        progressBar.setVisibility(View.VISIBLE);
        cardSummary.setEnabled(false);
        cardDetail.setEnabled(false);
        
        // Memanggil API untuk mengirim data
        RfidRequest rfidRequest = new RfidRequest(tagList, "search", null);
        Call<ResponseInfo> call = apiHelper.search_info(
                rfidRequest,
                "Token " + token,
                "application/json",
                "application/json"
        );
        
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                progressBar.setVisibility(View.GONE);
                cardSummary.setEnabled(true);
                cardDetail.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    responseData = response.body();
                    Toast.makeText(SearchInfoActivity.this, "Data berhasil diambil", Toast.LENGTH_SHORT).show();
                    
                    List<DetailScanInfo> items = responseData.getData();
                    if (items == null || items.isEmpty()) {
                        Toast.makeText(SearchInfoActivity.this, "Tidak ada data yang ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchInfoActivity.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                cardSummary.setEnabled(true);
                cardDetail.setEnabled(true);
                Toast.makeText(SearchInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}