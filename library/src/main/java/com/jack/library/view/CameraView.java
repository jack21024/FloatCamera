package com.jack.library.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jack.library.Debug;
import com.jack.library.camera.CameraHolder;
import com.jack.library.camera.CameraManager;
import com.jack.library.camera.impl.CameraManagerFactory;

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

    private boolean mIsSurfacePrepared = false;
    private boolean mIsDisplaying = false;

    private SurfaceHolder mSurfaceHolder;

    private CameraManager mCameraManager;

    private CameraHolder mCameraHolder;

    private Thread mStartPreviewThread;

    private OnViewChangeListener mOnViewChangeListener;

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
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCameraManager = CameraManagerFactory.getInstance();
        mCameraHolder = mCameraManager.openCamera();

        if(mCameraHolder != null) {
            mCameraHolder.setPictureFormat(ImageFormat.JPEG);

            if(Debug.IS_DEBUG) {
                Log.d(TAG, "support preview size:");
                for (int[] size : mCameraHolder.getSupportedPreviewSizes()) {
                    Log.i(TAG, "w=" + size[0] + " h=" + size[1]);
                }
                Log.d(TAG, "support picture size:");
                for (int[] size : mCameraHolder.getSupportedPictureSizes()) {
                    Log.i(TAG, "w=" + size[0] + " h=" + size[1]);
                }
            }

            /**
             * set default size of camera preview and snapshot
             */
            mCameraHolder.setPreviewSize(CAMERA_PREVIEW_WIDTH_DEFAULT, CAMERA_PREVIEW_HEIGHT_DEFAULT);
            mCameraHolder.setSnapshotSize(CAMERA_SNAPSHOT_WIDTH_DEFAULT, CAMERA_SNAPSHOT_HEIGHT_DEFAULT);

            mCameraHolder.setOrientationPort();
            mCameraHolder.updateCameraParameters();

        }

    }

    public void setOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

    public void start() {
        if(mCameraHolder == null) return;
        if(mSurfaceHolder == null) return;
        if(mIsDisplaying) return;
        if(mStartPreviewThread != null) return;

        /**
         * This thread goals to launcher camera to preview after surface is prepared.
         * Because main thread is using to create a view of Surface, so we need to
         * create another thread to do that.
         */
        mStartPreviewThread = new Thread() {
            @Override
            public void run() {
                final long timeout = 5000;
                final long startTime = System.currentTimeMillis();

                //wait for surface to prepare
                while(!mIsSurfacePrepared) {
                    try {
                        Log.e(TAG, "wait for surface ready!");
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //check timeout
                    long time = System.currentTimeMillis();
                    if(time - startTime > timeout)
                        break;
                }

                mCameraHolder.setPreviewHolder(mSurfaceHolder);
                try {
                    mCameraHolder.start();
                    mIsDisplaying = true;
                } catch(Exception e) {
                    e.printStackTrace();
                }

                mStartPreviewThread = null;
            }
        };
        mStartPreviewThread.start();
    }

    public void stop() {
        if(mCameraHolder == null) return;
        if(!mIsDisplaying) return;

        try {
            mCameraHolder.stop();
            mIsDisplaying = false;
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void release() {
        if(mCameraHolder != null)
            stop();
        if(mCameraManager != null)
            mCameraManager.closeCamera();

        mSurfaceHolder = null;
        mCameraHolder = null;
        mCameraManager = null;
        mIsDisplaying = false;
        mIsSurfacePrepared = false;
    }

    public CameraHolder getCameraController() {
        return mCameraHolder;
    }

    public void setCameraOrientationPort() {
        if(mCameraHolder == null) return;
        try {
            mCameraHolder.stop();
            mCameraHolder.setOrientationPort();
            mCameraHolder.start();
        } catch(Exception e) {

        }
    }

    public void setCameraOrientationLand() {
        if(mCameraHolder == null) return;
        try {
            mCameraHolder.stop();
            mCameraHolder.setOrientationLand();
            mCameraHolder.start();
        } catch(Exception e) {

        }
    }

    public void setCameraPreviewSize(int w, int h) {
        if(mCameraHolder == null) return;

        mCameraHolder.setPreviewSize(w, h);
        mCameraHolder.updateCameraParameters();
    }

    public void setCameraSnapshotSize(int w, int h) {
        if(mCameraHolder == null) return;

        mCameraHolder.setSnapshotSize(w, h);
        mCameraHolder.updateCameraParameters();
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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int pixel_format, int w, int h) {
        Debug.dumpLog(TAG, "surfaceChanged()");
        mIsSurfacePrepared = true;

        boolean _isIsDisplay = mIsDisplaying; //cache status
        if(_isIsDisplay)
            stop();
        if(mOnViewChangeListener != null)
            mOnViewChangeListener.onViewChanged(CameraView.this, pixel_format, w, h);
        if(_isIsDisplay)
            start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Debug.dumpLog(TAG, "surfaceDestroyed()");
        release();
    }

    public interface OnViewChangeListener {
        public void onViewChanged(CameraView view, int pixelFormat, int w, int h);
    }
}
