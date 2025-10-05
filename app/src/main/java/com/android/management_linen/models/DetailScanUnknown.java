package com.android.management_linen.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DetailScanUnknown implements Serializable {
    private String category;
    @SerializedName("sub_category")
    private String subCategory;
    private String color;
    private String size;
    private List<String> rfids;

    @SerializedName("history_type")
    private String historyType;
    
    private String location;
    private String status;
    
    @SerializedName("location_id")
    private int locationId;
    
    // Constructor
    public DetailScanUnknown() {
        rfids = new ArrayList<>();
    }
    
    // Getters and Setters
    public List<String> getRfids() {
        return rfids;
    }
    
    public void setRfids(List<String> rfids) {
        this.rfids = rfids;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getHistoryType() {
        return historyType;
    }
    
    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getLocationId() {
        return locationId;
    }
    
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}