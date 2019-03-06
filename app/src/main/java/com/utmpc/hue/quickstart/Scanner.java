package com.utmpc.hue.quickstart;


import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Scanner {

    private MyApplicationActivity myApplicationActivity;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    private Handler handler;

    private static final long SCAN_PERIOD = 10000;

    private int signalStrength;

    // Map consisting of mac address (beacon) - UUID (bulb) pairs
    private Map<String, String> map;

    private Set<String> beaconAddresses;

    public Scanner(MyApplicationActivity myApplicationActivity, int signalStrength) {
        this.myApplicationActivity = myApplicationActivity;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager = (BluetoothManager) myApplicationActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // handle map here
        map = new HashMap<>();
        map.put("78:A5:04:8C:1F:33", "00:17:88:01:01:18:95:ae-0b"); // Pair one
        map.put("78:A5:04:8C:1F:4E", "00:17:88:01:01:18:95:ba-0b"); // Pair two
        map.put("78:A5:04:8C:21:97", "00:17:88:01:01:14:82:c6-0b"); // Pair three

        beaconAddresses = map.keySet();

        handler = new Handler();

    }


    private void scanLeDevice(final boolean enable) {
        if (enable && !scanning) {
            Toast.makeText(myApplicationActivity.getApplicationContext(), "Starting BLE Scan...", Toast.LENGTH_LONG).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myApplicationActivity.getApplicationContext(), "Stopping BLE Scan...", Toast.LENGTH_LONG).show();
                    scanning = false;
                    bluetoothAdapter.stopLeScan(bleScanCallBack);
                    myApplicationActivity.stopScan();
                }
            }, SCAN_PERIOD);
            scanning = true;
            bluetoothAdapter.startLeScan(bleScanCallBack);
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(bleScanCallBack);
        }

    }


    private BluetoothAdapter.LeScanCallback bleScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            final int newRssi = rssi;
            if (newRssi > signalStrength && beaconAddresses.contains(device.getAddress())) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        myApplicationActivity.addDevice(device, map.get(device.getAddress()), newRssi);
                    }
                });
            }
        }
    };

    public void start() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(myApplicationActivity.getApplicationContext(), "Please enable Bluetooth", Toast.LENGTH_LONG).show();
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            myApplicationActivity.startActivityForResult(enableBluetoothIntent, myApplicationActivity.REQUEST_ENABLE_BT);
        }
        else {
            scanLeDevice(true);
        }

    }

    public void stop() {
        scanLeDevice(false);
    }

    public boolean isScanning() {
        return scanning;
    }



}
