package com.android.management_linen.models;

import java.util.List;

public class ResponseScanSTO {
    private String result, message;
    private List<DetailScanSto> data;

    public ResponseScanSTO(String result, String message, List<DetailScanSto> data) {
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

    public List<DetailScanSto> getData() {
        return data;
    }

    public void setData(List<DetailScanSto> data) {
        this.data = data;
    }
}