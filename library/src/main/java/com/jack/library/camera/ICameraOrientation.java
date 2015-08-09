package com.jack.library.camera;

/**
 * Created by jacktseng on 2015/8/8.
 */
public interface ICameraOrientation {

    public static final int CAMERA_ORIENTATION_PORT = 90;
    public static final int CAMERA_ORIENTATION_LAND = 0;

    public void setOrientationPort();

    public void setOrientationLand();

}
