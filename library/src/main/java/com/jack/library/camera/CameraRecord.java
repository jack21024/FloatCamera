package com.jack.library.camera;

/**
 * Created by jacktseng on 2015/8/6.
 */
public interface CameraRecord<T> {

    public void setTime(long t);

    public long getTime();

//    public  void setImage(Bitmap image);
//
//    public Bitmap getImage();

    public void setRecord(T record);

    public T getRecord();

    public String getResourcePath();

    public void setResourcePath(String path);

}
