package com.jack.library.camera.impl;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.jack.library.Debug;
import com.jack.library.camera.CameraHolderKeeper;
import com.jack.library.camera.CameraRecord;
import com.jack.library.camera.Cameras;
import com.jack.library.camera.listener.OnSnapshotListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An instance of CameraHolderKeeper which supporting the camera object is a Camera1 instance for
 * android api level 21 before.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
class CameraHolderImpl implements CameraHolderKeeper {

    public static final String TAG = CameraHolderImpl.class.getSimpleName();

    private static final int CAMERA_ORIENTATION_PORT = 90;
    private static final int CAMERA_ORIENTATION_LAND = 0;

    /**
     * The preview status which is used to camera parameters updating
     */
    private boolean mIsCameraStated = false;

    private Camera1 mCamera;

    private ArrayList<OnSnapshotListener> mOnSnapshotListenerList = new ArrayList<>();


    private Camera.PictureCallback mJPEGPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Debug.dumpLog(TAG, "take snapshot!");

            /**
             * Starts camera to preview again cause preview was stopped by taking snapshot
             */
            camera.startPreview();

            /**
             * Puts snapshot into CameraRecord object for encapsulation
             */
            CameraRecord record = new CameraRecordImpl(bytes, null);

            //Fires the OnSnapshot event to all listeners
            if(mOnSnapshotListenerList.size() > 0)
                for(OnSnapshotListener listener : mOnSnapshotListenerList) {
                    listener.onSnapshot(CAMERA_SNAPSHOT_TYPE_JPEG, record);
                }
        }
    };


    CameraHolderImpl(Camera1 camera) {
        mCamera = camera;
    }

    /**
     * Checks the size of preview is valid or not
     *
     * @param w
     * @param h
     * @return
     */
    private boolean checkPreviewSize(int w, int h) {
        boolean isError = true;

        out:
        do {
            if(w <= 0 || h <= 0) break;

            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            Camera.Parameters params = camera.getParameters();
            if(params == null) break;
            if(params.getSupportedPreviewSizes().size() <= 0) break;

            for(Camera.Size size : params.getSupportedPreviewSizes()) {
                if(size.width == w && size.height == h) {
                    break out;
                }
            }

            isError = false;
        } while(false);

        return  !isError;
    }

    /**
     * Checks the size of snapshot is valid or not
     *
     * @param w
     * @param h
     * @return
     */
    private boolean checkSnapshotSize(int w, int h) {
        boolean isError = true;

        out:
        do {
            if(w <= 0 || h <= 0) break;

            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            Camera.Parameters params = camera.getParameters();
            if(params == null) break;
            if(params.getSupportedPictureSizes().size() <= 0) break;

            for(Camera.Size size : params.getSupportedPictureSizes()) {
                if(size.width == w && size.height == h) {
                    break out;
                }
            }

            isError = false;
        } while(false);

        return  !isError;
    }


    @Override
    public Cameras getCamera() {
        return mCamera;
    }

    @Override
    public void setPreviewHolder(SurfaceHolder holder) {
        if(holder == null) return;

        Camera camera = mCamera.getCamera();
        if(camera != null)
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void release() {
        if(mCamera != null)
            mCamera.release();
        if(mOnSnapshotListenerList != null)
            mOnSnapshotListenerList.clear();

        mIsCameraStated = false;
        mCamera = null;
        mOnSnapshotListenerList = null;
        mJPEGPictureCallback = null;
    }

    @Override
    public void addSnapshotListener(OnSnapshotListener listener) {
        if(mOnSnapshotListenerList != null)
            mOnSnapshotListenerList.add(listener);
    }

    @Override
    public void removeSnapshotListener(OnSnapshotListener listener) {
        if(mOnSnapshotListenerList != null)
            mOnSnapshotListenerList.remove(listener);
    }

    @Override
    public void snapshot() {
        Camera camera = mCamera.getCamera();
        camera.takePicture(null, null, null, mJPEGPictureCallback);
        /**
         * This code was moved to the OnSnapshotListener instance to make sure camera starting
         * preview correctly
         */
//        camera.startPreview();
    }

    /**
     * Not implemented
     *
     * @param path
     */
    @Deprecated
    @Override
    public void startRecording(String path) {

    }

    /**
     * Not implemented
     */
    @Deprecated
    @Override
    public void stopRecording() {

    }

    @Override
    public List<int[]> getSupportedPreviewSizes() {
        List<int[]> rtn = new ArrayList<>();

        do {
            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            if(sizes.size() <= 0) break;

            for(Camera.Size size : sizes)
                rtn.add(new int[]{size.width, size.height});
        } while(false);

        return rtn;
    }

    @Override
    public List<int[]> getSupportedPictureSizes() {
        List<int[]> rtn = new ArrayList<>();

        do {
            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            if(sizes.size() <= 0) break;

            for(Camera.Size size : sizes)
                rtn.add(new int[]{size.width, size.height});
        } while(false);


        return rtn;
    }

    @Override
    public void setPreviewSize(int w, int h) {
        do {
            if(checkPreviewSize(w, h)) break;

            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            Camera.Parameters params = camera.getParameters();
            if(params == null) break;

            params.setPreviewSize(w, h);
        } while(false);
    }

    @Override
    public void setSnapshotSize(int w, int h) {
        do {
            if(checkSnapshotSize(w, h)) break;

            Camera camera = mCamera.getCamera();
            if(camera == null) break;

            Camera.Parameters params = camera.getParameters();
            if(params == null) break;

            params.setPictureSize(w, h);
        } while(false);
    }

    @Override
    public void setPreviewFormat(int pixel_format) {
        if((pixel_format | ImageFormat.NV21 | ImageFormat.YV12) == 0) return;

        Camera camera = mCamera.getCamera();
        if(camera != null)
            camera.getParameters().setPreviewFormat(pixel_format);
    }

    @Override
    public void setPictureFormat(int pixel_format) {
        if((pixel_format | ImageFormat.JPEG |
                ImageFormat.RGB_565 | ImageFormat.NV21) == 0) return;

        Camera camera = mCamera.getCamera();
        if(camera != null)
            camera.getParameters().setPictureFormat(pixel_format);
    }

    @Override
    public void setOrientationPort() {
        Camera camera = mCamera.getCamera();
        if(camera != null)
            camera.setDisplayOrientation(CAMERA_ORIENTATION_PORT);
    }

    @Override
    public void setOrientationLand() {
        Camera camera = mCamera.getCamera();
        if(camera != null)
            camera.setDisplayOrientation(CAMERA_ORIENTATION_LAND);
    }

    @Override
    public void updateCameraParameters() {
        Camera camera = mCamera.getCamera();
        if(camera != null) {
            if(mIsCameraStated)
                camera.stopPreview();
            camera.setParameters(camera.getParameters());
            if(mIsCameraStated)
                camera.startPreview();
        }
    }

    @Override
    public void start() throws Exception {
        Camera camera = mCamera.getCamera();
        if(camera != null) {
            camera.startPreview();
            mIsCameraStated = true;
        }
    }

    @Override
    public void stop() throws Exception {
        Camera camera = mCamera.getCamera();
        if(camera != null) {
            camera.stopPreview();
            mIsCameraStated = false;
        }
    }

    /**
     * An instance of CameraRecord for CameraHolderImpl of Camera1
     */
    private class CameraRecordImpl implements CameraRecord<byte[]> {
        private byte[] image;
        private long time;
        private String path;

        private CameraRecordImpl(byte[] image, String path) {
            this.image = image;
            this.path = path;
            this.time = System.currentTimeMillis();
        }

        @Override
        public void setTime(long t) {
            this.time = t;
        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public void setRecord(byte[] record) {

        }

        @Override
        public byte[] getRecord() {
            return image;
        }

        @Override
        public String getResourcePath() {
            return path;
        }

        @Override
        public void setResourcePath(String path) {
            this.path = path;
        }
    }
}
