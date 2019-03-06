package com.utmpc.hue.quickstart;

// Hue Bulb object
public class HueBulb {

    private String uniqueId;
    private SensorTag tag;

    public HueBulb(String uniqueId, SensorTag tag) {
        this.uniqueId = uniqueId;
        this.tag = tag;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public SensorTag getTag() {
        return this.tag;
    }

    public void setTag(SensorTag tag) {
        this.tag = tag;
    }
}

