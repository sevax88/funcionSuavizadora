package com.example.seba.funcionsuavizadora;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView rssiBeacon1,rssiBeacon2,rssiBeacon3,candidato,toptv;
    private BeaconManager beaconManager;
    private Region region;
    private int listenerCount = 0;
    private Map<Integer,Integer> readsBc;
    private double alpha = 0.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fillMap();
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(250,100);
        beaconManager.setForegroundScanPeriod(250,100);
        region = new Region("ranged region", UUID.fromString("E7CD3AEB-363F-9EF0-4E53-DFB7DC5DDD46"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                Log.v("onBeaconDiscover","se descubrio un beacon - tiempo: " + System.currentTimeMillis());
                if (!list.isEmpty()) {
                    Log.v("entrada al listener", String.valueOf(listenerCount));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int top = 0;
                            for (int i = 0; i < list.size(); i++) {
                                Beacon beacon = list.get(i);
                                if (readsBc.containsKey(beacon.getMinor())) {
                                    int lastRssi = readsBc.get(beacon.getMinor());
                                    int actualRssi = beacon.getRssi() * (-1);
                                    actualRssi = (int) (alpha * actualRssi + (1 - alpha) * lastRssi);
                                    readsBc.put(beacon.getMinor(), actualRssi);

                                    switch (beacon.getMinor()) {
                                        case 28695:
                                            rssiBeacon1.setText("rssi lemon1 = " + actualRssi);
                                            break;
                                        case 28617:
                                            rssiBeacon2.setText("rssi lemon2 = " + actualRssi);
                                            break;
                                        case 1731:
                                            rssiBeacon3.setText("rssi remolacha = " + actualRssi);
                                            break;
                                        default:
                                            break;
                                    }

                                    if (actualRssi > top) {
                                        top = actualRssi;
                                        switch (beacon.getMinor()) {
                                            case 28695:
                                                toptv.setText("THE GRAMY GOES FOR lemon1");
                                                break;
                                            case 28617:
                                                toptv.setText("THE GRAMY GOES FOR lemon2");
                                                break;
                                            case 1731:
                                                toptv.setText("THE GRAMY GOES FOR remolacha");
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }

                        }
                    });
                    // TODO: update the UI here
                }
            }
        });

    }

    private void fillMap() {
        readsBc = new HashMap<>();
        readsBc.put(28695,0);     //minor del lemon1
        readsBc.put(28617,0);     //minor del lemon2
        readsBc.put(1731,0);     //minor del remolacha1
    }

    private void initViews() {
        rssiBeacon1 = (TextView)findViewById(R.id.rssiBeacon1);
        rssiBeacon2 = (TextView)findViewById(R.id.rssiBeacon2);
        rssiBeacon3 = (TextView)findViewById(R.id.rssiBeacon3);
        toptv = (TextView)findViewById(R.id.toptv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
