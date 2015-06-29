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

    /**
     * jack@150626
     * 1. 修改preview size不能設定大於等於螢幕尺寸的size
     *
     */

    /**
     * jack@150625
     * 1. 支援偽裝遮罩
     * 2. 支援網頁瀏覽
     */

    /**
     * jack@150624
     * 1. 加入照相機尺寸設定(preview & picture)
     * 2. 修正snapshot後圖片沒有釋放的問題
     * 3. 修正儲存圖片時沒有檢查參數
     * 4. 加入儲存圖片時的format預設(StorageImpl.save())
     * 5. 修正snapshot的尺寸過大時會oom的問題 (bitmap options & open largeHeap)
     */

    /**
     * jack@150622
     * 1. 修正視窗實際大小為全螢幕之問題, 使用measure測量視窗內容之實際大小並更新size
     *
     */

}
