package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SearchActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean fromSTORuangan = false;
    private boolean fromSearchCard = false;
    private boolean fromSearchCardUnknown = false;
    private boolean fromSearchCardNotReturn = false;
    private boolean fromSearchCardInfo = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Toolbar setup removed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search");
        }
        
        // Setup click listeners
        CardView cardTidakDiketahui = findViewById(R.id.card_tidak_diketahui);
        CardView cardBelumKembali = findViewById(R.id.card_belum_kembali);
        CardView cardInformasiLinen = findViewById(R.id.card_informasi_linen);

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        fromSTORuangan = sharedPreferences.getBoolean("fromSTORuangan", false);
        fromSearchCard = sharedPreferences.getBoolean("fromSearchCard", false);

        cardTidakDiketahui.setOnClickListener(v -> {
            // System.out.println("SearchActivity fromSTORuangan: " + fromSTORuangan);
            // System.out.println("SearchActivity fromSearchCard: " + fromSearchCard);
            // sharedPreferences.edit().putBoolean("fromSearchCardUnknown", true).apply();
            // sharedPreferences.edit().putBoolean("fromSearchCardNotReturn", false).apply();
            // sharedPreferences.edit().putBoolean("fromSearchCardInfo", false).apply();
            // Intent intent = new Intent(SearchActivity.this, RuanganActivity.class);
            // startActivity(intent);
            
            // Show "Coming soon" notification
            Toast.makeText(SearchActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
        });

        cardBelumKembali.setOnClickListener(v -> {
            // System.out.println("SearchActivity fromSTORuangan: " + fromSTORuangan);
            // System.out.println("SearchActivity fromSearchCard: " + fromSearchCard);
            // sharedPreferences.edit().putBoolean("fromSearchCardUnknown", false).apply();
            // sharedPreferences.edit().putBoolean("fromSearchCardNotReturn", true).apply();
            // sharedPreferences.edit().putBoolean("fromSearchCardInfo", false).apply();
            // Intent intent = new Intent(SearchActivity.this, STORuanganActivity.class);
            // startActivity(intent);
            
            // Show "Coming soon" notification
            Toast.makeText(SearchActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
        });

        cardInformasiLinen.setOnClickListener(v -> {
            System.out.println("SearchActivity fromSTORuangan: " + fromSTORuangan);
            System.out.println("SearchActivity fromSearchCard: " + fromSearchCard);
            sharedPreferences.edit().putBoolean("fromSearchCardUnknown", false).apply();
            sharedPreferences.edit().putBoolean("fromSearchCardNotReturn", false).apply();
            sharedPreferences.edit().putBoolean("fromSearchCardInfo", true).apply();
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
            
            // // Show "Coming soon" notification
            // Toast.makeText(SearchActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Navigasi ke HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        // Animasi transisi
        if (getResources().getIdentifier("slide_in_left", "anim", getPackageName()) != 0 &&
                getResources().getIdentifier("slide_out_right", "anim", getPackageName()) != 0) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}