package com.jack.library.camera;

/**
 * Created by jacktseng on 2015/8/8.
 */
public interface ICameraCapture {

    public static final int CAMERA_SNAPSHOT_TYPE_RAW    = 0x01;
    public static final int CAMERA_SNAPSHOT_TYPE_POST   = 0x11;
    public static final int CAMERA_SNAPSHOT_TYPE_JPEG   = 0x21;

    public void addSnapshotListener(OnSnapshotListener listener);

    public void removeSnapshotListener(OnSnapshotListener listener);

    public void snapshot();

    public void startRecording();

    public void stopRecording();

}
