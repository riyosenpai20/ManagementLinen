package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseInfo implements Serializable {
    private String result, message;
    private List<DetailScanInfo> data;

    public ResponseInfo(String result, String message, List<DetailScanInfo> data) {
        this.result = result;
        this.message = message;
        this.data = data;
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

    public List<DetailScanInfo> getData() {
        return data;
    }

    public void setData(List<DetailScanInfo> data) {
        this.data = data;
    }
}