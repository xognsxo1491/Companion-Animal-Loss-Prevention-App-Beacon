package com.example.taehun.totalmanager.BeaconScan;

public class BeaconItem {
    String uuid,major,minor,distance;

    public BeaconItem(String uuid, String major, String minor, String distance){
        this.uuid = uuid;
        this.distance = distance;
        this.major = major;
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String distance) {
        this.minor = minor;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
