package com.example.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.management_linen.adapter.DetailsAdapter;
import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.DataItem;
import com.example.management_linen.models.Details;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestPage extends AppCompatActivity  {
    public ArrayList<HashMap<String, String>> tagList;
    RecyclerView recyclerView;
    DetailsAdapter adapter;
    LinearLayoutManager layoutManager;
    private Context context;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private int intLayout = 2;
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    String token;
    private List<Details> detailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);

        setContentView(R.layout.activity_testpage);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DetailsAdapter(detailsList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailsAdapter(detailsList);
        recyclerView.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // For sample testing
        List<String> hardcodedData = Arrays.asList(
                "E28069950000400AAF27B511",
                "E28069950000400AAF2555D8",
                "E28069950000400AAF27B511",
                "E28069950000400AAF2555D8",
                "E2806995000040115B650861",
                "E28068940000401EDFA5FD9B",
                "E28069950000400AAF21CE74",
                "E28069950000400AAF299544",
                "E28068940000502016A5A8E5",
                "E2806894000040159D313D73",
                "E28069950000400EDEA630DE",
                "E280689400004021FF431923",
                "E28068940000501938D3591F"
        );

        tagList = new ArrayList<HashMap<String, String>>();

        for (String tag : hardcodedData) {
            HashMap<String, String> map = new HashMap<>();
            map.put("tagUii", tag);
            map.put("tagLen", "24"); // Example value for tagLen
            map.put("tagCount", "1"); // Example value for tagCount
            map.put("tagRssi", "-55"); // Example value for tagRssi
            tagList.add(map);

        }

        String baseUrl = getResources().getString(R.string.BASE_URL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiHelper apiService = retrofit.create(ApiHelper.class);
        Call<List<Details>> call = apiService.inventory_detail(hardcodedData, token, "application/json", "application/json");
        call.enqueue(new Callback<List<Details>>() {
            @Override
            public void onResponse(Call<List<Details>> call, Response<List<Details>> response) {
                if (response.isSuccessful() && response.body() != null){
                    detailsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TestPage.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Details>> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(TestPage.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        if(intLayout == 2){
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(intLayout == 2){
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", (ArrayList<HashMap<String, String>>) tagList);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
