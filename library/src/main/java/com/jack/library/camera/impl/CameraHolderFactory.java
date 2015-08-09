package com.jack.library.camera.impl;

import android.hardware.Camera;
import android.os.Build;

import com.jack.library.camera.CameraHolderManager;

/**
 * Created by jacktseng on 2015/8/9.
 */
class CameraHolderFactory {

    private static CameraHolderManager INSTANCE;

    private static final Object FINAL = new Object() {
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            if(INSTANCE != null)
                INSTANCE.release();
            INSTANCE = null;
        }
    };

    public static CameraHolderManager getInstance() {
        if(INSTANCE != null)
            INSTANCE.release();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //api 21

        }
        else {
            INSTANCE = new CameraHolderImpl(new Camera1(Camera.open()));
        }

        return INSTANCE;
    }

}
