package com.jack.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceHolder;

import com.jack.Debug;
import com.jack.util.OnSnapshotListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by jacktseng on 2015/6/21.
 */
public class FCVCameraFactory {


    public static CameraHelper getInstance() {
        CameraHelper rtn = null;

        /**
         * because the camera class was be instead to camera2 on API 21 upper, so the CameraHelper
         * interface is needed to resolve the version of camera & camera2, developer do not to care
         * what the version must be used.
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //api 21

        }
        else {
            if (CameraHelperHolder.INSTANCE.getCamera() == null) {
                Camera camera = Camera.open();
                Camera.Parameters params = camera.getParameters();
                CameraHelperHolder.INSTANCE.setCamera(camera);
                CameraHelperHolder.INSTANCE.setCameraParameters(params);
            }
            rtn = CameraHelperHolder.INSTANCE;
        }

        return rtn;
    }

    private static class CameraHelperHolder {
        private static final String TAG = "FCVCameraHelper";

        private static final CameraHelper INSTANCE = new CameraHelper() {

            private boolean mIsDisplaying = false;

            private OnSnapshotListener mOnSnapshotListener;

            private Camera mCamera;

            private Camera.Parameters mParameters;

            private Bitmap mSnapshotBitmap;

            private Camera.ShutterCallback mShutterCallback;

            private Camera.PictureCallback mRawPictureCallback;

            private Camera.PictureCallback mPostViewPictureCallback;

            private Camera.PictureCallback mJPEGPictureCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    Debug.dumpLog(CameraHelperHolder.TAG, "take snapshot!");

                    /**
                     * goals to avoid OOM problem
                     */
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPurgeable = true;
                    options.inInputShareable = true;

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    Matrix mx = new Matrix();
                    mx.postRotate(90f);
                    Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mx, true);

                    mSnapshotBitmap = bmp2;

                    //release bmp
                    bmp.recycle();
                    System.gc();

                    if(mOnSnapshotListener != null)
                        mOnSnapshotListener.onSnapshot(CAMERA_SNAPSHOT_TYPE_JPEG, bmp2);
                }
            };

            @Override
            public boolean isDisplaying() {
                return mIsDisplaying;
            }

            @Override
            public boolean checkPreviewSize(int w, int h) {

                boolean isValid = false;
                if(mParameters != null) {
                    for(Camera.Size size : mParameters.getSupportedPreviewSizes()) {
                        if(size.width == w && size.height == h) {
                            isValid = true;
                            break;
                        }
                    }
                }

                return  isValid;
            }

            @Override
            public boolean checkSnapshotSize(int w, int h) {

                boolean isValid = false;
                if(mParameters != null) {
                    for(Camera.Size size : mParameters.getSupportedPictureSizes()) {
                        if(size.width == w && size.height == h) {
                            isValid = true;
                            break;
                        }
                    }
                }

                return  isValid;
            }

            @Override
            public Camera getCamera() {
                return mCamera;
            }

            @Override
            public void setCamera(Camera camera) {
                mCamera = camera;
            }

            @Override
            public List<Camera.Size> getSupportedPreviewSizes() {
                List<Camera.Size> rtn = null;
                if(mParameters != null)
                    rtn = mParameters.getSupportedPreviewSizes();
                return rtn;
            }

            @Override
            public List<Camera.Size> getSupportedPictureSizes() {
                List<Camera.Size> rtn = null;
                if(mParameters != null)
                    rtn = mParameters.getSupportedPictureSizes();
                return rtn;
            }

            @Override
            public Camera.Parameters getCameraParameters() {
                return mParameters;
            }

            @Override
            public void setCameraParameters(Camera.Parameters params) {
                mParameters = params;
            }

            @Override
            public void setSnapshotListener(OnSnapshotListener listener) {
                mOnSnapshotListener = listener;
            }

            @Override
            public void setOrientationPort() {
                if(mIsDisplaying) return;
                if(mCamera != null)
                    mCamera.setDisplayOrientation(CAMERA_ORIENTATION_PORT);
            }

            @Override
            public void setOrientationLand() {
                if(mIsDisplaying) return;
                if(mCamera != null)
                    mCamera.setDisplayOrientation(CAMERA_ORIENTATION_LAND);
            }

            @Override
            public void setPreviewSize(Camera.Size size) {
                if(mParameters != null)
                    mParameters.setPreviewSize(size.width, size.height);
            }

            @Override
            public void setSnapshotSize(Camera.Size size) {
                if(mParameters != null)
                    mParameters.setPictureSize(size.width, size.height);
            }

            @Override
            public void setDisplayHolder(SurfaceHolder holder) {
                if(mIsDisplaying) return;
                if(mCamera == null) return;

                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void setShutterCallback(Camera.ShutterCallback callback) {
                mShutterCallback = callback;
            }

            @Override
            public void setRawPictureCallback(Camera.PictureCallback callback) {
                    mRawPictureCallback = callback;
            }

            @Override
            public void setPostViewPictureCallback(Camera.PictureCallback callback) {
                mPostViewPictureCallback = callback;
            }

            @Override
            public void setJPEGPictureCallback(Camera.PictureCallback callback) {
                mJPEGPictureCallback = callback;
            }

            @Override
            public void updateCameraParameters() {
                if(mCamera == null) return;
                if(mParameters == null) return;

                if(mIsDisplaying) {
                    mCamera.stopPreview();
                    mCamera.setParameters(mParameters);
                    mCamera.startPreview();
                }
                else {
                    mCamera.setParameters(mParameters);
                }
            }

            @Override
            public void start() {
                if(mIsDisplaying) return;
                if(mCamera == null) return;
                if(mParameters == null) return;

                updateCameraParameters();
                mCamera.startPreview();
                mIsDisplaying = true;
            }

            @Override
            public void stop() {
                if(mCamera == null) return;

                mCamera.stopPreview();
                mIsDisplaying = false;
            }

            @Override
            public void release() {
                if(mIsDisplaying)
                    stop();

                if(mCamera != null) {
                    mCamera.release();
                }
                mCamera = null;
                mParameters = null;
                mIsDisplaying = false;
            }

            @Override
            public Bitmap snapshot() {
                if(!mIsDisplaying) return null;
                if(mCamera == null) return null;

                mCamera.takePicture(mShutterCallback, mRawPictureCallback, mPostViewPictureCallback, mJPEGPictureCallback);
                mCamera.startPreview();

                Bitmap rtn = mSnapshotBitmap;
                mSnapshotBitmap = null;

                return rtn;
            }
        };
    }


}
