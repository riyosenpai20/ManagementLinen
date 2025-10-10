package com.example.management_linen;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.Button;
import android.content.Context;
public class TesTab extends AppCompatActivity {
    public static final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testab);

        Button BtnTes = (Button) findViewById(R.id.BtnTes);
        BtnTes.setOnClickListener( v -> {
            Intent intent = new Intent(this, TestPage.class);
            startActivity(intent);
            finish();
        });
    }
}
