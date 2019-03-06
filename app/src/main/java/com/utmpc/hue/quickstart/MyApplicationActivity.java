package com.utmpc.hue.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class MyApplicationActivity extends Activity implements AdapterView.OnItemClickListener {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    public static final int REQUEST_ENABLE_BT = 1;


    private BluetoothAdapter bluetoothAdapter;

    private Map<String, HueBulb> map = new HashMap<>();

    private BroadcastReceiver receiver;
    private Scanner scanner;

    private ArrayList<HueBulb> bulbsList;
    private ListAdapter listAdapter;
    private ListView listView;

    private Button buttonScan;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        // check to make sure bluetooth is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
            finish();
        }

        receiver = new BTBroadcastReceiver(this.getApplicationContext());
        scanner = new Scanner(this, -75);

        bulbsList = new ArrayList<>();

        listAdapter = new ListAdapter(this, R.layout.bulb_device_list_item, bulbsList);

        listView = new ListView(this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        ScrollView scrollView = (ScrollView) findViewById(R.id.lights_list);
        scrollView.addView(listView);

        // create instance of Hue SDK
        phHueSDK = PHHueSDK.create();

        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scanner.isScanning()) {
                    Log.v(TAG, "PRESSED");
                    startScan();
                } else {
                    stopScan();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        stopScan();
    }

    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this.getApplicationContext(), "Thank you for turning on Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        Log.v(TAG, "clicked");
        HueBulb bulb = bulbsList.get(position);
        String uniqueId = bulb.getUniqueId();

        PHBridge bridge = phHueSDK.getSelectedBridge();
        Random rand = new Random();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light: allLights) {
            if (light.getUniqueId().equals(uniqueId)) {
                PHLightState lightState = new PHLightState();
                lightState.setOn(true);
                lightState.setHue(rand.nextInt(MAX_HUE));
                bridge.updateLightState(light, lightState, listener);
            }
        }
    }

    public void addDevice(BluetoothDevice device, String uniqueId, int rssi) {
        SensorTag tag = new SensorTag(device.getAddress(), rssi);

        if (!map.containsKey(device.getAddress())) {
            HueBulb bulb = new HueBulb(uniqueId, tag);
            map.put(device.getAddress(), bulb);
            bulbsList.add(bulb);
            Log.v(TAG, "YES");
        } else {
            HueBulb bulb = map.get(device.getAddress());
            bulb.setTag(tag);
        }
        listAdapter.notifyDataSetChanged();
    }

    public void startScan() {
        buttonScan.setText("Scanning...");
        scanner.start();
    }

    public void stopScan() {
        Log.v(TAG, "Stop Clicked");
        buttonScan.setText("Scan Again");
        scanner.stop();
    }


    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };
    

}
