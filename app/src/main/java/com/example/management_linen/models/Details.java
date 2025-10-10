package com.example.management_linen.models;

import java.util.List;

public class Details {
    private int batas_cuci, count_barang;
    private String sub_category__name, warna__name, ukuran__name, perusahaan__nama, barangruang__ruang__nama;

    public Details(int batas_cuci, int count_barang, String sub_category__name, String warna__name, String ukuran__name, String perusahaan__nama, String barangruang__ruang__nama) {
        this.batas_cuci = batas_cuci;
        this.count_barang = count_barang;
        this.sub_category__name = sub_category__name;
        this.warna__name = warna__name;
        this.ukuran__name = ukuran__name;
        this.perusahaan__nama = perusahaan__nama;
        this.barangruang__ruang__nama = barangruang__ruang__nama;
    }

    public int getBatascuci() {
        return batas_cuci;
    }

    public void setBatascuci(int batas_cuci) {
        this.batas_cuci = batas_cuci;
    }

    public String getSubCategoryName() {
        return sub_category__name;
    }

    public void setSubCategoryName(String sub_category__name) {
        this.sub_category__name = sub_category__name;
    }

    public String getWarnaName() {
        return warna__name;
    }

    public void setWarnaName(String warna__name) {
        this.warna__name = warna__name;
    }

    public String getUkuranName() {
        return ukuran__name;
    }

    public void setUkuranName(String ukuran__name) {
        this.ukuran__name = ukuran__name;
    }

    public int getCountBarang() {
        return count_barang;
    }

    public void setCountBarang(int count_barang) {
        this.count_barang = count_barang;
    }

    public String getPerusahaanNama() {
        return perusahaan__nama;
    }

    public void setPerusahaanNama(String perusahaan__nama) {
        this.perusahaan__nama = perusahaan__nama;
    }

    public String getBarangruangRuangNama() {
        return barangruang__ruang__nama;
    }

    public void setBarangruangRuangNama(String barangruang__ruang__nama) {
        this.barangruang__ruang__nama = barangruang__ruang__nama;
    }
}
