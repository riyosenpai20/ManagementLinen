package com.android.management_linen.models;

import java.util.List;

public class APIResponse {
    private String result, message;
    private List<String> data;

    public APIResponse(String result, String message) {
        this.result = result;
        this.message = message;
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
}
