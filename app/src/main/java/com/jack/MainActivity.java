package com.jack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jack.service.FloatCameraService;

/**
 * Note: This application is not support api level 21 upper.
 * <p/>
 * This class just a entry point of this application, and the real main class is FloatCameraService.
 * <br/>In order to use float window without any pages, we uses the service instead of the activity
 * to create views dynamically.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class MainActivity extends Activity {

    public static final String TAG = "FloatCameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Launching the FloatCameraService to work and destroys itself
         */
        Intent intent = new Intent(FloatCameraService.ACTION);
        startService(intent);
        finish();
    }

}
