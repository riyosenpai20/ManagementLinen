package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String rfid;
    private String category;
    @SerializedName("sub_category")
    private String subCategory;
    private String color;
    private String size;
    private String room;
    @SerializedName("history_type")
    private String historyType;

    public Item(int id, String rfid, String category, String subCategory, String color, String size, String room, String historyType) {
        this.id = id;
        this.rfid = rfid;
        this.category = category;
        this.subCategory = subCategory;
        this.color = color;
        this.size = size;
        this.room = room;
        this.historyType = historyType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getHistoryType() {
        return historyType;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }
}