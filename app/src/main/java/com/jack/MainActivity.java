package com.jack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jack.service.FloatCameraService;

public class MainActivity extends Activity {

    public static final String TAG = "FloatCameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(FloatCameraService.ACTION);
        startService(intent);
        finish();
    }

}
