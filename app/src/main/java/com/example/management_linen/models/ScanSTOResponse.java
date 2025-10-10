package com.example.management_linen.models;

import java.util.List;

public class ScanSTOResponse {
    private List<String> tag;
    private Integer ruang;

    private String jenisSTO;

    public ScanSTOResponse(List<String> tag, Integer ruang, String jenisSTO) {
        this.tag = tag;
        this.ruang = ruang;
        this.jenisSTO = jenisSTO;
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

    public String getJenisSTO() {
        return jenisSTO;
    }

    public void setJenisSTO(String jenisSTO) {
        this.jenisSTO = jenisSTO;
    }
}