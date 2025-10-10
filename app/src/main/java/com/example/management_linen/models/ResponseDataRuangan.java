package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseDataRuangan {
    private List<Ruangan> data;

    public List<Ruangan> getData() {
        return data;
    }

    public void setData(List<Ruangan> data) {
        this.data = data;
    }

    public static class Ruangan {
        @SerializedName("id")
        private int id;
        @SerializedName("nama")
        private String nama;
        @SerializedName("perusahaan")
        private int perusahaan;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public int getPerusahaan() {
            return perusahaan;
        }

        public void setPerusahaan(int perusahaan) {
            this.perusahaan = perusahaan;
        }
    }
}
