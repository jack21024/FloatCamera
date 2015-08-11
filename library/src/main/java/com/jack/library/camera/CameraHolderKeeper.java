package com.jack.library.camera;

/**
 * CameraHolderKeeper extends CameraHolder to get more powerful controlling of camera.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface CameraHolderKeeper extends CameraHolder {

    /**
     * Releases the CameraHolder instance and disconnect the camera on the device
     */
    public void release();

}
