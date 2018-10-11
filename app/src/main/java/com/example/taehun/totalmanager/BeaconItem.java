package com.example.taehun.totalmanager;

public class BeaconItem {
    String uuid;
    String distance;

    public BeaconItem(String uuid, String distance){
        this.uuid = uuid;
        this.distance = distance;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
