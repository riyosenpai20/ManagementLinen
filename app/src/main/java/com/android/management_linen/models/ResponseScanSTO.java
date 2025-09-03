package com.android.management_linen.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponseScanSTO {
    private String result, message;
    private List<DetailScanSto> data;
    
    @SerializedName("unmatched_groups")
    private List<DetailScanSto.UnmatchedGroup> unmatchedGroups;
    
    @SerializedName("not_registered")
    private List<DetailScanSto> notRegistered;

    public ResponseScanSTO(String result, String message, List<DetailScanSto> data) {
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

    public List<DetailScanSto> getData() {
        return data;
    }

    public void setData(List<DetailScanSto> data) {
        this.data = data;
    }
    
    public List<DetailScanSto.UnmatchedGroup> getUnmatchedGroups() {
        return unmatchedGroups;
    }
    
    public void setUnmatchedGroups(List<DetailScanSto.UnmatchedGroup> unmatchedGroups) {
        this.unmatchedGroups = unmatchedGroups;
    }
    
    public List<DetailScanSto> getNotRegistered() {
        return notRegistered;
    }
    
    public void setNotRegistered(List<DetailScanSto> notRegistered) {
        this.notRegistered = notRegistered;
    }
}