package com.jack.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jack.Debug;
import com.jack.view.CameraView;

/**
 * Created by jacktseng on 2015/6/24.
 */
public class SettingManager {

    private static Setting INSTANCE;

    public static Setting getInstance(Context ctx) {
        if(INSTANCE == null)
            INSTANCE = SPSettingBuilder.create(ctx);

        return INSTANCE;
    }

    private static class SPSettingBuilder {

        public static final String TAG = "SPSetting";

        private static String SETTING_SHARED_NAME = "SETTING";
        private static String SETTING_FIELD_PREVIEW_WIDTH   = "PREVIEW_WIDTH";
        private static String SETTING_FIELD_PREVIEW_HEIGHT  = "PREVIEW_HEIGHT";
        private static String SETTING_FIELD_SNAPSHOT_WIDTH  = "SNAPSHOT_WIDTH";
        private static String SETTING_FIELD_SNAPSHOT_HEIGHT = "SNAPSHOT_HEIGHT";


        public static Setting create(final Context ctx) {

            return new Setting() {

                private SharedPreferences mShared;

                private SharedPreferences.Editor mEdit;

                {
                    mShared = ctx.getSharedPreferences(SETTING_SHARED_NAME, Context.MODE_PRIVATE);
                    if(mShared != null)
                        mEdit = mShared.edit();

                    Debug.dumpLog(TAG, "created instance of SPSetting");
                }

                @Override
                public void setPreviewWidth(int w) {
                    if(mEdit != null)
                        mEdit.putInt(SETTING_FIELD_PREVIEW_WIDTH, w);
                }

                @Override
                public int getPreviewWidth() {
                    int rtn = 0;
                    if(mShared != null)
                        rtn = mShared.getInt(SETTING_FIELD_PREVIEW_WIDTH, CameraView.CAMERA_PREVIEW_WIDTH_DEFAULT);

                    return rtn;
                }

                @Override
                public void setPreviewHeight(int h) {
                    if(mEdit != null)
                        mEdit.putInt(SETTING_FIELD_PREVIEW_HEIGHT, h);
                }

                @Override
                public int getPreviewHeight() {
                    int rtn = 0;
                    if(mShared != null)
                        rtn = mShared.getInt(SETTING_FIELD_PREVIEW_HEIGHT, CameraView.CAMERA_PREVIEW_HEIGHT_DEFAULT);

                    return rtn;
                }

                @Override
                public void setSnapshotWidth(int w) {
                    if(mEdit != null)
                        mEdit.putInt(SETTING_FIELD_SNAPSHOT_WIDTH, w);
                }

                @Override
                public int getSnapshotWidth() {
                    int rtn = 0;
                    if(mShared != null)
                        rtn = mShared.getInt(SETTING_FIELD_SNAPSHOT_WIDTH, CameraView.CAMERA_SNAPSHOT_WIDTH_DEFAULT);

                    return rtn;
                }

                @Override
                public void setSnapshotHeight(int h) {
                    if(mEdit != null)
                        mEdit.putInt(SETTING_FIELD_SNAPSHOT_HEIGHT, h);
                }

                @Override
                public int getSnapshotHeight() {
                    int rtn = 0;
                    if(mShared != null)
                        rtn = mShared.getInt(SETTING_FIELD_SNAPSHOT_HEIGHT, CameraView.CAMERA_SNAPSHOT_HEIGHT_DEFAULT);

                    return rtn;
                }

                @Override
                public void commit() {
                    if(mEdit != null)
                        mEdit.commit();
                }
            };
        }

    }
}
