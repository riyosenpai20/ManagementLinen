package com.android.management_linen.models;

import java.util.List;

public class ResponseScanBersih {
    private String result, message;
    private List<DetailsScanBersih> data;

    public ResponseScanBersih(String result, String message, List<DetailsScanBersih> data) {
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

    public List<DetailsScanBersih> getData() {
        return data;
    }

    public void setData(List<DetailsScanBersih> data) {
        this.data = data;
    }
}
