package com.android.management_linen.models;

import com.google.gson.annotations.SerializedName;

public class Details {
    private int batas_cuci, count_barang;

    @SerializedName("berat")
    private Double berat;
    private String sub_category__name, warna__name, ukuran__name, perusahaan__nama, barangruang__ruang__nama, status, result, message;

    public Details(int batas_cuci, int count_barang, String sub_category__name, String warna__name, String ukuran__name, String perusahaan__nama, String barangruang__ruang__nama, String status, String result, String message, Double berat) {
        this.batas_cuci = batas_cuci;
        this.count_barang = count_barang;
        this.sub_category__name = sub_category__name;
        this.warna__name = warna__name;
        this.ukuran__name = ukuran__name;
        this.perusahaan__nama = perusahaan__nama;
        this.barangruang__ruang__nama = barangruang__ruang__nama;
        this.status = status;
        this.result = result;
        this.message = message;
        this.berat = berat;
    }

    public Double getBerat() {
        return berat;
    }

    public void setBerat(Double berat) {
        this.berat = berat;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Details{" +
                "batas_cuci=" + batas_cuci +
                ", count_barang=" + count_barang +
                ", sub_category__name='" + sub_category__name + '\'' +
                ", warna__name='" + warna__name + '\'' +
                ", ukuran__name='" + ukuran__name + '\'' +
                ", perusahaan__nama='" + perusahaan__nama + '\'' +
                ", barangruang__ruang__nama='" + barangruang__ruang__nama + '\'' +
                ", status='" + status + '\'' +
                ", result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", berat='"+berat+'\''+
                '}';
    }
}
