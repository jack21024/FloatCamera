package com.jack.library.camera.feature;

import java.util.List;

/**
 * This interface defines the operations of camera parameters setting.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface ICameraParameter {

    /**
     * Gets the supported preview sizes
     *
     * @return
     */
    public List<int[]> getSupportedPreviewSizes();

    /**
     * Gets the supported picture sizes.
     *
     * @return
     */
    public List<int[]> getSupportedPictureSizes();

    /**
     * Sets the dimensions for preview
     *
     * @param w
     * @param h
     */
    public void setPreviewSize(int w, int h);

    /**
     * Sets the dimensions for pictures
     *
     * @param w
     * @param h
     */
    public void setSnapshotSize(int w, int h);

    /**
     * Sets the image format for preview pictures
     *
     * @param pixel_format
     */
    public void setPreviewFormat(int pixel_format);

    /**
     * Sets the image format for pictures
     *
     * @param pixel_format
     */
    public void setPictureFormat(int pixel_format);

    /**
     * Changes the parameters of this camera service if camera is starting preview
     */
    public void updateCameraParameters();

}
