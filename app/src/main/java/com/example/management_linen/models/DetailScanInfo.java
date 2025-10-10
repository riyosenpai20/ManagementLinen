package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailScanInfo implements Serializable {
    private String category;
    @SerializedName("sub_category")
    private String subCategory;
    private String color;
    private String size;
    private int count;
    @SerializedName("room_distribution")
    private List<RoomDistribution> roomDistribution;
    private List<Item> items;

    public DetailScanInfo(String category, String subCategory, String color, String size, int count, 
                         List<RoomDistribution> roomDistribution, List<Item> items) {
        this.category = category;
        this.subCategory = subCategory;
        this.color = color;
        this.size = size;
        this.count = count;
        this.roomDistribution = roomDistribution;
        this.items = items;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<RoomDistribution> getRoomDistribution() {
        return roomDistribution;
    }

    public void setRoomDistribution(List<RoomDistribution> roomDistribution) {
        this.roomDistribution = roomDistribution;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}