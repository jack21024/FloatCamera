package com.jack.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jack.Debug;
import com.jack.camera.CameraHelper;
import com.jack.camera.FCVCameraFactory;

import java.util.List;

/**
 * Created by jacktseng on 2015/6/19.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = CameraView.class.getSimpleName();

    public static final int CAMERA_ORIENTATION_PORT = 0;
    public static final int CAMERA_ORIENTATION_LAND = 90;
    public static final int CAMERA_PREVIEW_WIDTH_DEFAULT    = 320;
    public static final int CAMERA_PREVIEW_HEIGHT_DEFAULT   = 240;
    public static final int CAMERA_SNAPSHOT_WIDTH_DEFAULT   = 1920;
    public static final int CAMERA_SNAPSHOT_HEIGHT_DEFAULT  = 1080;

    private boolean mIsDisplaying = false;

    private SurfaceHolder mHolder;

    private CameraHelper mCameraHelper;

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraHelper = FCVCameraFactory.getInstance();

        if(mCameraHelper != null) {
            Camera.Parameters cameraParams = mCameraHelper.getCameraParameters();
            cameraParams.setPictureFormat(PixelFormat.JPEG);

            if(Debug.IS_DEBUG) {
                Log.d(TAG, "support preview size:");
                for (Camera.Size cs : cameraParams.getSupportedPreviewSizes()) {
                    Log.i(TAG, "w=" + cs.width + " h=" + cs.height);
                }
                Log.d(TAG, "support picture size:");
                for (Camera.Size cs : cameraParams.getSupportedPictureSizes()) {
                    Log.i(TAG, "w=" + cs.width + " h=" + cs.height);
                }
            }

            /**
             * set default size for camera preview and snapshot
             */
            cameraParams.setPreviewSize(CAMERA_PREVIEW_WIDTH_DEFAULT, CAMERA_PREVIEW_HEIGHT_DEFAULT);
            cameraParams.setPictureSize(CAMERA_SNAPSHOT_WIDTH_DEFAULT, CAMERA_SNAPSHOT_HEIGHT_DEFAULT);

            mCameraHelper.setOrientationPort();
            mCameraHelper.setCameraParameters(cameraParams);
        }

    }

    public void start() {
        if(mCameraHelper == null) return;
        if(mHolder == null) return;
        if(mIsDisplaying) return;

        mCameraHelper.setDisplayHolder(mHolder);

        try {
            mCameraHelper.start();
            mIsDisplaying = true;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if(mCameraHelper == null) return;
        if(!mIsDisplaying) return;

        try {
            mCameraHelper.stop();
            mIsDisplaying = false;
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void release() {
        if(mCameraHelper != null) {
//            try {
//                if(mIsDisplaying)
//                    mCameraHelper.stop();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            stop();
            mCameraHelper.release();
        }
        mHolder = null;
    }

    public CameraHelper getCameraController() {
        return mCameraHelper;
    }

    public void setCameraOrientationPort() {
        if(mCameraHelper == null) return;
        try {
            mCameraHelper.stop();
            mCameraHelper.setOrientationPort();
            mCameraHelper.start();
        } catch(Exception e) {

        }
    }

    public void setCameraOrientationLand() {
        if(mCameraHelper == null) return;
        try {
            mCameraHelper.stop();
            mCameraHelper.setOrientationLand();
            mCameraHelper.start();
        } catch(Exception e) {

        }
    }

    public void setCameraPreviewSize(int w, int h) {
        if(mCameraHelper == null) return;
        if(!mCameraHelper.checkPreviewSize(w, h)) return;
        if(mCameraHelper.getCameraParameters() == null) return;

        mCameraHelper.getCameraParameters().setPreviewSize(w, h);
        mCameraHelper.updateCameraParameters();
//        mCameraHelper.setPreviewSize(size);
    }

    public void setCameraSnapshotSize(int w, int h) {
        if(mCameraHelper == null) return;
        if(!mCameraHelper.checkSnapshotSize(w, h)) return;
        if(mCameraHelper.getCameraParameters() == null) return;

        mCameraHelper.getCameraParameters().setPictureSize(w, h);
        mCameraHelper.updateCameraParameters();
//        mCameraHelper.setSnapshotSize(size);
    }

    public List<Camera.Size> getCameraSupportedPreviewSizes() {
        List<Camera.Size> rtn = null;
        if(mCameraHelper != null)
            rtn = mCameraHelper.getSupportedPreviewSizes();
        return rtn;
    }

    public List<Camera.Size> getCameraSupportedSnapshotSizes() {
        List<Camera.Size> rtn = null;
        if(mCameraHelper != null)
            rtn = mCameraHelper.getSupportedPictureSizes();
        return rtn;
    }

    public void snapshot() {
        mCameraHelper.snapshot();
    }


    /**
     *
     * implements for SurfaceHolder.Callback
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Debug.dumpLog(TAG, "surfaceCreated()");
    }

    @Override
    public void surfaceChanged(final SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Debug.dumpLog(TAG, "surfaceChanged()");

        if(mCameraHelper == null) return;

        /**
         * lets camera starting lately to avoid unexpected crashing (maybe this crash is
         * creating WebViewer and starting camera at the same time)
         */
//        new Thread(new Runnable() {
//
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//
//                    if (mCameraHelper.isDisplaying())
//                        mCameraHelper.stop();
//                    mCameraHelper.setDisplayHolder(surfaceHolder);
//                    mCameraHelper.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }).start();

        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(1000);

                    if (mCameraHelper.isDisplaying())
                        stop();

                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Debug.dumpLog(TAG, "surfaceDestroyed()");
        release();
    }
}
