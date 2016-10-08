package Utils;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Sebas on 07/10/2016.
 */
public class ActivityRecognizedService extends IntentService {
    private Activity activity;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }



    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (int j=0;j<probableActivities.size();j++){
            DetectedActivity detectedActivity = probableActivities.get(j);
            if (detectedActivity.getType() == DetectedActivity.STILL && detectedActivity.getConfidence()>75){
            }
        }
    }
}
