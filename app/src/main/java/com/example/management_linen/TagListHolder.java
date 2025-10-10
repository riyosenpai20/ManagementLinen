package com.example.management_linen;

import java.util.ArrayList;
import java.util.HashMap;

public class TagListHolder {
    private static final TagListHolder instance = new TagListHolder();
    private ArrayList<HashMap<String, String>> tagList;

    private TagListHolder() {
        tagList = new ArrayList<>();
    }

    public static TagListHolder getInstance() {
        return instance;
    }

    public ArrayList<HashMap<String, String>> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<HashMap<String, String>> tagList) {
        this.tagList = tagList;
    }
}
