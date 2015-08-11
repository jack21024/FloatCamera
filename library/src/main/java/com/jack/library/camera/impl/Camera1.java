package com.jack.library.camera.impl;

import android.hardware.Camera;

import com.jack.library.camera.Cameras;

/**
 * Camera1 is an instance of Cameras of library that supporting Camera of android api level 21
 * before.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class Camera1 implements Cameras<Camera> {

    /**
     * Deprecated on android api level 21
     */
    private Camera mCamera;

    Camera1(Camera camera) {
       mCamera = camera;
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public void start() {
        if(mCamera == null) return;
        mCamera.startPreview();
    }

    @Override
    public void stop() {
        if(mCamera == null) return;
        mCamera.stopPreview();
    }

    @Override
    public void release() {
        if(mCamera == null) return;
        stop();
        mCamera.release();
    }
}
