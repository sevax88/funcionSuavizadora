package com.example.seba.funcionsuavizadora;

import android.app.Activity;
import android.os.Bundle;

import com.estimote.sdk.EstimoteSDK;

/**
 * Created by seba on 19/09/2016.
 */
public class MyApplication extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), "com-example-seba-funcionsu-7kw", "bd21e09a23281f9817b176c24713bdea");
// Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);
    }
}
