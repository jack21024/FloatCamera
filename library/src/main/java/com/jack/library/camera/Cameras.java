package com.jack.library.camera;

/**
 * An Cameras keeps the camera object of devices and supports some important operations for camera
 * controlling.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public interface Cameras<T> {

    /**
     * Gets a camera instance which is determined by android api level
     *
     * @return
     */
    public T getCamera();

    /**
     * Starts capturing and drawing preview frames to the screen
     */
    public void start();

    /**
     * Stops capturing and drawing preview frames to the screen
     */
    public void stop();

    /**
     * Disconnects and releases the Camera object resources
     * <p/>
     * Note: Make sure to do this once a camera never to use
     */
    public void release();
}
