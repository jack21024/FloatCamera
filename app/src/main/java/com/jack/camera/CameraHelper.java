package com.jack.camera;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.jack.util.OnSnapshotListener;

import java.util.List;

/**
 * Created by jacktseng on 2015/6/20.
 */
public interface CameraHelper {

    public static final int CAMERA_ORIENTATION_PORT = 90;
    public static final int CAMERA_ORIENTATION_LAND = 0;
    public static final int CAMERA_SNAPSHOT_TYPE_RAW    = 0x01;
    public static final int CAMERA_SNAPSHOT_TYPE_POST   = 0x11;
    public static final int CAMERA_SNAPSHOT_TYPE_JPEG   = 0x21;


    public boolean isDisplaying();

    public boolean checkPreviewSize(int w, int h);

    public boolean checkSnapshotSize(int w, int h);

    public Camera getCamera();

    public void setCamera(Camera camera);

    public List<Camera.Size> getSupportedPreviewSizes();

    public List<Camera.Size> getSupportedPictureSizes();

    public Camera.Parameters getCameraParameters();

    public void setCameraParameters(Camera.Parameters params);

    public void setSnapshotListener(OnSnapshotListener listener);

    public void setOrientationPort();

    public void setOrientationLand();

    public void setPreviewSize(Camera.Size size);

    public void setSnapshotSize(Camera.Size size);

    public void setDisplayHolder(SurfaceHolder holder);

    public void setShutterCallback(Camera.ShutterCallback callback);

    public void setRawPictureCallback(Camera.PictureCallback callback);

    public void setPostViewPictureCallback(Camera.PictureCallback callback);

    public void setJPEGPictureCallback(Camera.PictureCallback callback);

    public void updateCameraParameters();

    public void start() throws Exception;

    public void stop() throws Exception;

    public void release();

    public Bitmap snapshot();

}
