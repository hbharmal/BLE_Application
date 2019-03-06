package com.utmpc.hue.quickstart;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter {

    Activity activity;
    int layoutResourceID;
    ArrayList<HueBulb> bulbs;

    public ListAdapter(Activity activity, int resource, ArrayList<HueBulb> objects) {
        super(activity.getApplicationContext(), resource, objects);

        this.activity = activity;
        this.layoutResourceID = resource;
        this.bulbs = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID, parent, false);
        }

        HueBulb bulb = bulbs.get(position);
        SensorTag sensor_tag = bulb.getTag();

        String uniqueID = bulb.getUniqueId();
        String RSSI = Integer.toString(sensor_tag.getRssi());

        TextView bulbAddress = (TextView) convertView.findViewById(R.id.device_address);
        TextView bulbRSSI = (TextView) convertView.findViewById(R.id.device_rssi);

        bulbAddress.setText("Address: " + uniqueID);
        bulbRSSI.setText("RSSI: " + RSSI);

        return convertView;

    }



}
