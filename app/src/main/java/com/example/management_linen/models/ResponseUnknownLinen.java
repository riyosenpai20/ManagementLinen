package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseUnknownLinen implements Serializable {
    private String result, message;
    private List<DetailScanUnknown> data;
    private String cardType;
    private String ruang;
    @SerializedName("perusahaan")
    private String perusahaanNama;

    public ResponseUnknownLinen(String result, String message, List<DetailScanUnknown> data, String cardType, String ruang, String perusahaanNama) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.cardType = cardType;
        this.ruang = ruang;
        this.perusahaanNama = perusahaanNama;
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

    public List<DetailScanUnknown> getData() {
        return data;
    }

    public void setData(List<DetailScanUnknown> data) {
        this.data = data;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getRuang() {
        return ruang;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getPerusahaanNama() {
        return perusahaanNama;
    }

    public void setPerusahaanNama(String perusahaanNama) {
        this.perusahaanNama = perusahaanNama;
    }
}