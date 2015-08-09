package com.jack.library.camera;

/**
 * Created by jacktseng on 2015/8/8.
 */
public interface Cameras<T> {

    public T getCamera();

    public void start();

    public void stop();

    public void release();
}
