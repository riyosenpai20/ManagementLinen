package com.example.management_linen.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataRequest {
    private ArrayList<String> harcodedData;

    public DataRequest(ArrayList<String> harcodedData) {
        this.harcodedData = harcodedData;
    }

    public ArrayList<String> getHarcodedData() {
        return harcodedData;
    }

    public void setHarcodedData(ArrayList<String> harcodedData) {
        this.harcodedData = harcodedData;
    }
}
