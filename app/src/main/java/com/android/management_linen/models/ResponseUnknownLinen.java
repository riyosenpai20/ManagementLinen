package com.android.management_linen.models;

import java.util.List;

public class ResponseUnknownLinen {
    private String result, message;
    private List<DetailScanUnknown> data;

    public ResponseUnknownLinen(String result, String message, List<DetailScanUnknown> data) {
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

    public List<DetailScanUnknown> getData() {
        return data;
    }

    public void setData(List<DetailScanUnknown> data) {
        this.data = data;
    }
}