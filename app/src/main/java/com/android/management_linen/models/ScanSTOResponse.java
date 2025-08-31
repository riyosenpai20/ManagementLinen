package com.android.management_linen.models;

import java.util.List;

public class ScanSTOResponse {
    private List<String> tag;
    private Integer ruang;

    public ScanSTOResponse(List<String> tag, Integer ruang) {
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
}