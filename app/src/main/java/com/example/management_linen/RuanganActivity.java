package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;

public class RuanganActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private boolean fromSTORuangan = false;
    private boolean fromSearchCard = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruangan);

        // Toolbar setup removed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ruangan");
        }
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        fromSTORuangan = sharedPreferences.getBoolean("fromSTORuangan", false);
        fromSearchCard = sharedPreferences.getBoolean("fromSearchCard", false);

        System.out.println("RuanganActivity fromSTORuangan: " + fromSTORuangan);
        System.out.println("RuanganActivity fromSearchCard: " + fromSearchCard);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh flags from SharedPreferences when activity resumes
        fromSTORuangan = sharedPreferences.getBoolean("fromSTORuangan", false);
        fromSearchCard = sharedPreferences.getBoolean("fromSearchCard", false);

        System.out.println("RuanganActivity onResume - fromSTORuangan: " + fromSTORuangan);
        System.out.println("RuanganActivity onResume - fromSearchCard: " + fromSearchCard);

        // Setup card_laundry click listener
        CardView cardLaundry = findViewById(R.id.card_laundry);
        cardLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set flag that MainActivity is opened from RuanganActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("fromRuanganActivity", false);
                editor.putString("cardType", "laundry");
                editor.apply();
                
                // Open MainActivity
                Intent intent = new Intent(RuanganActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        
        // Setup card_inventory click listener
        CardView cardInventory = findViewById(R.id.card_inventory);
        cardInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set flag that ScanMode is opened from RuanganActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("fromRuanganActivity", false);
                editor.putString("cardType", "inventory");
                editor.apply();
                
                // Open MainActivity instead of ScanMode
                Intent intent = new Intent(RuanganActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        
        // Setup card_ruangan click listener
        CardView cardRuangan = findViewById(R.id.card_ruangan);
        cardRuangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set flag that PreviewSearchActivity is opened from RuanganActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("fromRuanganActivity", true);
                editor.putBoolean("fromSearchCard", true);
                editor.putString("cardType", "ruangan");
                editor.apply();
                
                // Open PreviewSearchActivity
                Intent intent = new Intent(RuanganActivity.this, STORuanganActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Tambahkan menu logout
        menu.add(Menu.NONE, 1, Menu.NONE, "Logout")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
    
    @Override
    public void onBackPressed() {
        // Reset flags before navigating back
        if (fromSTORuangan) {
            // Navigate back to STORuanganActivity
            Intent intent = new Intent(this, STORuanganActivity.class);
            startActivity(intent);
            finish();
        } else if (fromSearchCard) {
            // Navigate back to SearchActivity
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Default back behavior
            super.onBackPressed();
        }
        
        // Clear flags in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("fromSTORuangan", false);
        editor.putBoolean("fromSearchCard", false);
        editor.apply();

        // Animasi transisi
        if (getResources().getIdentifier("slide_in_left", "anim", getPackageName()) != 0 &&
                getResources().getIdentifier("slide_out_right", "anim", getPackageName()) != 0) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}