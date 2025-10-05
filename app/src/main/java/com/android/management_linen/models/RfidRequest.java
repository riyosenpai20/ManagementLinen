package com.android.management_linen.models;

import java.util.List;

public class RfidRequest {
    private List<String> rfids;
    private String cardType;

    public RfidRequest(List<String> rfids) {
        this.rfids = rfids;
    }
    
    public RfidRequest(List<String> rfids, String cardType) {
        this.rfids = rfids;
        this.cardType = cardType;
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
}