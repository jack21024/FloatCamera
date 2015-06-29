package com.jack.util;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jacktseng on 2015/6/19.
 */
public class StorageImpl extends Storage {
    public static final String TAG = StorageImpl.class.getSimpleName();

    @Override
    public boolean saveBytes(String path, byte[] bytes) {
        return false;
    }

    @Override
    public boolean saveText(String path, String text) {
        return false;
    }

    @Override
    public boolean saveBitmap(String path, Bitmap.CompressFormat format, Bitmap bmp) {
        checkPath(path);

        boolean bIsError = true;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bmp.compress(format, 100, bos);
            bos.flush();
            bos.close();

            bIsError = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return !bIsError;
    }

}
