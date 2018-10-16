package com.example.taehun.totalmanager;

public class BeaconListItem {
    private String UUID;
    private String major;
    private String minor;

    public BeaconListItem(String UUID, String major, String minor) {
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
    }


    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
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

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
