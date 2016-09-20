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
    private Map<Integer,List<Integer>> readsBc;
    private String beaconGanador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fillMap();
        beaconManager = new BeaconManager(this);
        beaconManager.setBackgroundScanPeriod(200, 0);
        beaconManager.setForegroundScanPeriod(200,0);
        region = new Region("ranged region", UUID.fromString("E7CD3AEB-363F-9EF0-4E53-DFB7DC5DDD46"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                Log.v("onBeaconDiscover","se descubrio un beacon");
                if (!list.isEmpty()) {
                    Log.v("entrada al listener", String.valueOf(listenerCount));
                    listenerCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int top = 110;
                            for (int i=0;i<list.size();i++){
                                Beacon beacon = list.get(i);
                                List<Integer> reads2 = readsBc.get(beacon.getMinor());
                                if (reads2.size()==3){
                                    int avg = (reads2.get(0) + reads2.get(1) + reads2.get(2))/3;
                                    int rssi = beacon.getRssi()*(-1);
                                    if (rssi<(avg + 5)&& rssi>(avg - 5)){
                                        reads2.set (0,reads2.get(1));
                                        reads2.set (1,reads2.get(2));
                                        reads2.set (2,beacon.getRssi()*(-1));
                                    }
                                    else {
                                        //no hago nada descarto la ultima lectura
                                    }

                                }
                                else{
                                    reads2.add(beacon.getRssi() * (-1));
                                }
                                readsBc.remove(beacon.getMinor());
                                readsBc.put(beacon.getMinor(),reads2);
                                if (beacon.getMinor() == 28695) {
                                    rssiBeacon1.setText(String.valueOf(reads2.get(reads2.size()-1)));
                                }
                                if (beacon.getMinor()==28617) {
                                    rssiBeacon2.setText(String.valueOf(reads2.get(reads2.size()-1)));
                                }
                                if (beacon.getMinor()==1731) {
                                    rssiBeacon3.setText(String.valueOf(reads2.get(reads2.size()-1)));
                                }
                                try{
                                    if(reads2.get(2)<top){
                                        if (beacon.getMinor()==28695)
                                            beaconGanador = "amarillopared";
                                        if (beacon.getMinor()== 28617)
                                            beaconGanador = "amarillopared";
                                        else beaconGanador = "remolachamedio";
                                        toptv.setText("ganador : " + beaconGanador);
                                        top = reads2.get(2) ;
                                    }
                                }catch (Exception e){

                                }
                            }
//                            toptv.setText(String.valueOf(top));

                        }
                    });

                    // TODO: update the UI here
                }
            }
        });

    }

    private void fillMap() {
        readsBc = new HashMap<>();
        readsBc.put(28695,new ArrayList<Integer>());     //minor del lemon1
        readsBc.put(28617,new ArrayList<Integer>());     //minor del lemon2
        readsBc.put(1731,new ArrayList<Integer>());     //minor del remolacha1
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
