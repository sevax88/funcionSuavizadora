package Utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.example.seba.funcionsuavizadora.MainActivity;
import com.example.seba.funcionsuavizadora.SplashActivity;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Sebas on 03/10/2016.
 */
public class Speaker implements TextToSpeech.OnInitListener {

    private static Speaker instance;
    private static  Context context;
    private TextToSpeech tts;
    private static SplashActivity msplashActivity;

    private boolean ready = false;

    private boolean allowed = false;

    public static Speaker getInstance(Context contexto, SplashActivity splashActivity){
        if (instance == null) {
            context = contexto;
            instance = new Speaker();
            msplashActivity = splashActivity;
            return instance;
        }
        else return instance;
    }

    public Speaker (){
        tts = new TextToSpeech(context,this);
    }

    public boolean isAllowed(){
        return allowed;
    }

    public void allow(boolean allowed){
        this.allowed = allowed;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            // Change this to match your
            // locale
//            tts.setLanguage(Locale.ENGLISH);
            tts.setLanguage(new Locale("spa","ESP"));
            ready = true;
            Intent intent = new Intent(context,MainActivity.class);
            msplashActivity.startActivity(intent);
            msplashActivity.finish();
        }else{
            ready = false;
        }
    }

    public void speak(String text) {

        // Speak only if the TTS is ready
        // and the user has allowed speech

        if (ready && allowed) {
            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,String.valueOf(AudioManager.STREAM_NOTIFICATION));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }

    public void pause(int duration){
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void destroy(){
        tts.shutdown();
    }


}
