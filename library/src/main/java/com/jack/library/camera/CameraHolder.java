package com.jack.library.camera;

import android.view.SurfaceHolder;

import com.jack.library.camera.feature.ICameraCapture;
import com.jack.library.camera.feature.ICameraOrientation;
import com.jack.library.camera.feature.ICameraParameter;

/**
 * CameraHolder is a agent which accessing to the camera object on the device.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface CameraHolder extends ICameraParameter, ICameraCapture, ICameraOrientation {

    /**
     * Gets a camera object of the device
     *
     * @return
     */
    public Cameras getCamera();

    /**
     * Sets the Surface to be used for live preview
     *
     * @param holder
     */
    public void setPreviewHolder(SurfaceHolder holder);

    /**
     * Starts capturing and drawing preview frames to the screen
     *
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * Stops capturing and drawing preview frames to the screen
     *
     * @throws Exception
     */
    public void stop() throws Exception;

}
