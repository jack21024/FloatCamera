package com.jack.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by jacktseng on 2015/6/19.
 */
public abstract class Storage {

    public static String getRootStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public void checkPath(String path) {
        File f = new File(path);

        if(!f.getParentFile().exists())
            f.getParentFile().mkdirs();

        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract boolean saveBytes(String path, byte[] bytes);

    public abstract boolean saveText(String path, String text);

    public abstract boolean saveBitmap(String path, Bitmap.CompressFormat format, Bitmap bmp);


}
