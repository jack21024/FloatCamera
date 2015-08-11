package com.jack.library.camera.impl;

import com.jack.library.camera.CameraHolder;
import com.jack.library.camera.CameraHolderKeeper;
import com.jack.library.camera.CameraManager;
import com.jack.library.camera.CameraStorage;

/**
 * CameraManagerFactory is in charge of giving a CameraManager instance for camera support.
 * <p/>
 *  Author: Jack Tseng (jack21024@gmail.com)
 */
public class CameraManagerFactory {

    public static final String TAG = CameraManagerFactory.class.getSimpleName();

    /**
     * A singleton instance of CameraHolderKeeper
     */
    private static CameraHolderKeeper CAMERA_HOLDER;

    public static CameraManager getInstance() {

        return CameraManagerHolder.INSTANCE;
    }

    /**
     * This holder is used to create a CameraManager instance and keep it all the time
     */
    private static class CameraManagerHolder {

        private static final CameraManager INSTANCE = new CameraManager() {

            @Override
            public CameraHolder getCameraHolder() {
                return CAMERA_HOLDER;
            }

            @Override
            public CameraStorage getCameraStorage(CameraStorage.STORAGE_TYPE type) {
                CameraStorage rtn = null;
                switch (type) {
                    case LOCAL_DEFAULT:
                        rtn = new CameraStorageImpl();
                        break;
                    case REMOTE_DEFAULT: //not implemented
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
        };

    }

}
