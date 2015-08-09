package com.jack.library.camera.impl;

import android.graphics.Bitmap;
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
 * Created by jacktseng on 2015/8/9.
 */
class CameraStorageImpl implements CameraStorage {

    public static final String TAG = CameraStorageImpl.class.getSimpleName();

    private static final String STORAGE_IMAGE_FORMAT = "jpg";

    private static final String STORAGE_ROOT_DEFAULT =
            Environment.getExternalStorageDirectory().getPath();

    private String mRoot = STORAGE_ROOT_DEFAULT;

    private static String getRootStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

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

//    @Override
//    public void save(CameraRecord record) {
//        if(record == null) return;
//
//        String path = record.getResourcePath();
//        long time = record.getTime();
//        Bitmap bmp = record.getImage();
//
//        if(path == null || path.isEmpty()) {
//            path = getRootStoragePath() + "/" + mRootFolder + "/" + time
//                    + "." + STORAGE_IMAGE_FORMAT;
//            //record updates the resource path
//            record.setResourcePath(path);
//        }
//
//        /**
//         * checks and making the folder or file if doesn't exist
//         */
//        checkPath(path);
//
//        boolean bIsError = true;
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//
//            bIsError = false;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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
            if(!(record.getRecord() instanceof Bitmap)) break; //just accepts image

            if(mRoot == null) {
                mRoot = STORAGE_ROOT_DEFAULT;
                Log.e(TAG, "use default path to store cause by never giving root path");
            }
            String path = mRoot + "/" + record.getTime() + "." + STORAGE_IMAGE_FORMAT;
            Bitmap bmp = (Bitmap) record.getRecord();
            /**
             * checks and making the folder or file if doesn't exist
             */
            checkPath(path);
            Debug.dumpLog(TAG, "saving a record with path " + path);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                Debug.dumpLog(TAG, "saved a record success");
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            record.setResourcePath(path); //update resource path

            isError = false;
        } while(false);

        return !isError;
    }

    @Deprecated
    @Override
    public List<CameraRecord> loadThumbnail() {
        return null;
    }

    @Deprecated
    @Override
    public Object loadResource(CameraRecord record) {
        return null;
    }
}
