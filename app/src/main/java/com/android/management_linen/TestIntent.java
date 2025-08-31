package com.android.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.helpers.ContainsFilterAdapter;
import com.android.management_linen.models.ResponseData;
import com.android.management_linen.models.ResponseDataRuangan;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestIntent extends AppCompatActivity {
    private int intLayout = 1;
    public SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    public String token, namePIC, pdf_title, namaPerusahaan, dispNamaRuang;
    private int idPerusahaan, roleUser;
    private Integer idRuang;
    private ApiHelper apiService;
    TextView txtPIC;
    public Button btnScanBersih, btnScanKotor;
    MaterialAutoCompleteTextView autoCompleteDropdown;
    ContainsFilterAdapter adapter2;
    private Map<String, Integer> ruanganMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        idRuang = sharedpreferences.getInt("idRuang", 0);
        dispNamaRuang = sharedpreferences.getString("namaRuang", null);
        pdf_title = sharedpreferences.getString("pdf_title", null);

        if (token == null) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_input);

        btnScanBersih = findViewById(R.id.BtnScanBersih);
        btnScanKotor = findViewById(R.id.BtnScanKotor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pilih Ruang");
        }
        txtPIC = findViewById(R.id.txtPIC);
        autoCompleteDropdown = findViewById(R.id.autoCompleteDropdown);

        autoCompleteDropdown.setOnClickListener(v -> autoCompleteDropdown.showDropDown());

        String baseUrl = getResources().getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiHelper.class);

        // get detail user
        Call<ResponseData> getUser = apiService.getResponseData("Token " +token, "application/json", "application/json");
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
                        txtPIC.setText("Selamat datang, " + namePIC);
                        fetchRuangan(idPerusahaan);
                        System.out.println("Role User: " + roleUser);
                    }
                }
                System.out.println(token);
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                txtPIC.setText("Error: " + t.getMessage());
            }
        });
        btnScanBersih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailScanLinen("Scan Bersih");
            }
        });
        btnScanKotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailScanLinen("Scan Kotor");
            }
        });
    }

    // Fetch Ruangan
    private void fetchRuangan(int idPerusahaan) {
        Call<ResponseDataRuangan> getListRuang = apiService.getRuangan(idPerusahaan,"Token " +token, "application/json", "application/json");
        getListRuang.enqueue(new Callback<ResponseDataRuangan>() {
            @Override
            public void onResponse(Call<ResponseDataRuangan> call, Response<ResponseDataRuangan> response) {
                if (response.isSuccessful()) {
                    List<ResponseDataRuangan.Ruangan> ruanganList = response.body().getData();
                    List<String> namaRuang = new ArrayList<>();
                    ruanganMap = new HashMap<>();

                    for (ResponseDataRuangan.Ruangan ruang : ruanganList) {
                        namaRuang.add(ruang.getNama());
                        ruanganMap.put(ruang.getNama(), ruang.getId());
                    }

                    System.out.println("namaRuang size: " + namaRuang.size());

                    adapter2 = new ContainsFilterAdapter(
                            TestIntent.this,
                            android.R.layout.simple_dropdown_item_1line,
                            namaRuang
                    );

                    autoCompleteDropdown.setAdapter(adapter2);
                    autoCompleteDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            idRuang = ruanganMap.get(selectedItem);
                        }
                    });

                } else {
                    Toast.makeText(TestIntent.this, "failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDataRuangan> call, Throwable t) {
                Toast.makeText(TestIntent.this, t.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void openDetailScanLinen(String title) {
        intLayout = 2;
        Intent intent = new Intent(this, MainActivity .class);
        sharedpreferences.edit().putString("pdf_title", title).apply();
        sharedpreferences.edit().putString(token, token).apply();
        sharedpreferences.edit().putInt("idRuang", idRuang).apply();
        sharedpreferences.edit().putString("namaRuangReport", autoCompleteDropdown.getText().toString()).apply();
        startActivity(intent);
        finish();

    }

    // Create Menu Option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Sign Out");
                dialog.setMessage("Apakah anda yakin ingin keluar?");
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya", (dialog1, which) -> {
                    SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tidak", (dialog1, which) -> dialog.dismiss());
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
