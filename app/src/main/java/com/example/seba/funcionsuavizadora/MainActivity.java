package com.example.seba.funcionsuavizadora;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.internal.spdy.FrameReader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Models.Poi;
import Utils.Speaker;

public class MainActivity extends AppCompatActivity implements SensorEventListener,GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {

    private TextView rssiBeacon1,rssiBeacon2,rssiBeacon3,rssiBeacon4,rssiBeacon5,toptv,azimuthtv;
    private BeaconManager beaconManager;
    private Region region;
    private int listenerCount = 0;
    private Map<Integer,Integer> readsBc,beaconsSoporte;
    private double alpha = 0.8;
    private TextView soporteAmarillo,soporteCandy,soporteRemolacha,equipoAmarillotv,equipoCandytv,equipoRemolachatv,equipoVerdetv,soporteVerde,soporteAzul,equipoAzultv;
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
    private LinearLayout linearfiltroPasos;
    private boolean flagpasillo=true;
    TreeMap<Integer,String> equiposMap = new TreeMap<Integer, String>();
    List<String> equiposOrdenados = new ArrayList<>();
    private String sugerenciaCompleta;
    private int fuertaEstacion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new GestureDetector(this, this);
        detector.setOnDoubleTapListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        speaker = Speaker.getInstance(getApplicationContext(),null);
        checkBluetoothAndInet();
        initViews();
        fillMaps();
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(300,0);
        region = new Region("ranged region", null, null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
                if(list.size()==0){
                    fuertaEstacion++;
                }
                resetearEquipos();
                equiposMap.clear();
                long yourmilliseconds = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
                Date resultdate = new Date(yourmilliseconds);
                Log.v("onBeaconDiscover", "se descubrio un beacon - tiempo: " + sdf.format(resultdate) + " list size =" + list.size());
                firstTime=true;
                if (firstTime && list.size()>0) {
                    Log.v("entrada al listener", String.valueOf(listenerCount));
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
                                        case 15063:
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
                                            equipoAmarillotv.setText("equipo amarillo = " + String.valueOf(equipoAmarillo));
                                            break;
                                        case 27802:
                                            soporteCandy.setText("rssi  soporte candy = " + actualRssi);
                                            rssiCarry = readsBc.get(52909);
                                            equipoCandy = rssiCarry + actualRssi;
                                            equipoCandytv.setText("equipo candy = " + String.valueOf(equipoCandy));
                                            break;
                                        case 25989:
                                            soporteRemolacha.setText("rssi soporte remolacha = " + actualRssi);
                                            rssiCarry = readsBc.get(1731);
                                            equipoRemolacha = rssiCarry + actualRssi;
                                            equipoRemolachatv.setText("equipo remolacha = " + String.valueOf(equipoRemolacha));
                                            break;
                                        case 49846:
                                            soporteVerde.setText("rssi soporte verde = " + actualRssi);
                                            equipoVerde = 80 + actualRssi;
                                            equipoVerdetv.setText("equipo verde = " + String.valueOf(equipoVerde));
                                            break;
                                        case 61868:
                                            soporteAzul.setText("rssi soporte azul = " + actualRssi);
                                            rssiCarry = readsBc.get(15063);
                                            equipoAzul = rssiCarry + actualRssi-7;
                                            equipoAzultv.setText("equipo azul = " + String.valueOf(equipoAzul));
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

                                if (diffLastStep>5500){
                                    linearfiltroPasos.setBackgroundColor(Color.RED);
                                }

                                else {
                                    linearfiltroPasos.setBackgroundColor(Color.WHITE);
                                }
                                if (equipoGanador!=null && !equiposMap.values().toArray()[0].toString().equals(equipoGanador)){
                                    b=true;
                                }
                                if (diffLastStep < 5500 &&  equiposMap.firstKey()>0 && equiposMap.firstKey()<=133 && b ) {
                                    equipoGanador = equiposMap.values().toArray()[0].toString();
                                    toptv.setText("AND THE AMI GOES TO " + equipoGanador);
                                    speaker.allow(true);
                                    speaker.speak(equipoGanador.toString());
                                    speaker.allow(false);
                                    b = false;
                                    flagpasillo = true;

                                }else if(diffLastStep < 5500 &&  equiposMap.firstKey()>0 && equiposMap.firstKey()>140 && flagpasillo){
                                    equipoGanador = "Pasillo";
                                    toptv.setText("AND THE AMI GOES TO " + equipoGanador);
                                    speaker.allow(true);
                                    speaker.speak(equipoGanador.toString());
                                    speaker.allow(false);
                                    flagpasillo=false;
                                }

                            } catch (Exception e) {

                            }

                }
                if(fuertaEstacion>25){
                    fuertaEstacion=0;
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

    private void fillMaps() {

        Parcelable[] pois  = getIntent().getExtras().getParcelableArray(SplashActivity.POIS);
        readsBc = new HashMap<>();
        beaconsSoporte = new HashMap<>();

        readsBc.put(((Poi)pois[7]).getMinor(), 0);          //minor del lemon1
        readsBc.put(((Poi)pois[3]).getMinor(), 0);           //minor del remolacha1
        readsBc.put (((Poi)pois[5]).getMinor(),0);          //minor candy1
        beaconsSoporte.put (((Poi)pois[2]).getMinor(),0);   //minor celeeste grande
        readsBc.put(((Poi)pois[1]).getMinor(),0);           //minor del azulgrande

        beaconsSoporte.put(((Poi)pois[8]).getMinor(),0);    //minor del lemon2
        beaconsSoporte.put (((Poi)pois[6]).getMinor(),0);   //minor candy2
        beaconsSoporte.put (((Poi)pois[4]).getMinor(),0);   //minor remolacha2
        beaconsSoporte.put(((Poi)pois[0]).getMinor(),0);    //minor del verdegrande


//        readsBc.put(28695, 0);          //minor del lemon1
//        readsBc.put(1731, 0);           //minor del remolacha1
//        readsBc.put (52909,0);          //minor candy1
//        beaconsSoporte.put (61868,0);   //minor celeeste grande
//        readsBc.put(15063,0);           //minor del azulgrande
//
//        beaconsSoporte.put(28617,0);    //minor del lemon2
//        beaconsSoporte.put (27802,0);   //minor candy2
//        beaconsSoporte.put (25989,0);   //minor remolacha2
//        beaconsSoporte.put(49846,0);    //minor del verdegrande

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
        azimuthtv = (TextView)findViewById(R.id.azimuthtv);
        linearfiltroPasos = (LinearLayout)findViewById(R.id.filtroPasos);
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
        suguerirDestinos(equipoGanador,azimuth);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private void suguerirDestinos(String equipoGanador, float azimuth) {
        speaker.allow(true);
        if (!equipoGanador.equals("Pasillo")) {
            String sugerencia = "Entrada";
            if (equipoGanador.equals("Entrada")) {
                if (azimuth > 0 && azimuth < 35) {
                    sugerencia = "Baños,primera salida a la izquierda,dos metros,segunda salida a la izquierda andén,cuatro metros";
                } else {
                    sugerencia = "Escaleras,primera salida a la deracha, dos metros, segunda salida a la derecha molinetes,cuatro metros";
                }
            }
            if (equipoGanador.equals("Escaleras")) {
                if (azimuth > 230 && azimuth < 280) {
                    sugerencia = "Entrada,primera salida a la derecha, dos metros";
                } else if (azimuth > 280 && azimuth < 320) {
                    sugerencia = "Baños,primera salida a la izquierda,dos metros";
                } else if (azimuth > 320 && azimuth < 359) {
                    sugerencia = "Andén,segunda salida a la izquierda,cuatro metros";
                } else {
                    sugerencia = "Molinetes,proxima salida a la derecha,dos metros";
                }
            }
            if (equipoGanador.equals("Baños")) {
                if (azimuth > 185 && azimuth < 230) {
                    sugerencia = "Entrada, primera salida a la derecha, dos metros";
                } else if (azimuth > 130 && azimuth < 185) {
                    sugerencia = "Escaleras,primera salida a la izquierda,dos metros";
                } else if (azimuth > 50 && azimuth < 130) {
                    sugerencia = "Molinetes,primera salida a la derecha, dos metros";
                } else {
                    sugerencia = "Andén,primera salida a la izquierda, dos metros";
                }
            }

            if (equipoGanador.equals("Molinetes")) {
                if (azimuth > 190 && azimuth < 220) {
                    sugerencia = "Escaleras,primera salida a la izquierda,dos metros";
                } else if (azimuth < 240 && azimuth > 220) {
                    sugerencia = "Entrada, segunda salida a la derecha, cuatro metros";
                } else if (azimuth > 245 && azimuth < 285) {
                    sugerencia = "Baños,primera salida a la derecha, dos metros";
                } else {
                    sugerencia = "Andén,primera salida a la izquierda, dos metros";
                }
            }

            if (equipoGanador.equals("Andén")) {
                if (azimuth > 185 && azimuth < 225) {
                    sugerencia = "Baños,primera salida a la derecha,dos metros,segunda salida a la derecha entrada,cuatro metros";
                } else if (azimuth > 165 && azimuth < 185) {
                    sugerencia = "Escaleras,segunda salida a la izquierda,cuatro metros";
                } else {
                    sugerencia = "Molinetes, primera salida a la izquierda,dos metros";
                }
            }
            speaker.speak(sugerencia);
        } else {
            //en el pasillo yendo hacia el norte
            equiposOrdenados.addAll(equiposMap.values());
            if (((azimuth > 340&& azimuth < 360)||azimuth<20)  &&(equiposOrdenados.get(0).equals("Andén") || equiposOrdenados.get(1).equals("Andén"))) {
                speaker.speak("Estás en el pasillo,hacia la izquierda está el andén a dos metros");
            } else if (azimuth > 295 && azimuth < 340 && equipoAzul < equipoAmarillo && equipoCandy<equipoRemolacha) {
                speaker.speak("Estás en el pasillo, baños hacia la izquierda a un metro");
            } else if (azimuth > 50 && azimuth < 100 && equipoAzul>155) {
                    if (equipoCandy < equipoRemolacha ) {
                        speaker.speak("Estás en el pasillo,a la derecha a un metro están los molinetes");
                    } else {
                        speaker.speak("Estás en el pasillo, a la derecha a un metro están las escaleras");
                    }
            }
            //en el pasillo yendo hacia el sur
            else if (azimuth > 300 && azimuth < 350 && equipoCandy>equipoRemolacha && equipoAzul<equipoAmarillo) {
                speaker.speak("Estás en el pasillo, hacia la derecha los baños a un metro");
            }
            else if(azimuth>50 && azimuth<100){
                if (equipoCandy<equipoRemolacha){
                    speaker.speak("Estás en el pasillo, los molinetes están a la izquierda, a un metro");
                }else {
                    speaker.speak("Estás en el pasillo, las escaleras están a la izquierda, a un metro");
                }
            }
            else if(equipoVerde<143){
                speaker.speak("Estás en el pasillo, la entrada está hacia la derecha a un metro");
            }
            equiposOrdenados.clear();
        }
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
        if (!equipoGanador.equals("Pasillo")) {
            if (equipoGanador.equals("equipoVerde")) {
                sugerenciaCompleta = "Baños y andén a la izquierda.Escaleras y molinetes a la derecha";
            } else if (equipoGanador.equals("equipoRemolacha")) {
                sugerenciaCompleta = "Hacia la izquierda la entrada.Hacia la dereche baños,molinetes y andén";
            } else if (equipoGanador.equals("equipoAzul")) {
                sugerenciaCompleta = "Hacia la derecha escaleras y entrada. Hacia la izquierda molinetes,y andén";
            } else if (equipoGanador.equals("equipoCandy")) {
                sugerenciaCompleta = "Hacia la derecha andén.Hacia la izquierda baños,escaleras y entrada";
            } else {
                sugerenciaCompleta = "Hacia la derecha molinetes,baños,escaleras y entrada";
            }
            speaker.allow(true);
            speaker.speak(sugerenciaCompleta);
        }
            //esta en el pasillo
        }



    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    private void checkBluetoothAndInet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            speaker.allow(true);
            speaker.speak("Necesitas acceso a internet para usar esta aplicacion");
            finish();
        } else {
            SystemRequirementsChecker.check(this, new SystemRequirementsChecker.Callback() {
                @Override
                public void onRequirementsMissing(EnumSet<SystemRequirementsChecker.Requirement> enumSet) {
                    if (enumSet.contains(SystemRequirementsChecker.Requirement.BLUETOOTH_DISABLED)) {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter!=null){
                            bluetoothAdapter.enable();
                        }else {
                            speaker.allow(true);
                            speaker.speak("Necesitas bluetooth para utilizar esta aplicación");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },4000);

                        }

                    } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        speaker.allow(true);
                        speaker.speak("Necesitas una version más nueva de bluetooth para utilizar esta aplicacion");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },4000);
                    }
                }
            });
        }
    }

}
