package com.jack.library.camera.impl;

import android.os.Environment;
import android.util.Log;

import com.jack.library.Debug;
import com.jack.library.camera.CameraRecord;
import com.jack.library.camera.CameraStorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * This class is an instance of CameraStorage, and provides saving the raw image (byte array).
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
class CameraStorageImpl implements CameraStorage {

    public static final String TAG = CameraStorageImpl.class.getSimpleName();

    private static final String STORAGE_IMAGE_FORMAT = "jpg";

    private static final String STORAGE_ROOT_DEFAULT =
            Environment.getExternalStorageDirectory().getPath();

    private String mRoot = STORAGE_ROOT_DEFAULT;

    /**
     * Checks the path and making folders or creating file if they are not exist
     *
     * @param path
     * @return
     */
    private static boolean checkPath(String path) {
        File f = new File(path);

        if(!f.getParentFile().exists())
            f.getParentFile().mkdirs();

        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false; //return false
            }
        }

        return true;
    }

    CameraStorageImpl() {

    }

    @Override
    public void setRoot(String root) {
        mRoot = root;
    }

    @Override
    public String getRoot() {
        return mRoot;
    }

    @Override
    public boolean save(CameraRecord record) {

        boolean isError = true;
        do {
            if(record == null) break;
            if(!(record.getRecord() instanceof byte[])) break; //just accepts image of bytes

            if(mRoot == null) {
                mRoot = STORAGE_ROOT_DEFAULT;
                Log.e(TAG, "use default path to store cause by never giving root path");
            }
            String path = mRoot + "/" + record.getTime() + "." + STORAGE_IMAGE_FORMAT;
            byte[] image = (byte[]) record.getRecord();

            /**
             * Checks and making the folder or file if doesn't exist
             */
            checkPath(path);
            Debug.dumpLog(TAG, "saving a record with path " + path);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
                bos.write(image);
                bos.flush();
                bos.close();
                Debug.dumpLog(TAG, "saved a record success");
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            record.setResourcePath(path); //updates resource path

            isError = false;
        } while(false);

        return !isError;
    }

    //not implemented
    @Deprecated
    @Override
    public List<CameraRecord> loadThumbnail() {
        return null;
    }

    //not implemented
    @Deprecated
    @Override
    public Object loadResource(CameraRecord record) {
        return null;
    }
}
