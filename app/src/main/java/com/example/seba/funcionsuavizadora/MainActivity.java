package com.example.seba.funcionsuavizadora;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.telemetry.EstimoteTelemetry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView rssiBeacon1,rssiBeacon2,rssiBeacon3,rssiBeacon4,rssiBeacon5,toptv;
    private BeaconManager beaconManager;
    private Region region;
    private int listenerCount = 0;
    private Map<Integer,Integer> readsBc,beaconsSoporte;
    private double alpha = 0.1;
    private TextView soporteAmarillo,soporteCandy,soporteRemolacha,equipoAmarillotv,equipoCandytv,equipoRemolachatv,equipoVerdetv,soporteVerde,soporteAzul,equipoAzultv;
    private Integer rssiCarry;
    private int equipoAmarillo,equipoCandy,equipoRemolacha,equipoVerde,equipoAzul;
    public int j=3;
    private String equipoGanador;
    private TreeMap<Integer, String> equiposMap;
    private String neightborArray[] = new String[5];
    private int actualPos = 0;
    private String candidato = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fillMap();
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(950,0);
        region = new Region("ranged region", null, null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                Log.v("onBeaconDiscover","se descubrio un beacon - tiempo: " + System.currentTimeMillis() + "list size =" + list.size());
                if (!list.isEmpty()) {
                    Log.v("entrada al listener", String.valueOf(listenerCount));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                                        case 52909:
                                            rssiBeacon2.setText("rssi candi1 - 52909 = " + actualRssi);
                                            break;
                                        case 1731:
                                            rssiBeacon3.setText("rssi remolacha1 - 1731 = " + actualRssi);
                                            break;
                                        case 4739:
                                            rssiBeacon4.setText("rssi verde1 - 4739 = " + actualRssi);
                                            break;
                                        case 17578:
                                            rssiBeacon5.setText("rssi azul - 17578 = " + actualRssi);
                                            break;
                                        default:
                                            break;
                                    }

                                }
                                if (beaconsSoporte.containsKey(beacon.getMinor())) {
                                    int lastRssi = beaconsSoporte.get(beacon.getMinor());
                                    int actualRssi = beacon.getRssi() * (-1);
                                    actualRssi = (int) (alpha * actualRssi + (1 - alpha) * lastRssi);
                                    beaconsSoporte.put(beacon.getMinor(), actualRssi);
                                    switch (beacon.getMinor()) {
                                        case 28617:
                                            soporteAmarillo.setText("rssi soporte lemon = " + actualRssi);
                                            rssiCarry = readsBc.get(28695);
                                            equipoAmarillo = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi) > 10) {
                                                equipoAmarillo = equipoAmarillo - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi) < 10) {
                                                equipoAmarillo = equipoAmarillo - 5;
                                            }
                                            equipoAmarillotv.setText("equipo amarillo = " + String.valueOf(equipoAmarillo));
                                            break;
                                        case 27802:
                                            soporteCandy.setText("rssi  soporte candy = " + actualRssi);
                                            rssiCarry = readsBc.get(52909);
                                            equipoCandy = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi) > 10) {
                                                equipoCandy = equipoCandy - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi) <10) {
                                                equipoCandy = equipoCandy - 5;
                                            }
                                            equipoCandytv.setText("equipo candy = " + String.valueOf(equipoCandy));
                                            break;
                                        case 25989:
                                            soporteRemolacha.setText("rssi soporte remolacha = " + actualRssi);
                                            rssiCarry = readsBc.get(1731);
                                            equipoRemolacha = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi)>10){
                                                equipoRemolacha = equipoRemolacha - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi)<10){
                                                equipoRemolacha = equipoRemolacha - 5;
                                            }
                                            equipoRemolachatv.setText("equipo remolacha = " + String.valueOf(equipoRemolacha));
                                            break;
                                        case 13451:
                                            soporteVerde.setText("rssi soporte verde = " + actualRssi);
                                            rssiCarry = readsBc.get(4739);
                                            equipoVerde = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi)>=10){
                                                equipoVerde = equipoVerde - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi)<10){
                                                equipoVerde = equipoVerde - 5;
                                            }
                                            equipoVerdetv.setText("equipo verde = " + String.valueOf(equipoVerde));
                                            break;
                                        case 20799:
                                            soporteAzul.setText("rssi soporte azul = " + actualRssi);
                                            rssiCarry = readsBc.get(17578);
                                            equipoAzul = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi)>10){
                                                equipoAzul = equipoAzul - 20;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi)<10){
                                                equipoAzul = equipoAzul - 8;
                                            }
                                            equipoAzultv.setText("equipo azul = " + String.valueOf(equipoAzul));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
//                            j--;
                            equiposMap = new TreeMap<Integer, String>();
                            equiposMap.put(equipoRemolacha,"equipoRemolacha");
                            equiposMap.put(equipoCandy, "equipoCandy");
                            equiposMap.put(equipoAmarillo, "equipoAmarillo");
                            equiposMap.put(equipoVerde, "equipoVerde");
                            equiposMap.put(equipoAzul," equipoAzul");
                            //agregar filtros oblicuos de espalda
                            try {
                                candidato = equiposMap.values().toArray()[0].toString();
                                if (candidato.equals(neightborArray[actualPos +1])){
                                    equipoGanador = candidato;
                                    actualPos++;
                                }
                                if (candidato.equals(neightborArray[actualPos -1])){
                                    equipoGanador = candidato;
                                    actualPos--;
                                }
                                if(candidato.equals("")){
                                    candidato = equiposMap.values().toArray()[0].toString();
                                    equipoGanador = candidato;
                                    for (int k=0;k<neightborArray.length;k++){
                                        if (neightborArray[k].equals(equipoGanador)){
                                            actualPos = k;
                                        }
                                    }
                                }
                                j--;
                                if (j==0) {
                                    toptv.setText("AND THE AMI GOES TO " + equipoGanador);
                                    j=3;
                                }

                            }catch (Exception e){

                            }
                            resetearEquipos();
                            equiposMap.clear();

                        }
                    });
                }
            }
        });

//        beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener() {
//            @Override
//            public void onTelemetriesFound(List<EstimoteTelemetry> list) {
//                if (list.size()>0)
//                    Log.v("telemetry","");
//            }
//        });

    }

    private void resetearEquipos() {
        equipoAmarillo = 0;
        equipoRemolacha = 0;
        equipoCandy = 0;
        equipoVerde = 0;
        equipoAzul=0;
    }

    private void fillMap() {
        readsBc = new HashMap<>();
        beaconsSoporte = new HashMap<>();

        readsBc.put(28695, 0);          //minor del lemon1
        readsBc.put(1731, 0);           //minor del remolacha1
        readsBc.put (52909,0);          //minor candy1
        readsBc.put (4739,0);           //minor verde1
        readsBc.put(17578,0);           //minor del azul

        beaconsSoporte.put(28617,0);    //minor del lemon2
        beaconsSoporte.put (27802,0);   //minor candy2
        beaconsSoporte.put (25989,0);   //minor remolacha2
        beaconsSoporte.put(13451,0);    //minor del verde2
        beaconsSoporte.put(20799,0);    //minor del celeste

        neightborArray[0] = "equipoVerde";
        neightborArray[1] = "equipoRemolacha";
        neightborArray[2] = "equipoAzul";
        neightborArray[3] = "equipoCandy";
        neightborArray[4] = "equipoAmarillo";
    }

    private void initViews() {
        rssiBeacon1 = (TextView)findViewById(R.id.rssiBeacon1);
        rssiBeacon2 = (TextView)findViewById(R.id.rssiBeacon2);
        rssiBeacon3 = (TextView)findViewById(R.id.rssiBeacon3);
        rssiBeacon4 = (TextView)findViewById(R.id.rssiBeacon4);
        rssiBeacon5 = (TextView)findViewById(R.id.rssiBeacon5);

        soporteAmarillo = (TextView)findViewById(R.id.soporteAmarillo);
        soporteCandy = (TextView)findViewById(R.id.soporteCandy);
        soporteRemolacha = (TextView)findViewById(R.id.soporteRemolacha);
        soporteVerde = (TextView)findViewById(R.id.soporteVerde);
        soporteAzul = (TextView)findViewById(R.id.soporteAzul);

        equipoAmarillotv = (TextView) findViewById(R.id.equipoAmarillo);
        equipoCandytv = (TextView)findViewById(R.id.equipoCandy);
        equipoRemolachatv = (TextView)findViewById(R.id.equipoRemolacha);
        equipoVerdetv = (TextView)findViewById(R.id.equipoVerde);
        equipoAzultv = (TextView)findViewById(R.id.equipoAzul);

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
//                beaconManager.startTelemetryDiscovery();
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
//        beaconManager.stopTelemetryDiscovery();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
