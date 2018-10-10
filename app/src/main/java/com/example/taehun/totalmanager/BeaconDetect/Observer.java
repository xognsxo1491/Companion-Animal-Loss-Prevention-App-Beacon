package com.example.taehun.totalmanager.BeaconDetect;


import com.clj.fastble.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
