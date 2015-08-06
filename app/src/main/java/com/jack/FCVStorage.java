package com.jack;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.jack.camera.CameraHelper;
import com.jack.util.OnSnapshotListener;
import com.jack.util.StorageImpl;
import com.jack.util.Storage;

/**
 * Created by jacktseng on 2015/6/19.
 */
public class FCVStorage extends StorageImpl implements OnSnapshotListener {
    public static final String TAG = FCVStorage.class.getSimpleName();

    public static final String STORAGE_APP_FOLDER = "FCV";
    public static final String STORAGE_PICTURE_FORMAT_JPEG  = "jpg";
    public static final String STORAGE_PICTURE_FORMAT_PNG   = "png";

    private Context mContext;

//    private Storage mStorage;

//    public FCVStorage() {
//        mStorage = new StorageImpl();
//    }

    public FCVStorage(Context ctx) {
        mContext = ctx;
    }

    private String getFileNameByTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String save(Bitmap.CompressFormat format, Bitmap bmp) {
        if(bmp == null || bmp.isRecycled()) return null;
        if(format == null)
            format = Bitmap.CompressFormat.JPEG;

        String path = Storage.getRootStoragePath() + "/" + STORAGE_APP_FOLDER + "/" + getFileNameByTime() + ".";

        if(format == Bitmap.CompressFormat.JPEG)
            path += STORAGE_PICTURE_FORMAT_JPEG;
        else if(format == Bitmap.CompressFormat.PNG)
            path += STORAGE_PICTURE_FORMAT_PNG;

//        if(!mStorage.saveBitmap(path, Bitmap.CompressFormat.JPEG, bmp))
        if(!saveBitmap(path, Bitmap.CompressFormat.JPEG, bmp)) {
            Log.e(TAG, "save picture failed(jpeg)");
            return null;
        }
        else {
            Debug.dumpLog(TAG, "save picture success.");
            return path;
        }
    }

    @Override
    public void onSnapshot(int rawType, Bitmap bmp) {
        if(rawType == CameraHelper.CAMERA_SNAPSHOT_TYPE_JPEG) {
            String path = save(Bitmap.CompressFormat.JPEG, bmp);
            if(path != null && !path.isEmpty() && mContext != null)
                Toast.makeText(mContext, "save at \"" + path + "\" ", Toast.LENGTH_SHORT).show();
        }
        if(bmp != null) {
            bmp.recycle();
            System.gc();
        }
    }

//    @Override
//    public void saveJPEG(Bitmap bmp) {
//        save(Bitmap.CompressFormat.JPEG, bmp);
//    }
//
//    @Override
//    public void savePNG(Bitmap bmp) {
//        save(Bitmap.CompressFormat.PNG, bmp);
//    }

}
