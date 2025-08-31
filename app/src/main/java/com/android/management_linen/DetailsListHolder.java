package com.android.management_linen;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailsListHolder {
    private static final DetailsListHolder instance = new DetailsListHolder();
    private ArrayList<HashMap<String, String>> detailsListScan;

    private DetailsListHolder() {
        detailsListScan = new ArrayList<>();
    }

    public static DetailsListHolder getInstance() {
        return instance;
    }

    public ArrayList<HashMap<String, String>> getDetailsListScan() {
        return detailsListScan;
    }

    public void setDetailsListScan(ArrayList<HashMap<String, String>> detailsListScan) {
        this.detailsListScan = detailsListScan;
    }
}
