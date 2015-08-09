package com.jack.library.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.jack.library.Debug;


/**
 * Created by jacktseng on 2015/6/19.
 */
public class FloatWindow implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    public static final String TAG = FloatWindow.class.getSimpleName();

    public static final int WINDOWS_WIDTH_DEFAULT = 320;
    public static final int WINDOWS_HEIGHT_DEFAULT = 240;
    public static final int WINDOWS_COORDINATE_X_DEFAULT = 0;
    public static final int WINDOWS_COORDINATE_Y_DEFAULT = 0;

    private Context mContext;
    private WindowManager mWindowManager;

    private boolean mIsLayout = false;
    private boolean mIsLayoutFitContent = true;
    private boolean mIsMovable = true;

    private View mLayout;
    private WindowManager.LayoutParams mLayoutParams;

    private int mDeviceWidth;
    private int mDeviceHeight;
    private int mTouchStartX;
    private int mTouchStartY;

    private OnEventListener mEventListener;
    private OnLayoutCompleteListener mOnLayoutCompleteListener;

    public FloatWindow(View layout) {
        mLayout = layout;
        init();
    }

    private void init() {
        mContext = mLayout.getContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        DisplayMetrics m = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(m);
        mDeviceWidth = m.widthPixels;
        mDeviceHeight = m.heightPixels;

        mLayout.setOnTouchListener(this);
    }

    public void release() {
        if(mWindowManager != null && mLayout != null)
            mWindowManager.removeView(mLayout);

        mContext = null;
        mWindowManager = null;
        mLayout = null;
        mLayoutParams = null;

        mIsLayout = false;

        mTouchStartX = 0;
        mTouchStartY = 0;
    }

    public boolean isIsLayoutFitContent() {
        return mIsLayoutFitContent;
    }

    public void setLayoutFitContent(boolean isFit) {
        mIsLayoutFitContent = isFit;
    }

    public boolean isMovable() {
        return mIsMovable;
    }

    public void setMovable(boolean isMovable) {
        mIsMovable = isMovable;
    }

    public void setWidth(int w) {
        if(mLayoutParams != null)
            mLayoutParams.width = w;
    }

    public void setHeight(int h) {
        if(mLayoutParams != null)
            mLayoutParams.height = h;
    }

    public int getWidth() {
        return (mLayoutParams == null) ? -1 : mLayoutParams.width;
    }

    public int getHeight() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.height;
    }

    public void setCoordinate(int x, int y) {
        if(mLayoutParams == null) return;

        mLayoutParams.x = x;
        mLayoutParams.y = y;
    }

    public int getX() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.x;
    }

    public int getY() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.y;
    }

    public void setOnEventListener(OnEventListener listener) {
        mEventListener = listener;
    }

    public void setOnLayoutCompleteListener(OnLayoutCompleteListener listener) {
        mOnLayoutCompleteListener = listener;
    }

    public void setLayoutFormat(int pixelFormat) {
        mLayoutParams.format = pixelFormat;
    }

    public void layout() {
        if(mWindowManager == null) return;
        if(mLayout == null) return;
        if(mLayoutParams == null) return;

        if(!mIsLayout) {
            //jack@150704
            ViewTreeObserver vto = mLayout.getViewTreeObserver();
            if(vto != null)
                vto.addOnGlobalLayoutListener(this);

            mWindowManager.addView(mLayout, mLayoutParams);

            /**
             * Let window size to fix the view.
             * Note: Call the function View.measure() before you need to add view to parent.
             */
            if(mIsLayoutFitContent) {
                mLayout.measure(0, 0);
                mLayoutParams.width = mLayout.getMeasuredWidth();
                mLayoutParams.height = mLayout.getMeasuredHeight();
                mLayoutParams.x = (mDeviceWidth - mLayoutParams.width) / 2;
                mLayoutParams.y = (mDeviceHeight - mLayoutParams.height) / 2;
                mWindowManager.updateViewLayout(mLayout, mLayoutParams);
            }
        }
        else
            mWindowManager.updateViewLayout(mLayout, mLayoutParams);

        mIsLayout = true;

        Debug.dumpLog(TAG, "display layout to screen");
    }

    public void dismiss() {
        if(mWindowManager == null) return;
        if(mLayout == null) return;
        if(!mIsLayout) return;

        mWindowManager.removeView(mLayout);
        mIsLayout = false;
    }

    /**
     * implements for View.OnTouchListener
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Debug.dumpLog(TAG, "onTouch()->x=" + motionEvent.getX() + " y=" + motionEvent.getY()
            + " w=" + view.getWidth() + " h=" + view.getHeight());

        if(!mIsMovable) return false;

        int x = (int) motionEvent.getRawX();
        int y = (int) motionEvent.getRawY()-25;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int) motionEvent.getX();
                mTouchStartY = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                setCoordinate(x-mTouchStartX, y-mTouchStartY);
                layout();
                break;
            case MotionEvent.ACTION_UP:
                mTouchStartX = 0;
                mTouchStartY = 0;
                break;
        }

        if(mEventListener != null)
            mEventListener.onTouch(view, motionEvent);

        return true;
    }

    @Override
    public void onGlobalLayout() {
        ViewTreeObserver vto = mLayout.getViewTreeObserver();
        if(vto != null)
            vto.removeOnGlobalLayoutListener(this);
        if(mOnLayoutCompleteListener != null)
            mOnLayoutCompleteListener.onLayoutCompleted();
    }

    public interface OnEventListener {
        public void onTouch(View v, MotionEvent motionEvent);
    }

    public interface OnLayoutCompleteListener {
        public void onLayoutCompleted();
    }
}
