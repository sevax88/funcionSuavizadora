package com.example.seba.funcionsuavizadora;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Models.Audio;
import Models.Poi;
import Models.ServiceResponse;
import Utils.Speaker;

public class MainActivity extends AppCompatActivity implements SensorEventListener,GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {

    private BeaconManager beaconManager;
    private Region region;
    private Map<Integer,Integer> readsBc,beaconsSoporte;
    private double alpha = 0.8;
    private Integer rssiCarry;
    private int equipoAmarillo,equipoCandy,equipoRemolacha,equipoVerde,equipoAzul;
    private String equipoGanador;
    private Speaker speaker;
    private SensorManager mSensorManager;
    private boolean firstTime = false;
    private float azimuth;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private Sensor gsensor;
    private Sensor msensor;
    private GestureDetector detector;
    private boolean b=true;
    private long lastStep;
    private boolean flagpasillo=true;
    TreeMap<Integer,String> equiposMap = new TreeMap<Integer, String>();
    List<String> equiposOrdenados = new ArrayList<>();
    private String sugerenciaCompleta;
    private int fueraEstacion =0;
    private Poi[] pois;
    private Audio[] audios;
    private String sugerencia;
    private int umbralPi;
    private int umbralPasillo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new GestureDetector(this, this);
        detector.setOnDoubleTapListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        speaker = Speaker.getInstance(getApplicationContext(),null);
        initViews();
        fillMapsAndGetIntet();
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(300,0);
        region = new Region("ranged region", null, null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                if(list.size()==0){
                    fueraEstacion++;
                }
                resetearEquipos();
                equiposMap.clear();
                firstTime=true;
                if (firstTime && list.size()>0) {
                    for (int i = 0; i < list.size(); i++) {
                        speaker.allow(false);
                        Beacon beacon = list.get(i);
                        if (readsBc.containsKey(beacon.getMinor())) {
                            int lastRssi = readsBc.get(beacon.getMinor());
                            int actualRssi = beacon.getRssi() * (-1);
                            actualRssi = (int) (alpha * actualRssi + (1 - alpha) * lastRssi);
                            readsBc.put(beacon.getMinor(), actualRssi);

                        }
                        if (beaconsSoporte.containsKey(beacon.getMinor())) {
                            int lastRssi = beaconsSoporte.get(beacon.getMinor());
                            int actualRssi = beacon.getRssi() * (-1);
                            actualRssi = (int) (alpha * actualRssi + (1 - alpha) * lastRssi);
                            beaconsSoporte.put(beacon.getMinor(), actualRssi);
                            switch (beacon.getMinor()) {
                                case 28617:
                                    rssiCarry = readsBc.get(28695);
                                    equipoAmarillo = rssiCarry + actualRssi;
                                    break;
                                case 27802:
                                    rssiCarry = readsBc.get(52909);
                                    equipoCandy = rssiCarry + actualRssi;
                                    break;
                                case 25989:
                                    rssiCarry = readsBc.get(1731);
                                    equipoRemolacha = rssiCarry + actualRssi;
                                    break;
                                case 49846:
                                    equipoVerde = 80 + actualRssi;
                                    break;
                                case 61868:
                                    rssiCarry = readsBc.get(15063);
                                    equipoAzul = rssiCarry + actualRssi-7;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    equiposMap.put(equipoRemolacha, "Escaleras");
                    equiposMap.put(equipoCandy, "Molinetes");
                    equiposMap.put(equipoAmarillo, "Andén");
                    equiposMap.put(equipoVerde, "Entrada");
                    equiposMap.put(equipoAzul, "Baños");
                    Log.v("primero",equiposMap.firstKey().toString());
                    try {

                        long diffLastStep = System.currentTimeMillis() - lastStep;
                        if (equipoGanador!=null && !equiposMap.values().toArray()[0].toString().equals(equipoGanador)){
                            b=true;
                        }
                        if (diffLastStep < 5500 &&  equiposMap.firstKey()>0 && equiposMap.firstKey()<= umbralPi && b ) {
                            equipoGanador = equiposMap.values().toArray()[0].toString();
                            speaker.allow(true);
                            speaker.speak(equipoGanador.toString());
                            speaker.allow(false);
                            b = false;
                            flagpasillo = true;

                        }else if(diffLastStep < 5500 &&  equiposMap.firstKey()>0 && equiposMap.firstKey()> umbralPasillo && flagpasillo){
                            equipoGanador = "Pasillo";
                            speaker.allow(true);
                            speaker.speak(equipoGanador.toString());
                            speaker.allow(false);
                            flagpasillo=false;
                        }

                    } catch (Exception e) {

                    }
                }
                if(fueraEstacion >25){
                    fueraEstacion =0;
                    speaker.allow(true);
                    speaker.speak("No estás en ninguna estacion de subte");
                }
            }
        });

    }

    private void resetearEquipos() {
        equipoAmarillo = 0;
        equipoRemolacha = 0;
        equipoCandy = 0;
        equipoVerde = 0;
        equipoAzul=0;
    }

    private void fillMapsAndGetIntet() {

        Parcelable response  = getIntent().getExtras().getParcelable(SplashActivity.POIS);
        audios = ((ServiceResponse)response).getAudios();
        pois = ((ServiceResponse)response).getListminor();
//        umbralPi = ((ServiceResponse)response).getUmbralPi();
//        umbralPasillo = ((ServiceResponse)response).getUmbralPasillo();
        readsBc = new HashMap<>();
        beaconsSoporte = new HashMap<>();

        readsBc.put(pois[7].getMinor(), 0);          //minor del lemon1
        readsBc.put(pois[3].getMinor(), 0);           //minor del remolacha1
        readsBc.put (pois[5].getMinor(),0);          //minor candy1
        beaconsSoporte.put(pois[2].getMinor(),0);   //minor celeeste grande
        readsBc.put(pois[1].getMinor(),0);           //minor del azulgrande

        beaconsSoporte.put(pois[8].getMinor(),0);    //minor del lemon2
        beaconsSoporte.put(pois[6].getMinor(),0);   //minor candy2
        beaconsSoporte.put(pois[4].getMinor(),0);   //minor remolacha2
        beaconsSoporte.put(pois[0].getMinor(),0);    //minor del verdegrande
    }

    private void initViews() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gsensor,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);

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
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float beta = 0.97f;
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = beta * mGravity[0] + (1 - beta)* event.values[0];
                mGravity[1] = beta * mGravity[1] + (1 - beta)* event.values[1];
                mGravity[2] = beta * mGravity[2] + (1 - beta)* event.values[2];
                if (mGravity[2]>=9.8){
                    lastStep = System.currentTimeMillis();
                }

            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = beta * mGeomagnetic[0] + (1 - beta)* event.values[0];
                mGeomagnetic[1] = beta * mGeomagnetic[1] + (1 - beta)* event.values[1];
                mGeomagnetic[2] = beta * mGeomagnetic[2] + (1 - beta)*event.values[2];
            }

            float Ri[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(Ri, I, mGravity,mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(Ri, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        suguerirDestinos(equipoGanador,azimuth);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private void suguerirDestinos(String equipoGanador, float azimuth) {
        speaker.allow(true);
        sugerencia = "";
        if (!equipoGanador.equals("Pasillo")) {
            switch (equipoGanador){
                case "Entrada":
                if (azimuth > 0 && azimuth < 35) {
                    sugerencia = audios[11].getAudio();break;
                } else {
                    sugerencia = audios[12].getAudio();break;
                }

                case "Escaleras":
                    if (azimuth > 230 && azimuth < 280) {
                        sugerencia = audios[13].getAudio();break;
                    } else if (azimuth > 280 && azimuth < 320) {
                        sugerencia = audios[14].getAudio();break;
                    } else if (azimuth > 320 && azimuth < 359) {
                        sugerencia = audios[15].getAudio();break;
                    } else {
                        sugerencia = audios[16].getAudio();break;
                    }
                case "Baños" :
                    if (azimuth > 185 && azimuth < 230) {
                        sugerencia = audios[17].getAudio();break;
                    } else if (azimuth > 130 && azimuth < 185) {
                        sugerencia = audios[18].getAudio();break;
                    } else if (azimuth > 50 && azimuth < 130) {
                        sugerencia = audios[19].getAudio();break;
                    } else {
                        sugerencia = audios[20].getAudio();break;
                    }
                case "Molinetes" :
                    if (azimuth > 190 && azimuth < 220) {
                        sugerencia = audios[21].getAudio();break;
                    } else if (azimuth < 240 && azimuth > 220) {
                        sugerencia = audios[22].getAudio();break;
                    } else if (azimuth > 245 && azimuth < 285) {
                        sugerencia = audios[23].getAudio();break;
                    } else {
                        sugerencia = audios[24].getAudio();break;
                }

                case "Andén":
                    if (azimuth > 185 && azimuth < 225) {
                        sugerencia = audios[25].getAudio();break;
                    } else if (azimuth > 165 && azimuth < 185) {
                        sugerencia = audios[26].getAudio();break;
                    } else {
                        sugerencia = audios[27].getAudio();break;
                }
            }
        }
        else {
            equiposOrdenados.addAll(equiposMap.values());
            if ((azimuth>0 && azimuth<20)  &&(equiposOrdenados.get(0).equals("Andén") || equiposOrdenados.get(1).equals("Andén"))) {
                sugerencia = audios[28].getAudio();
            } else if (azimuth > 295 && azimuth < 360 ) {
                sugerencia = audios[29].getAudio();
            } else if (azimuth > 50 && azimuth < 100 ) {
                if (equipoCandy < equipoRemolacha ) {
                    sugerencia = audios[30].getAudio();
                } else {
                    sugerencia = audios[31].getAudio();
                }
            }
            else if(equipoVerde<143){
                sugerencia = audios[32].getAudio();
            }
            equiposOrdenados.clear();
        }
        speaker.speak(sugerencia);
    }



    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        speaker.allow(true);
        if (!equipoGanador.equals("Pasillo")) {
            switch (equipoGanador){
                case "Entrada": sugerenciaCompleta = audios[0].getAudio(); break;
                case "Escaleras": sugerenciaCompleta = audios[1].getAudio();break;
                case "Baños": sugerenciaCompleta = audios[2].getAudio();break;
                case "Molinetes":sugerenciaCompleta = audios[3].getAudio();break;
                case "Andén": sugerenciaCompleta = audios[4].getAudio();break;
            }
        }
        else{
            equiposOrdenados.addAll(equiposMap.values());
            String ganadorDspPasillo = equiposOrdenados.get(0);
            //en el pasillo yendo hacia el norte
            if (azimuth>0 && azimuth<60){
                switch (ganadorDspPasillo){
                    case "Andén": sugerenciaCompleta = audios[5].getAudio();break;
                    case "Baños": sugerenciaCompleta = audios[6].getAudio();break;
                    case "Escaleras": sugerenciaCompleta = audios[7].getAudio();break;
                }
            }
            //en el pasillo yendo hacia el sur
            if(azimuth>120 && azimuth<250){
                switch (ganadorDspPasillo){
                    case "Andén" : sugerenciaCompleta = audios[8].getAudio();break;
                    case "Baños" : sugerenciaCompleta = audios[9].getAudio();break;
                    case "Escaleras" : sugerenciaCompleta = audios[10].getAudio();break;
                }
            }
            equiposOrdenados.clear();

        }
        speaker.speak(sugerenciaCompleta);
    }



    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        speaker.allow(true);
        speaker.speak(equipoGanador);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }




}
