package com.jack.library.camera.feature;

import com.jack.library.camera.listener.OnSnapshotListener;

/**
 * ICameraCapture is in charge of capturing camera snapshot or recording.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface ICameraCapture {

    /**
     * Snapshot is a raw(uncompressed) image data
     * <p/>
     * Not implemented
     */
    public static final int CAMERA_SNAPSHOT_TYPE_RAW    = 0x01;
    /**
     * Snapshot is a postview image date
     * <p/>
     * Not implemented
     */
    public static final int CAMERA_SNAPSHOT_TYPE_POST   = 0x11;
    /**
     * Snapshot is a JPEG image data
     */
    public static final int CAMERA_SNAPSHOT_TYPE_JPEG   = 0x21;

    /**
     * Adds a OnSnapshotLister instance to listen the snapshot callback event
     *
     * @param listener
     */
    public void addSnapshotListener(OnSnapshotListener listener);

    /**
     *
     * Removes a listener of OnSnapshotListener
     * @param listener
     */
    public void removeSnapshotListener(OnSnapshotListener listener);

    /**
     * Triggers an asynchronous image capture
     */
    public void snapshot();

    /**
     * Begins capturing and encoding data to the file specified with given path
     *
     * @param path
     */
    public void startRecording(String path);

    /**
     * Stops recording
     */
    public void stopRecording();

}
