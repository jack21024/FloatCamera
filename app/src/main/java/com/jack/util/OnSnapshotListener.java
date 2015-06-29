package com.jack.util;

import android.graphics.Bitmap;

/**
 * Created by jacktseng on 2015/6/21.
 */
public interface OnSnapshotListener {
    public void onSnapshot(int rawType, Bitmap bmp);
}
