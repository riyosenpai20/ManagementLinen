package com.example.management_linen.models;
import java.util.List;

public class Users {
    private int id, perusahaan, type_rs;
    private String name, email, username, nama_perusahaan;
    private List<Object> rooms;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPerusahaan() {
        return perusahaan;
    }

    public void setPerusahaan(int perusahaan) {
        this.perusahaan = perusahaan;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Object> getRooms() {
        return rooms;
    }

    public void setRooms(List<Object> rooms) {
        this.rooms = rooms;
    }

    public String getNamaPerusahaan() {
        return nama_perusahaan;
    }

    public int getTypeRs() {
        return type_rs;
    }

    public void setTypeRs(int type_rs) {
        this.type_rs = type_rs;
    }

    public void setNamaPerusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }
}
