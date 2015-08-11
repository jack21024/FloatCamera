package com.jack.library.camera.impl;

import android.hardware.Camera;
import android.os.Build;

import com.jack.library.camera.CameraHolderKeeper;

/**
 * This class is used to create an singleton instance of CameraHolder which according to device's
 * android api level.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
class CameraHolderFactory {

    private static CameraHolderKeeper INSTANCE;

    /**
     * This is a taps to handle the static finalize event
     */
    private static final Object FINAL = new Object() {
        @Override
        protected void finalize() throws Throwable {
            super.finalize();

            /**
             * Make sure the camera to release when class be destroyed
             */
            if(INSTANCE != null)
                INSTANCE.release();
            INSTANCE = null;
        }
    };

    public static CameraHolderKeeper getInstance() {
        if(INSTANCE != null)
            INSTANCE.release();

        /**
         * Because Camera2 replaces deprecated Camera class since API level 21 , so the cameraHelper
         * is needed to resolve compatibility between Camera class and Camera2 class
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //api 21 for Camera2
            //not implemented
        }
        else { //for Camera
            INSTANCE = new CameraHolderImpl(new Camera1(Camera.open()));
        }

        return INSTANCE;
    }

}
