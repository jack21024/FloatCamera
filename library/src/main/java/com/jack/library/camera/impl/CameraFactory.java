package com.jack.library.camera.impl;

import android.hardware.Camera;
import android.os.Build;

import com.jack.library.camera.Cameras;

/**
 * Created by jacktseng on 2015/8/8.
 */
@Deprecated
public class CameraFactory {

    private static Cameras CAMERA;

    private static final Object FINAL = new Object() {
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            if(CAMERA != null)
                CAMERA.release();
            CAMERA = null;
        }
    };

    public static Cameras getInstance() {
        if(CAMERA == null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //api 21

            }
            else {
                CAMERA = new Camera1(Camera.open());
            }
        }

        return CAMERA;
    }

}
