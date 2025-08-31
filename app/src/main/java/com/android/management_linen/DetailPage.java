package com.android.management_linen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailsAdapter;
import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.Details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailPage extends AppCompatActivity {
    private int intLayout = 2;
    public ArrayList<HashMap<String, String>> tagList, tagList2;
    RecyclerView recyclerView;
    DetailsAdapter adapter;
    LinearLayoutManager layoutManager;
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    String token;
    private List<Details> detailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);

        setContentView(R.layout.activity_detailpage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DetailsAdapter(detailsList, DetailPage.this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DetailsAdapter(detailsList, DetailPage.this);
        recyclerView.setAdapter(adapter);

        String baseUrl = getResources().getString(R.string.BASE_URL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiHelper apiService = retrofit.create(ApiHelper.class);

//        ArrayList<HashMap<String, String>> tagList2 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("tagList");
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();

        ArrayList<String> tagUiiList = new ArrayList<>();

        for (HashMap<String, String> map : tagList2) {
            String tagUii = map.get("tagUii");
            if (tagUii != null) {
                tagUiiList.add(tagUii);
            }
        }
        System.out.println("tagUiiList: " + tagUiiList);

        Call<List<Details>> call = apiService.inventory_detail(tagUiiList, token, "application/json", "application/json");
        call.enqueue(new Callback<List<Details>>() {

            @Override
            public void onResponse(Call<List<Details>> call, Response<List<Details>> response) {
                if (response.isSuccessful() && response.body() != null){
                    detailsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DetailPage.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Details>> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(DetailPage.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
        System.out.println("Tag2: " + tagList2);
        if(intLayout == 2){
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra("tagList", tagList2);
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
            ArrayList<HashMap<String, String>> tagList2 = TagListHolder.getInstance().getTagList();
            TagListHolder.getInstance().setTagList(tagList2);
            System.out.println("Tag3: " + tagList2);
            intLayout = 1;
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tagList", tagList2);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
