package com.android.management_linen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.management_linen.helpers.ApiHelper;
import com.android.management_linen.models.LogIn;
import com.android.management_linen.requests.LogInRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
//    SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
//    SharedPreferences.Editor editor = preferences.edit();
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String token = sharedpreferences.getString("token", null);
        if (token != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        EditText username, password;
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);

        String baseUrl = getResources().getString(R.string.BASE_URL);

        Button signInButton = (Button) findViewById(R.id.btn_login);
        signInButton.setOnClickListener(v -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiHelper apiService = retrofit.create(ApiHelper.class);

            LogInRequest logInRequest = new LogInRequest(username.getText().toString(), password.getText().toString());

            Call<LogIn> call = apiService.logIn(logInRequest);
//            Toast.makeText(this, call.toString(), Toast.LENGTH_SHORT).show();

            call.enqueue(new Callback<LogIn>() {
                @Override
                public void onResponse(Call<LogIn> call, Response<LogIn> response) {
                    assert response.body() != null;
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    sharedpreferences.edit().putString("token", response.body().getToken()).apply();
                    if(response.body().getToken() != null) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<LogIn> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
