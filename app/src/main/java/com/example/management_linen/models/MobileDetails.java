package com.example.management_linen.models;

public class MobileDetails {
    private String name;
    private String warna;
    private String ukuran;

    public MobileDetails(String name, String warna, String ukuran) {
        this.name = name;
        this.warna = warna;
        this.ukuran = ukuran;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }
}
