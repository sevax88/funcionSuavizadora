package com.example.seba.funcionsuavizadora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import Models.Poi;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkTTS();
    }

    private void checkStation() {
        GsonRequest gsonRequest = new GsonRequest<>(URL_BASE + "minors", ServiceResponse.class , null, this, this);
        Volley.newRequestQueue(this).add(gsonRequest);
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
                Speaker.getInstance(getApplicationContext(),this);
            } else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

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

    public void launchMain() {
        checkStation();
    }
}
