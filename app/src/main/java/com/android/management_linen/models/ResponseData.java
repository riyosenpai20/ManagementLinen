package com.android.management_linen.models;

import java.util.List;

public class ResponseData {
    private List<Users> data;
    private int role, role_user;
    private int type_rs;

    private String result, message, nama_perusahaan;


    public List<Users> getData() {
        return data;
    }

    public void setData(List<Users> data) {
        this.data = data;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getRoleUser() {
        return role_user;
    }

    public void setRoleUser(int role_user) {
        this.role_user = role_user;
    }

    public int getTypeRs() {
        return type_rs;
    }

    public void setTypeRs(int type_rs) {
        this.type_rs = type_rs;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNamaPerusahaan() {
        return nama_perusahaan;
    }

    public void setNamaPerusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public ResponseData(String result, String message) {
        this.result = result;
        this.message = message;
    }
}
