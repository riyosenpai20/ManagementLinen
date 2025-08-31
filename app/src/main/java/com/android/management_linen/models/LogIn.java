package com.android.management_linen.models;

public class LogIn {
    private String token;

    public LogIn(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
