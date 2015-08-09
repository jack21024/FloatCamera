package com.jack.sys;

/**
 * Created by jacktseng on 2015/6/24.
 */
public interface AppConfig {

    public void setPreviewWidth(int w);

    public int getPreviewWidth();

    public void setPreviewHeight(int h);

    public int getPreviewHeight();

    public void setSnapshotWidth(int w);

    public int getSnapshotWidth();

    public void setSnapshotHeight(int h);

    public int getSnapshotHeight();

    public void commit();

}
