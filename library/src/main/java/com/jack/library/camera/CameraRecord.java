package com.jack.library.camera;

/**
 * This class is a java bean for keeping the record of camera. <br/>
 * The record is a camera record which is loaded in memory. However, some types of record such as
 * video can't do that, so the resource path is a good way to indicate how to access those record
 * from storage.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface CameraRecord<T> {

    /**
     * Sets a time of record capturing
     *
     * @param t
     */
    public void setTime(long t);

    /**
     * Gets a time of record capturing
     *
     * @return
     */
    public long getTime();

    /**
     * Sets a camera record which can be any object to indication
     *
     * @param record
     */
    public void setRecord(T record);

    /**
     * Gets a record of camera
     *
     * @return
     */
    public T getRecord();

    /**
     * Gets the path that can be able to access the record from storage
     *
     * @return
     */
    public String getResourcePath();

    /**
     * Sets the path to indicate how to access this record from storage
     *
     * @param path
     */
    public void setResourcePath(String path);

}
