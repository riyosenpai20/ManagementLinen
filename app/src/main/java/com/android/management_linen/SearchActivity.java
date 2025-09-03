package com.android.management_linen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
        
        // Setup click listeners
        CardView cardTidakDiketahui = findViewById(R.id.card_tidak_diketahui);
        CardView cardBelumKembali = findViewById(R.id.card_belum_kembali);
        CardView cardInformasiLinen = findViewById(R.id.card_informasi_linen);
        
        cardTidakDiketahui.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, RuanganActivity.class);
            startActivity(intent);
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
}