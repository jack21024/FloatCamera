package com.jack.sys;

/**
 * AppConfig is in charge of to store the app config.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
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

    /**
     * Commits settings to update
     */
    public void commit();

}
