package com.example.management_linen.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RoomDistribution implements Serializable {
    private String room;
    private int count;
    private List<String> rfid;

    public RoomDistribution(String room, int count, List<String> rfid) {
        this.room = room;
        this.count = count;
        this.rfid = rfid;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getRfid() {
        return rfid;
    }

    public void setRfid(List<String> rfid) {
        this.rfid = rfid;
    }
}