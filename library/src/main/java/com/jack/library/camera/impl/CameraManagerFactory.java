package com.jack.library.camera.impl;

import com.jack.library.camera.CameraHolder;
import com.jack.library.camera.CameraHolderManager;
import com.jack.library.camera.CameraManager;
import com.jack.library.camera.CameraStorage;

/**
 * Created by jacktseng on 2015/8/7.
 */
public class CameraManagerFactory {

    public static final String TAG = CameraManagerFactory.class.getSimpleName();

    private static CameraHolderManager CAMERA_HOLDER;

    public static CameraManager getInstance() {

        return CameraManagerHolder.INSTANCE;
    }

    private static class CameraManagerHolder {

        private static final CameraManager INSTANCE = new CameraManager() {

            private CameraStorage mCameraStorage;

            @Override
            public CameraHolder getCameraHolder() {
                return CAMERA_HOLDER;
            }

//            @Override
//            public void setCameraStorage(CameraStorage storage) {
//                mCameraStorage = storage;
//            }

//            @Override
//            public CameraStorage getCameraStorage() {
//                return mCameraStorage;
//            }


            @Override
            public CameraStorage getCameraStorage(CameraStorage.STORAGE_TYPE type) {
                CameraStorage rtn = null;
                switch (type) {
                    case LOCAL_DEFAULT:
                        rtn = new CameraStorageImpl();
                        break;
                    case REMOTE_DEFAULT: //not implements
                        break;
                }

                return rtn;
            }

            @Override
            public CameraHolder openCamera() {
                if(CAMERA_HOLDER == null) {
                    CAMERA_HOLDER = CameraHolderFactory.getInstance();
                }

                return CAMERA_HOLDER;
            }

            @Override
            public void closeCamera() {
                if(CAMERA_HOLDER != null) {
                    CAMERA_HOLDER.release();
                    CAMERA_HOLDER = null;
                }
            }

//            @Override
//            public void onSnapshot(int rawType, CameraRecord record) {
//                if(rawType == ICameraCapture.CAMERA_SNAPSHOT_TYPE_JPEG) {
//                    if(mCameraStorage != null) {
//                        mCameraStorage.save(record);
//
//                    }
//
//                    String path = record.getResourcePath();
//                    Debug.dumpLog(TAG, "save snapshot on " + path);
//                }
//
//                /**
//                 * release bitmap for clean memory
//                 */
//                Bitmap bmp = record.getImage();
//                if(bmp != null) {
//                    bmp.recycle();
//                    System.gc();
//                }
//            }
        };

    }

}
