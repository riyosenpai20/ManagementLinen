package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.ResponseData;
import com.example.management_linen.models.ResponseDataRuangan;

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
    private boolean fromSTORuangan = false;
    private boolean fromSearchCard = false;
    private boolean fromRuanganActivity = false;

    private boolean fromSearchCardUnknown = false;
    private boolean fromSearchCardNotReturn = false;
    private boolean fromSearchCardInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sto_ruangan);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        fromSTORuangan = sharedPreferences.getBoolean("fromSTORuangan", false);
        fromSearchCard = sharedPreferences.getBoolean("fromSearchCard", false);
        fromRuanganActivity = sharedPreferences.getBoolean("fromRuanganActivity", false);
        fromSearchCardUnknown = sharedPreferences.getBoolean("fromSearchCardUnknown", false);
        fromSearchCardNotReturn = sharedPreferences.getBoolean("fromSearchCardNotReturn", false);
        fromSearchCardInfo = sharedPreferences.getBoolean("fromSearchCardInfo", false);

        System.out.println("STORuangan fromStoRuangan: " + fromSTORuangan);
        System.out.println("STORuangan fromSearchCard: " + fromSearchCard);
        System.out.println("STORuangan fromRuanganActivity: " + fromRuanganActivity);
        System.out.println("STORuangan fromSearchCardUnknown: " + fromSearchCardUnknown);
        System.out.println("STORuangan fromSearchCardNotReturn: " + fromSearchCardNotReturn);
        System.out.println("STORuangan fromSearchCardInfo: " + fromSearchCardInfo);


        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Set up ActionBar with title and back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("STO");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Initialize views
        gridRooms = findViewById(R.id.grid_rooms);

        // Setup card_laundry click listener to open MainActivity (same behavior as room cards)
        CardView cardLaundry = findViewById(R.id.card_laundry);
        if (cardLaundry != null) {
            cardLaundry.setOnClickListener(v -> {
                // Optional: keep pdf title consistent with room card behavior
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putString("pdf_title", "STO").apply();
                    sharedPreferences.edit().putString("namaRuangReport", "laundry").apply();
                    sharedPreferences.edit().putString("jenisSTO", "laundry").apply();
                }
                Intent intent = new Intent(STORuanganActivity.this, MainActivity.class);
                startActivity(intent);
            });
        }

        // Setup card_inventory click listener to open MainActivity (same behavior as laundry, but jenisSTO = inventory)
        CardView cardInventory = findViewById(R.id.card_inventory);
        if (cardInventory != null) {
            cardInventory.setOnClickListener(v -> {
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putString("pdf_title", "STO").apply();
                    sharedPreferences.edit().putString("namaRuangReport", "inventory").apply();
                    sharedPreferences.edit().putString("jenisSTO", "inventory").apply();
                }
                Intent intent = new Intent(STORuanganActivity.this, MainActivity.class);
                startActivity(intent);
            });
        }

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
                sharedPreferences.edit().putString("jenisSTO", "").apply();
                // Set flag to indicate MainActivity is opened from STORuanganActivity
                //sharedPreferences.edit().putBoolean("fromSTORuangan", true).apply();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu with logout option
        menu.add(Menu.NONE, 1, Menu.NONE, "Logout")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle back button click
            onBackPressed();
            return true;
        } else if (id == 1) {
            // Handle logout
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Metode ini digunakan untuk menangani tombol back
    // Kita tetap menggunakan onBackPressed() karena masih didukung oleh versi Android yang digunakan
    // dan sudah digunakan di onOptionsItemSelected() untuk tombol back di ActionBar
    @Override
    public void onBackPressed() {
        // Navigasi ke HomeActivity
        if(fromSTORuangan) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }else if (fromSearchCard) {
//            if(fromRuanganActivity) {
//                Intent intent = new Intent(this, RuanganActivity.class);
//                startActivity(intent);
//            }
//            else {
//                Intent intent = new Intent(this, RuanganActivity.class);
//                startActivity(intent);
//            }
            if(fromSearchCardUnknown) {
                Intent intent = new Intent(this, RuanganActivity.class);
                startActivity(intent);
            }
            else if (fromSearchCardNotReturn){
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
        }
        finish();
        // Animasi transisi
        if (getResources().getIdentifier("slide_in_left", "anim", getPackageName()) != 0 && 
            getResources().getIdentifier("slide_out_right", "anim", getPackageName()) != 0) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reset the flag when returning to this activity
        if (sharedPreferences != null) {
            if(fromSTORuangan) {
                sharedPreferences.edit().putBoolean("fromSTORuangan", true).apply();
                sharedPreferences.edit().putBoolean("fromRuanganActivity", false).apply();
                sharedPreferences.edit().putBoolean("fromSearchCard", false).apply();
            } else {
                sharedPreferences.edit().putBoolean("fromSTORuangan", false).apply();
                sharedPreferences.edit().putBoolean("fromSearchCard", true).apply();
            }
        }
    }
}