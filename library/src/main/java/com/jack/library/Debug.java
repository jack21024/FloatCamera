package com.jack.library;

import android.util.Log;

/**
 * Created by jacktseng on 2015/8/7.
 */
public class Debug {
    public static boolean IS_DEBUG = true;

    public static void dumpLog(String tag, String msg) {
        if(IS_DEBUG)
            Log.d(tag, msg);
    }


}
