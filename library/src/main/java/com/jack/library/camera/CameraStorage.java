package com.jack.library.camera;

import java.util.List;

/**
 * This class is used to help accessing camera record (image, video) to storage which may a external
 * storage or a web service.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface CameraStorage {

    /**
     * Lists all of support storage's type
     */
    public static enum STORAGE_TYPE {
        LOCAL_DEFAULT,
        REMOTE_DEFAULT //not implemented
    }

    /**
     * Sets the root path which can be a file description, uri, url for storage accessing
     *
     * @param root
     */
    public void setRoot(String root);

    /**
     * Gets the root path which is a file path, uri, url etc.
     *
     * @return
     */
    public String getRoot();

    /**
     * Saves a record to storage
     *
     * @param record
     * @return
     */
    public boolean save(CameraRecord record);

    /**
     * Gets a list of thumbnails which are used to represent the record that accessing from storage
     *
     * @return
     */
    public List<CameraRecord> loadThumbnail();

    /**
     * Loads a resource of record which can be a image or video
     *
     * @param record
     * @return
     */
    public Object loadResource(CameraRecord record);

}
