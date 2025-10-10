package com.example.management_linen.models;

import java.util.List;

public class RfidRequest {
    private List<String> rfids;
    private String cardType;
    private Integer idRuang;

    public RfidRequest(List<String> rfids, String cardType, Integer idRuang) {
        this.rfids = rfids;
        this.cardType = cardType;
        this.idRuang = idRuang;
    }
    
    // Konstruktor tambahan tanpa idRuang (opsional)
    public RfidRequest(List<String> rfids, String cardType) {
        this.rfids = rfids;
        this.cardType = cardType;
        this.idRuang = null;
    }

    public List<String> getRfids() {
        return rfids;
    }

    public void setRfids(List<String> rfids) {
        this.rfids = rfids;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Integer getIdRuang() {
        return idRuang;
    }

    public void setIdRuang(Integer idRuang) {
        this.idRuang = idRuang;
    }
}