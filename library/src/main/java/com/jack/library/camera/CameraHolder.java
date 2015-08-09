package com.jack.library.camera;

import android.view.SurfaceHolder;

/**
 * Created by jacktseng on 2015/6/20.
 */
public interface CameraHolder extends ICameraParameter, ICameraCapture, ICameraOrientation {

    public Cameras getCamera();

    public void setPreviewHolder(SurfaceHolder holder);

    public void start() throws Exception;

    public void stop() throws Exception;

}
