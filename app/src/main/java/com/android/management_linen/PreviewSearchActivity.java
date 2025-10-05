package com.android.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailScanUnknownAdapter;
import com.android.management_linen.helpers.ApiClient;
import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.DetailScanSto;
import com.android.management_linen.models.DetailScanUnknown;
import com.android.management_linen.models.ResponseUnknownLinen;
import com.android.management_linen.models.RfidRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewSearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvRuangan;
    private TextView tvTotalItems;
    private List<String> tagList;
    private String namaRuang;
    private TableLayout tableLayout;
    private float txtSize = 12;
    
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private String token;
    private ApiHelper apiHelper;

    private List<DetailScanUnknown> searchData;
    private String cardType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_search);
        
        // Initialize searchData
        searchData = new ArrayList<>();

        // Initialize SharedPreferences
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        cardType = sharedpreferences.getString("cardType", "");

        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Preview Pencarian");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvRuangan = findViewById(R.id.tvRuangan);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tableLayout = findViewById(R.id.tableLayout); // Tambahkan inisialisasi tableLayout di sini
        
        // Initialize API helper
        apiHelper = ApiClient.getClient().create(ApiHelper.class);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            tagList = (List<String>) intent.getSerializableExtra("tagList");
            namaRuang = intent.getStringExtra("namaRuang");
            
            if (tagList != null) {
                tvRuangan.setText(namaRuang);
                tvTotalItems.setText(String.valueOf(tagList.size()));
                
                // Set up adapter for RecyclerView
                // Note: You'll need to create a TagAdapter class
                // TagAdapter adapter = new TagAdapter(tagList);
                // recyclerView.setAdapter(adapter);

                System.out.println(tagList);
                
                // Kirim data otomatis saat halaman dibuka
                kirimDataKeServer();
            } else {
                Toast.makeText(this, "Tidak ada data yang ditemukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void kirimDataKeServer() {
        if (tagList == null || tagList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk dikirim", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Menampilkan loading
        Toast.makeText(this, "Mengirim data dari lokasi " + cardType, Toast.LENGTH_SHORT).show();
        
        // Memanggil API untuk mengirim data
        RfidRequest rfidRequest = new RfidRequest(tagList, cardType);
        Call<ResponseUnknownLinen> call = apiHelper.search_unknown_linen(
                rfidRequest,
                "Token " + token,
                "application/json",
                "application/json"
        );
        
        call.enqueue(new Callback<ResponseUnknownLinen>() {
            @Override
            public void onResponse(Call<ResponseUnknownLinen> call, Response<ResponseUnknownLinen> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseUnknownLinen responseData = response.body();
//                    Toast.makeText(PreviewSearchActivity.this, responseData.getMessage(), Toast.LENGTH_SHORT).show();
                    
                    List<DetailScanUnknown> items = responseData.getData();
                    Log.d("PreviewSearch", "Jumlah items: " + (items != null ? items.size() : 0));
                    if (items != null && !items.isEmpty()) {
                        int rowIndex = 0; // Tambahkan variabel ini di awal loop items
                        
                        for (DetailScanUnknown item : items) {
                            // Tampilkan informasi item lebih detail
                            Log.d("PreviewSearch", "Category: " + item.getCategory() 
                                  + ", SubCategory: " + item.getSubCategory()
                                  + ", Location: " + item.getLocation()
                                  + ", RFID count: " + (item.getRfids() != null ? item.getRfids().size() : 0));
//                            System.out.println(item.getRfids());
                            // Tambahkan ke list untuk ditampilkan
//                            searchData.add(item);

//                            TableRow row = new TableRow(PreviewSearchActivity.this);
//                            row.setPadding(3,10,3,10);
//                            row.setLayoutParams(new TableLayout.LayoutParams(
//                                    TableLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//                            ));
//
//                            row.setBackgroundColor(ContextCompat.getColor(PreviewSearchActivity.this, R.color.tb_RowMain_Primary));

                            List<String> rfids = item.getRfids();
                            if(rfids != null && !rfids.isEmpty()) {
                                for(String rfid : rfids) {
                                    System.out.println(rfid);
                                    TableRow row = new TableRow(PreviewSearchActivity.this);
                                    row.setPadding(5,10,5,10);
                                    row.setLayoutParams(new TableLayout.LayoutParams(
                                            TableLayout.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));

                                    // Gunakan warna berbeda untuk baris ganjil dan genap
                                    row.setBackgroundColor(ContextCompat.getColor(PreviewSearchActivity.this, 
                                            rowIndex % 2 == 0 ? R.color.tb_RowUnknown_Primary : R.color.white));
                                    rowIndex++;
                                    TextView tvRfid = new TextView(PreviewSearchActivity.this);
                                    tvRfid.setText(rfid);
                                    // Perbaiki parameter layout - gunakan TableRow.LayoutParams bukan TableLayout.LayoutParams
                                    tvRfid.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvRfid.setGravity(Gravity.CENTER);
                                    tvRfid.setPadding(5,5,5,5);
                                    tvRfid.setTextSize(txtSize);

                                    TextView tvLinen = new TextView(PreviewSearchActivity.this);
                                    tvLinen.setText(item.getSubCategory() + " " + item.getColor() + " " + item.getSize());
                                    // Perbaiki parameter layout
                                    tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvLinen.setGravity(Gravity.LEFT);
                                    tvLinen.setPadding(5,5,5,5);
                                    tvLinen.setTextSize(txtSize);

                                    TextView tvLokasi = new TextView(PreviewSearchActivity.this);
                                    tvLokasi.setText(item.getLocation() != null ? item.getLocation() : "-");
                                    // Perbaiki parameter layout
                                    tvLokasi.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvLokasi.setGravity(Gravity.CENTER);
                                    tvLokasi.setPadding(5,5,5,5);
                                    tvLokasi.setTextSize(txtSize);

                                    row.addView(tvRfid);
                                    row.addView(tvLinen);
                                    row.addView(tvLokasi); // Tambahkan kolom lokasi ke dalam row

                                    tableLayout.addView(row);
                                }
                            }
                        }
                        
//                        // Tampilkan data dalam RecyclerView dengan format tabel
//                        DetailScanUnknownAdapter adapter = new DetailScanUnknownAdapter(searchData, PreviewSearchActivity.this);
//                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(PreviewSearchActivity.this, "Tidak ada data yang ditemukan", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Tambahkan logika untuk menampilkan hasil pencarian jika diperlukan
                    // Misalnya, update RecyclerView dengan data dari responseData.getData()
                } else {
                    Toast.makeText(PreviewSearchActivity.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ResponseUnknownLinen> call, Throwable t) {
                Toast.makeText(PreviewSearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}