package com.example.seba.funcionsuavizadora;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.EnumSet;

import Models.ServiceResponse;
import Utils.GsonRequest;
import Utils.Speaker;

/**
 * Created by Sebas on 01/11/2016.
 */

public class SplashActivity extends Activity implements Response.ErrorListener, Response.Listener<ServiceResponse> {

    private final int CHECK_CODE = 0x1;
    private String URL_BASE = "http://private-848b5-iguide.apiary-mock.com/";
    public static String POIS = "POIS";
    private Speaker speaker;
    private boolean returnValue;
    private ImageView image;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView)findViewById(R.id.image);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        checkTTS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                speaker = Speaker.getInstance(getApplicationContext(),this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.v("","");
    }

    @Override
    public void onResponse(ServiceResponse response) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(POIS,response);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void doMoreChecksAndStartMain() {
        if (checkBluetoothAndInet()){
            getStationsAndMoreData();
        }

    }

    private boolean checkBluetoothAndInet() {
        returnValue = true;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            speaker.allow(true);
            speaker.speak("Necesitas acceso a internet para usar esta aplicacion");
            returnValue = false;
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
                            returnValue =false;
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
                        returnValue =false;

                    }
                }
            });
        }
        return returnValue;
    }

    private void getStationsAndMoreData() {
        GsonRequest gsonRequest = new GsonRequest<>(URL_BASE + "minors", ServiceResponse.class , null, this, this);
        Volley.newRequestQueue(this).add(gsonRequest);
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

}
