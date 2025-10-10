package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.ResponseData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView tvWelcome;
    private String token, username;
    private ApiHelper apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Initialize shared preferences
        sharedPreferences = getSharedPreferences(com.example.management_linen.LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        
        // Cek jika token null, redirect ke LoginActivity
        if (token == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        // Initialize views
        tvWelcome = findViewById(R.id.tv_welcome);
        
        // Toolbar is already provided by the theme
        // No need to call setSupportActionBar()
        
        // Get user details from API
        getUserDetails();
        
        // Set up click listeners for menu cards
        CardView cardSto = findViewById(R.id.card_sto);
        CardView cardSearch = findViewById(R.id.card_search);
        
        cardSto.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("fromSTORuangan", true).apply();
            sharedPreferences.edit().putBoolean("fromSearchCard", false).apply();
            // Navigate to STORuanganActivity instead of TestIntent
            Intent intent = new Intent(HomeActivity.this, STORuanganActivity.class);
            startActivity(intent);
        });
        
        cardSearch.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("fromSTORuangan", false).apply();
            sharedPreferences.edit().putBoolean("fromSearchCard", true).apply();
            // Navigate to Search activity
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
    
    private void getUserDetails() {
        // Token sudah dipastikan tidak null di onCreate
        String baseUrl = getResources().getString(R.string.BASE_URL);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiHelper.class);
        
        // Get User Detail
        Call<ResponseData> getUser = apiService.getResponseData("Token " + token, "application/json", "application/json");
        getUser.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    System.out.println(responseData);
                    if (responseData != null && !responseData.getData().isEmpty()) {
                        username = responseData.getData().get(0).getName();
                        tvWelcome.setText("Selamat Datang, " + username + "!");

                        sharedPreferences.edit().putString("namePIC", username).apply();
                        sharedPreferences.edit().putInt("idPerusahaan", responseData.getData().get(0).getPerusahaan()).apply();
                        sharedPreferences.edit().putString("namaPerusahaan", responseData.getNamaPerusahaan()).apply();
                        sharedPreferences.edit().putInt("roleUser", responseData.getRoleUser()).apply();
                        sharedPreferences.edit().putInt("type_rs", responseData.getTypeRs()).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu dengan item Logout
        menu.add(Menu.NONE, 1, Menu.NONE, "Logout");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == 1) {
            // Logout: hapus token dan data user dari SharedPreferences
            sharedPreferences.edit().clear().apply();
            
            // Redirect ke LoginActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}