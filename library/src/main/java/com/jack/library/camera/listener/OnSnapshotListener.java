package com.jack.library.camera.listener;

import com.jack.library.camera.CameraRecord;

/**
 * This listener goals to gets image when snapshot callback is triggered.
 * </p>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface OnSnapshotListener {

    /**
     * Called when image data is available after a snapshot is taken
     * </p>
     * The parameter of imageType indicates the image data type of this image
     *
     * @param imageDataType
     * @param record
     */
    public void onSnapshot(int imageDataType, CameraRecord record);
}
