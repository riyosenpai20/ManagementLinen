package com.android.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.ResponseData;
import com.android.management_linen.models.ResponseDataRuangan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class STORuanganActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String token, namePIC, namaPerusahaan;
    private int idPerusahaan, roleUser;
    private ApiHelper apiService;
    private GridLayout gridRooms;
    private Map<String, Integer> ruanganMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sto_ruangan);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        gridRooms = findViewById(R.id.grid_rooms);

        // Initialize Retrofit
        String baseUrl = getResources().getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiHelper.class);

        // Get user details
        getUserDetails();
    }

    private void getUserDetails() {
        Call<ResponseData> getUser = apiService.getResponseData("Token " + token, "application/json", "application/json");
        getUser.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    if (responseData != null && !responseData.getData().isEmpty()) {
                        namePIC = responseData.getData().get(0).getName();
                        idPerusahaan = responseData.getData().get(0).getPerusahaan();
                        namaPerusahaan = responseData.getNamaPerusahaan();
                        roleUser = responseData.getRoleUser();
                        
                        // Fetch rooms after getting user details
                        fetchRuangan(idPerusahaan);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(STORuanganActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRuangan(int idPerusahaan) {
        // Don't proceed if idPerusahaan is invalid
        if (idPerusahaan <= 0) {
            Toast.makeText(STORuanganActivity.this, "Invalid hospital ID", Toast.LENGTH_SHORT).show();
            return;
        }
    
        Call<ResponseDataRuangan> getListRuang = apiService.getRuangan(idPerusahaan, "Token " + token, "application/json", "application/json");
        getListRuang.enqueue(new Callback<ResponseDataRuangan>() {
            @Override
            public void onResponse(Call<ResponseDataRuangan> call, Response<ResponseDataRuangan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ResponseDataRuangan.Ruangan> ruanganList = response.body().getData();
                    if (ruanganList != null && !ruanganList.isEmpty()) {
                        displayRooms(ruanganList);
                    } else {
                        Toast.makeText(STORuanganActivity.this, "No rooms available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(STORuanganActivity.this, "Failed to fetch rooms: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
    
            @Override
            public void onFailure(Call<ResponseDataRuangan> call, Throwable t) {
                String errorMessage = t.getMessage();
                if (errorMessage != null && errorMessage.contains("timeout")) {
                    Toast.makeText(STORuanganActivity.this, "Connection timeout when fetching rooms. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(STORuanganActivity.this, "Error fetching rooms: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayRooms(List<ResponseDataRuangan.Ruangan> ruanganList) {
        // Clear existing views
        gridRooms.removeAllViews();
        ruanganMap.clear();
    
        LayoutInflater inflater = LayoutInflater.from(this);
    
        for (ResponseDataRuangan.Ruangan ruang : ruanganList) {
            // Add to map for later reference
            ruanganMap.put(ruang.getNama(), ruang.getId());
    
            // Inflate room card
            View roomView = inflater.inflate(R.layout.item_room_card, gridRooms, false);
            CardView cardRoom = roomView.findViewById(R.id.card_room);
            TextView tvRoomName = roomView.findViewById(R.id.tv_room_name);
    
            // Set room name
            tvRoomName.setText(ruang.getNama());
    
            // Set click listener
            cardRoom.setOnClickListener(v -> {
                // Save room ID and name to shared preferences
                sharedPreferences.edit().putInt("idRuang", ruang.getId()).apply();
                sharedPreferences.edit().putString("namaRuangReport", ruang.getNama()).apply();
                sharedPreferences.edit().putString("pdf_title", "STO").apply();
    
                // Navigate to ScanMode activity
                Intent intent = new Intent(STORuanganActivity.this, MainActivity.class);
                startActivity(intent);
            });
    
            // Add to grid with improved spacing
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            params.setMargins(8, 8, 8, 8); // Add margins around each card
            roomView.setLayoutParams(params);
            gridRooms.addView(roomView);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}