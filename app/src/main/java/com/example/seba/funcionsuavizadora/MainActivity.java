package com.example.seba.funcionsuavizadora;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

import Models.Poi;
import Utils.Speaker;

public class MainActivity extends AppCompatActivity implements SensorEventListener,GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {

    private TextView rssiBeacon1,rssiBeacon2,rssiBeacon3,rssiBeacon4,rssiBeacon5,toptv,actualPostv,azimuthtv;
    private BeaconManager beaconManager;
    private Region region;
    private int listenerCount = 0;
    private Map<Integer,Integer> readsBc,beaconsSoporte;
    private double alpha = 0.7;
    private TextView soporteAmarillo,soporteCandy,soporteRemolacha,equipoAmarillotv,equipoCandytv,equipoRemolachatv,equipoVerdetv,soporteVerde,soporteAzul,equipoAzultv;
    private Integer rssiCarry;
    private int equipoAmarillo,equipoCandy,equipoRemolacha,equipoVerde,equipoAzul;
    private String equipoGanador;
    private TreeMap<Integer, String> equiposMap;
    private Poi neightborArray[] = new Poi[5];
    private int actualPos ;
    private String candidato = "";
    private final int CHECK_CODE = 0x1;
    private Speaker speaker;
    private final int SHORT_DURATION = 1200;
    private SensorManager mSensorManager;
    private boolean firstTime = true;
    private Handler handler = new Handler();
    private float azimuth;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    final float beta = 0.97f;
    private Sensor gsensor;
    private Sensor msensor;
    private HashMap<String,HashMap<String,String>> mapLocationAudios = new HashMap<>() ;
    private GestureDetector detector;
    int j=2;
    private boolean b=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        detector = new GestureDetector(this, this);
//        detector.setOnDoubleTapListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        checkTTS();

        initViews();
        fillMaps();
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(350,0);
        region = new Region("ranged region", null, null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                Log.v("onBeaconDiscover", "se descubrio un beacon - tiempo: " + System.currentTimeMillis() + " list size =" + list.size());
                if (list.size()==10) {
                    Log.v("entrada al listener", String.valueOf(listenerCount));

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < list.size(); i++) {
                                speaker.allow(false);
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
                                            if (Math.abs(rssiCarry - actualRssi) < 10) {
                                                equipoCandy = equipoCandy - 5;
                                            }
                                            equipoCandytv.setText("equipo candy = " + String.valueOf(equipoCandy));
                                            break;
                                        case 25989:
                                            soporteRemolacha.setText("rssi soporte remolacha = " + actualRssi);
                                            rssiCarry = readsBc.get(1731);
                                            equipoRemolacha = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi) > 10) {
                                                equipoRemolacha = equipoRemolacha - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi) < 10) {
                                                equipoRemolacha = equipoRemolacha - 5;
                                            }
                                            equipoRemolachatv.setText("equipo remolacha = " + String.valueOf(equipoRemolacha));
                                            break;
                                        case 13451:
                                            soporteVerde.setText("rssi soporte verde = " + actualRssi);
                                            rssiCarry = readsBc.get(4739);
                                            equipoVerde = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi) >= 10) {
                                                equipoVerde = equipoVerde - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi) < 10) {
                                                equipoVerde = equipoVerde - 5;
                                            }
                                            equipoVerdetv.setText("equipo verde = " + String.valueOf(equipoVerde));
                                            break;
                                        case 20799:
                                            soporteAzul.setText("rssi soporte azul = " + actualRssi);
                                            rssiCarry = readsBc.get(17578);
                                            equipoAzul = rssiCarry + actualRssi;
                                            if (Math.abs(rssiCarry - actualRssi) > 10) {
                                                equipoAzul = equipoAzul - 10;
                                            }
                                            if (Math.abs(rssiCarry - actualRssi) < 10) {
                                                equipoAzul = equipoAzul - 5;
                                            }
                                            equipoAzultv.setText("equipo azul = " + String.valueOf(equipoAzul));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            equiposMap = new TreeMap<Integer, String>();
                            equiposMap.put(equipoRemolacha, "equipoRemolacha");
                            equiposMap.put(equipoCandy, "equipoCandy");
                            equiposMap.put(equipoAmarillo, "equipoAmarillo");
                            equiposMap.put(equipoVerde, "equipoVerde");
                            equiposMap.put(equipoAzul, "equipoAzul");
                            try {

                                if (equipoGanador!=null && !equiposMap.values().toArray()[0].toString().equals(equipoGanador)){
                                    b=true;
                                }

                                if ( equiposMap.firstKey()<=133 && b) {
                                    equipoGanador = equiposMap.values().toArray()[0].toString();
                                    toptv.setText("AND THE AMI GOES TO " + equipoGanador);
                                    speaker.allow(true);
                                    speaker.speak(equipoGanador.toString());
                                    speaker.allow(false);
                                    b = false;

                                }

                            } catch (Exception e) {

                            }
                            resetearEquipos();
                            equiposMap.clear();

                        }
                    }, 4000);
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

    private void fillMaps() {
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


        neightborArray[0] = new Poi("equipoVerde","Entrada");
        neightborArray[1] = new Poi("equipoRemolacha","Escaleras");
        neightborArray[2] = new Poi("equipoAzul", "Baños");
        neightborArray[3] = new Poi("equipoCandy","Molinetes");
        neightborArray[4] = new Poi("equipoAmarillo","Anden");

        HashMap<String,String> equipoVerdeSubMap = new HashMap<>();
        equipoVerdeSubMap.put("Norte","Para ir a las escaleras primera salida a la derecha,dos metros");
        equipoVerdeSubMap.put("Este","");
        equipoVerdeSubMap.put("Oeste","");
        equipoVerdeSubMap.put("Sur","");
        mapLocationAudios.put("equipoVerde",equipoVerdeSubMap);

        HashMap<String,String> equipoRemolachaSubMap = new HashMap<>();
        equipoRemolachaSubMap.put("Norte","Para ir a los molinetes,primera salida a la derecha, dos metros");
        equipoRemolachaSubMap.put("Este","Para ir a los Baños,siga derecho dos metros");
        equipoRemolachaSubMap.put("Oeste","");
        equipoRemolachaSubMap.put("Sur","");
        mapLocationAudios.put("equipoRemolacha",equipoRemolachaSubMap);

        HashMap<String,String> equipoAzulSubMap = new HashMap<>();
        equipoAzulSubMap.put("Norte","Para ir al anden primera salida a la izquierda,dos metros");
        equipoAzulSubMap.put("Este","");
        equipoAzulSubMap.put("Oeste","Para ir a los molinetes,siga derecho,dos metros");
        equipoAzulSubMap.put("Sur","Para ir a la entrada,primera salida a la derecha,dos metros");
        mapLocationAudios.put("equipoAzul",equipoAzulSubMap);

        HashMap<String,String> equipoCandySubMap = new HashMap<>();
        equipoCandySubMap.put("Norte","");
        equipoCandySubMap.put("Este","Para ir al anden,segui derecho,dos metros");
        equipoCandySubMap.put("Oeste","");
        equipoCandySubMap.put("Sur","Para ir a los molinetes, primera salida a la izquierda,dos metros");
        mapLocationAudios.put("equipoCandy",equipoCandySubMap);

        HashMap<String,String> equipoAmarilloSubMap = new HashMap<>();
        equipoAmarilloSubMap.put("Norte","");
        equipoAmarilloSubMap.put("Este","");
        equipoAmarilloSubMap.put("Oeste","Para ir a los molinetes,siga derecha,dos metros");
        equipoAmarilloSubMap.put("Sur","Para ir a los baños,primera salida a la derecha,dos metros");
        mapLocationAudios.put("equipoAmarillo",equipoAmarilloSubMap);

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
        actualPostv = (TextView)findViewById(R.id.actualPostv);
        azimuthtv = (TextView)findViewById(R.id.azimuthtv);
    }


    @Override
    protected void onResume() {
        super.onResume();
        gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gsensor,SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, msensor,SensorManager.SENSOR_DELAY_GAME);

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

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = new Speaker(this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float beta = 0.97f;
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = beta * mGravity[0] + (1 - beta)* event.values[0];
                mGravity[1] = beta * mGravity[1] + (1 - beta)* event.values[1];
                mGravity[2] = beta * mGravity[2] + (1 - beta)* event.values[2];
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
                azimuthtv.setText(String.valueOf(azimuth));
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
//        suguerirDestinos(equipoGanador,azimuth);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private void suguerirDestinos(String equipoGanador, float azimuth) {
        String direccion = "Norte";
        if ((azimuth >= 345 && azimuth < 360) || (azimuth >=0 && azimuth < 75)){
            direccion = "Norte";
        }
        if (azimuth >= 75 && azimuth < 165){
            direccion = "Oeste";
        }
        if (azimuth >= 165 && azimuth < 255){
            direccion = "Sur";
        }
        if (azimuth >= 255 & azimuth < 345){
            direccion = "Este";
        }

        String sugerencia = mapLocationAudios.get(equipoGanador).get(direccion);
        speaker.allow(true);
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

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
