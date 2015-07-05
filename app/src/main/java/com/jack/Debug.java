package com.jack;

import android.util.Log;

/**
 * Created by jacktseng on 2015/6/19.
 */
public class Debug {

    public static boolean IS_DEBUG = true;

    public static void dumpLog(String tag, String msg) {
        if(IS_DEBUG)
            Log.d(tag, msg);
  }


}
