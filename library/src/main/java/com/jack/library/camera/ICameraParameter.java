package com.jack.library.camera;

import java.util.List;

/**
 * Created by jacktseng on 2015/8/8.
 */
public interface ICameraParameter {

//    public boolean checkPreviewSize(int w, int h);
//
//    public boolean checkSnapshotSize(int w, int h);

    public List<int[]> getSupportedPreviewSizes();

    public List<int[]> getSupportedPictureSizes();

    public void setPreviewSize(int w, int h);

    public void setSnapshotSize(int w, int h);

    public void setPreviewFormat(int pixel_format);

    public void setPictureFormat(int pixel_format);

    public void updateCameraParameters();

}
