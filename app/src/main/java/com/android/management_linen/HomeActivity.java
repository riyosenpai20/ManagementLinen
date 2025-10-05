package com.android.management_linen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.ResponseData;

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
        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        
        // Initialize views
        tvWelcome = findViewById(R.id.tv_welcome);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get user details from API
        getUserDetails();
        
        // Set up click listeners for menu cards
        CardView cardSto = findViewById(R.id.card_sto);
        CardView cardSearch = findViewById(R.id.card_search);
        
        cardSto.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("fromSTORuangan", true).apply();
            // Navigate to STORuanganActivity instead of TestIntent
            Intent intent = new Intent(HomeActivity.this, STORuanganActivity.class);
            startActivity(intent);
        });
        
        cardSearch.setOnClickListener(v -> {
            sharedPreferences.edit().putBoolean("fromSearchCard", true).apply();
            // Navigate to Search activity
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
    
    private void getUserDetails() {
        if (token == null) {
            return;
        }
        
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
                    if (responseData != null && !responseData.getData().isEmpty()) {
                        username = responseData.getData().get(0).getName();
                        tvWelcome.setText("Selamat Datang, " + username + "!");
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
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}