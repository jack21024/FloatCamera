package com.jack;

import android.util.Log;

/**
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class Debug {

    public static boolean IS_DEBUG = true;

    public static void dumpLog(String tag, String msg) {
        if(IS_DEBUG)
            Log.d(tag, msg);
    }
}
