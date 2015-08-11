package com.jack.library.camera;

/**
 * CameraManager handles functions regarding the camera of device. <br/>
 * Gets the CameraHolder to operate camera doing preview, snapshot, recording etc, and the
 * CameraStorage can help to read or write the record of camera accessing from storage.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface CameraManager {

    /**
     * Gets an instance of CameraHolder for camera operating
     *
     * @return
     */
    public CameraHolder getCameraHolder();

    /**
     * Returns a CameraStorage object which creating with given type of Storage
     *
     * @param type
     * @return
     */
    public CameraStorage getCameraStorage(CameraStorage.STORAGE_TYPE type);

    /**
     * Creates a new CameraHolder object to access the camera on the device
     *
     * @return
     */
    public CameraHolder openCamera();

    /**
     * Releases the camera object resource and it's holder
     */
    public void closeCamera();
}
