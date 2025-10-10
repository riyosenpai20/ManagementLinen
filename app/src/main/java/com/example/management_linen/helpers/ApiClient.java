package com.example.management_linen.helpers;

import android.content.Context;

import com.example.management_linen.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null && context != null) {
            String baseUrl = context.getResources().getString(R.string.BASE_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (retrofit == null) {
            // Gunakan URL default jika tidak ada context
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.3:8001/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    // Method lama untuk backward compatibility
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Gunakan URL default jika tidak ada context
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.3:8001/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}