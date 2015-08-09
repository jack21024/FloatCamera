package com.jack.library.camera.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.jack.library.Debug;
import com.jack.library.camera.CameraHolderManager;
import com.jack.library.camera.CameraRecord;
import com.jack.library.camera.Cameras;
import com.jack.library.camera.OnSnapshotListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class supports a Camera of Camera1 which under api 21
 *
 * Created by jacktseng on 2015/8/8.
 */
class CameraHolderImpl implements CameraHolderManager {

    public static final String TAG = CameraHolderImpl.class.getSimpleName();

    private Camera1 mCamera;

    private ArrayList<OnSnapshotListener> mOnSnapshotListenerList = new ArrayList<>();


    private Camera.PictureCallback mJPEGPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Debug.dumpLog(TAG, "take snapshot!");

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

            //release bmp
            bmp.recycle();
            System.gc();
            /**
             * puts snapshot into CameraRecord object for encapsulation
             */
            CameraRecord<Bitmap> record = new CameraRecordImpl(bmp2, null);

            if(mOnSnapshotListenerList.size() > 0)
                for(OnSnapshotListener listener : mOnSnapshotListenerList) {
                    listener.onSnapshot(CAMERA_SNAPSHOT_TYPE_JPEG, record);
                }

        }
    };


        CameraHolderImpl(Camera1 camera) {
        mCamera = camera;
    }

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
        camera.startPreview();
    }

    @Deprecated
    @Override
    public void startRecording() {

    }

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
//        Camera camera = mCamera.getCamera();
//        if(camera != null) {
//            camera.stopPreview();
//            camera.setParameters(camera.getParameters());
////            camera.startPreview();
//        }
    }

    @Override
    public void start() throws Exception {
        Camera camera = mCamera.getCamera();
        if(camera != null) {
//            camera.setParameters(camera.getParameters());
            camera.startPreview();
        }
    }

    @Override
    public void stop() throws Exception {
        Camera camera = mCamera.getCamera();
        if(camera != null)
            camera.stopPreview();
    }

    /**
     * An instance of CameraRecord for CameraHolderImpl of Camera1
     */
    private class CameraRecordImpl implements CameraRecord<Bitmap> {
        private Bitmap image;
        private long time;
        private String path;

        private CameraRecordImpl(Bitmap image, String path) {
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
        public void setRecord(Bitmap record) {

        }

        @Override
        public Bitmap getRecord() {
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
