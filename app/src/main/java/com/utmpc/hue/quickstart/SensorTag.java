package com.utmpc.hue.quickstart;

public class SensorTag {

    private String deviceAddress;
    private int rssi;

    public SensorTag(String deviceAddress, int rssi) {
        this.deviceAddress = deviceAddress;
        this.rssi = rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getDeviceAddress() {
        return this.deviceAddress;
    }

    public int getRssi() {
        return this.rssi;
    }


}