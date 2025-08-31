package com.android.management_linen.models;

import java.util.List;

public class ScanBersihResponse {
    private List<String> tag;
    private Integer ruang;

    private String result, message;
    private List<String> data;

    public ScanBersihResponse(List<String> tag, Integer ruang) {
        this.tag = tag;
        this.ruang = ruang;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public Integer getRuang() {
        return ruang;
    }

    public void setRuang(Integer ruang) {
        this.ruang = ruang;
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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
