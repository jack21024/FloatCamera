package com.jack.library.camera;

/**
 * Created by jacktseng on 2015/8/6.
 */
public interface CameraManager {

    public CameraHolder getCameraHolder();

//    public void setCameraStorage(CameraStorage storage);

    public CameraStorage getCameraStorage(CameraStorage.STORAGE_TYPE type);

    public CameraHolder openCamera();

    public void closeCamera();
}
