package com.android.management_linen.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DetailScanSto implements Serializable {
    private String category;
    
    @SerializedName("sub_category")
    private String subCategory;
    
    private String color;
    private String size;
    private List<String> rfids;
    private int count;
    
    @SerializedName("matching_rfids")
    private List<String> matchingRfids;
    
    @SerializedName("matching_count")
    private int matchingCount;
    
    @SerializedName("no_matching_rfids")
    private List<String> noMatchingRfids;
    
    @SerializedName("no_match_count")
    private int noMatchCount;
    
    @SerializedName("no_match_tags")
    private List<String> noMatchTags;
    
    // Constructor
    public DetailScanSto() {
        rfids = new ArrayList<>();
        matchingRfids = new ArrayList<>();
        noMatchingRfids = new ArrayList<>();
        noMatchTags = new ArrayList<>();
    }
    
    // Getters and Setters
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
    
    public List<String> getRfids() {
        return rfids;
    }
    
    public void setRfids(List<String> rfids) {
        this.rfids = rfids;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public List<String> getMatchingRfids() {
        return matchingRfids;
    }
    
    public void setMatchingRfids(List<String> matchingRfids) {
        this.matchingRfids = matchingRfids;
    }
    
    public int getMatchingCount() {
        return matchingCount;
    }
    
    public void setMatchingCount(int matchingCount) {
        this.matchingCount = matchingCount;
    }
    
    public List<String> getNoMatchingRfids() {
        return noMatchingRfids;
    }
    
    public void setNoMatchingRfids(List<String> noMatchingRfids) {
        this.noMatchingRfids = noMatchingRfids;
    }
    
    public int getNoMatchCount() {
        return noMatchCount;
    }
    
    public void setNoMatchCount(int noMatchCount) {
        this.noMatchCount = noMatchCount;
    }
    
    public List<String> getNoMatchTags() {
        return noMatchTags;
    }
    
    public void setNoMatchTags(List<String> noMatchTags) {
        this.noMatchTags = noMatchTags;
    }
    
    // Static class for API response
    public static class ApiResponse implements Serializable {
        private List<DetailScanSto> data;
        
        @SerializedName("unmatched_groups")
        private List<UnmatchedGroup> unmatchedGroups;
        
        private String error;
        
        public List<DetailScanSto> getData() {
            return data;
        }
        
        public void setData(List<DetailScanSto> data) {
            this.data = data;
        }
        
        public List<UnmatchedGroup> getUnmatchedGroups() {
            return unmatchedGroups;
        }
        
        public void setUnmatchedGroups(List<UnmatchedGroup> unmatchedGroups) {
            this.unmatchedGroups = unmatchedGroups;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    // Inner class for UnmatchedGroup
    public static class UnmatchedGroup implements Serializable {
        private String category;
        
        @SerializedName("sub_category")
        private String subCategory;
        
        private String color;
        private String size;
        private List<String> rfids;
        private int count;
        
        // Default constructor for deserialization
        public UnmatchedGroup() {
            rfids = new ArrayList<>();
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
        
        public List<String> getRfids() {
            return rfids;
        }
        
        public void setRfids(List<String> rfids) {
            this.rfids = rfids;
        }
        
        public int getCount() {
            return count;
        }
        
        public void setCount(int count) {
            this.count = count;
        }
    }
}