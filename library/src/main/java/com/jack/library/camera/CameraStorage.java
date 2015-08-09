package com.jack.library.camera;

import java.util.List;

/**
 * Created by jacktseng on 2015/8/6.
 */
public interface CameraStorage {

    public static enum STORAGE_TYPE {
        LOCAL_DEFAULT,
        REMOTE_DEFAULT //not implements
    }

    public void setRoot(String root);

    public String getRoot();

    public boolean save(CameraRecord record);

    public List<CameraRecord> loadThumbnail();

    public Object loadResource(CameraRecord record);

}
