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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView rssiBeacon1,rssiBeacon2,rssiBeacon3,candidato,toptv;
    private BeaconManager beaconManager;
    private Region region;
    private int listenerCount = 0;
    private Map<Integer,Integer> readsBc,beaconsSoporte;
    private double alpha = 0.1;

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
                                            rssiBeacon1.setText("rssi lemon1 - 28695 = " + actualRssi);
                                            break;
                                        case 28617:
                                            rssiBeacon2.setText("rssi lemon2 - 28617 = " + actualRssi);
                                            break;
                                        case 1731:
                                            rssiBeacon3.setText("rssi remolacha - 1731 = " + actualRssi);
                                            break;
                                        default:
                                            break;
                                    }

                                }
                            }
                            //llamar a la funcion que ordena el map by values
                            toptv.setText("AND THE AMI GOES TO... " + sortHashMapByValues((HashMap<Integer, Integer>) readsBc));

                        }
                    });
                    // TODO: update the UI here
                }
            }
        });

    }

    private void fillMap() {
        readsBc = new HashMap<>();
        beaconsSoporte = new HashMap<>();
        readsBc.put(28695, 0);          //minor del lemon1
        readsBc.put(28617,0);           //minor del lemon2
        readsBc.put(1731, 0);           //minor del remolacha1
        beaconsSoporte.put (52909,0);   //minor candy1
        beaconsSoporte.put (27802,0);   //minorcandy2
        beaconsSoporte.put (25989,0);   //minor remolacha2

    }

    private void initViews() {
        rssiBeacon1 = (TextView)findViewById(R.id.rssiBeacon1);
        rssiBeacon2 = (TextView)findViewById(R.id.rssiBeacon2);
        rssiBeacon3 = (TextView)findViewById(R.id.rssiBeacon3);
        toptv = (TextView)findViewById(R.id.toptv);
    }

    public int sortHashMapByValues(HashMap<Integer, Integer> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            int val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1==comp2) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return (int) sortedMap.keySet().toArray()[0];
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
