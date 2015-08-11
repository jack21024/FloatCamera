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
 * FloatWindow is a wrapper which can let the view always floating on the screen.<br/>
 * After window is created with given view, next steps is to call layout() to display
 * the view floating on the screen.
 * <p/>
 * Author: Jack Tseng (jack21024@gmail.com)
 */
public class FloatWindow implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    public static final String TAG = FloatWindow.class.getSimpleName();

    /**
     * Default width of window
     */
    public static final int WINDOWS_WIDTH_DEFAULT = 320;
    /**
     * Default height of window
     */
    public static final int WINDOWS_HEIGHT_DEFAULT = 240;
    /**
     * Default location of x axis of window
     */
    public static final int WINDOWS_COORDINATE_X_DEFAULT = 0;
    /**
     * Default location of y axis of window
     */
    public static final int WINDOWS_COORDINATE_Y_DEFAULT = 0;

    private Context mContext;
    private WindowManager mWindowManager;

    /**
     * A flag indicates this view is showing
     */
    private boolean mIsLayoutDisplayed = false;
    /**
     * A flag to enable the window fits automatically
     */
    private boolean mIsLayoutFitContent = true;
    /**
     * A flag to enable the window floating
     */
    private boolean mIsMovable = true;

    /**
     * This view is a content of window
     */
    private View mLayout;

    /**
     * Layout parameters of this window
     */
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

        /**
         * Initializes the layout parameters for window display
         */
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        /**
         * Takes the dimension of device
         */
        DisplayMetrics m = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(m);
        mDeviceWidth = m.widthPixels;
        mDeviceHeight = m.heightPixels;

        mLayout.setOnTouchListener(this);
    }

    /**
     * Cleans the view of content and releases all resource of window
     */
    public void release() {
        if(mWindowManager != null && mLayout != null)
            mWindowManager.removeView(mLayout);

        mContext = null;
        mWindowManager = null;
        mLayout = null;
        mLayoutParams = null;

        mIsLayoutDisplayed = false;

        mTouchStartX = 0;
        mTouchStartY = 0;
    }

    /**
     * Returns true if window will fit content automatically
     *
     * @return
     */
    public boolean isIsLayoutFitContent() {
        return mIsLayoutFitContent;
    }

    /**
     * Sets the window to enable fitting the content automatically
     *
     * @param isFit
     */
    public void setLayoutFitContent(boolean isFit) {
        mIsLayoutFitContent = isFit;
    }

    /**
     * Returns true if the window is enabled to move on the screen
     *
     * @return
     */
    public boolean isMovable() {
        return mIsMovable;
    }

    /**
     * Enables the window to move on the screen by finger touch
     *
     * @param isMovable
     */
    public void setMovable(boolean isMovable) {
        mIsMovable = isMovable;
    }

    /**
     * Sets the width of the window
     *
     * @param w
     */
    public void setWidth(int w) {
        if(mLayoutParams != null)
            mLayoutParams.width = w;
    }

    /**
     * Sets the height of the window
     *
     * @param h
     */
    public void setHeight(int h) {
        if(mLayoutParams != null)
            mLayoutParams.height = h;
    }

    /**
     *  Gets the width of the window
     *
     * @return
     */
    public int getWidth() {
        return (mLayoutParams == null) ? -1 : mLayoutParams.width;
    }

    /**
     * Gets the height of the window
     *
     * @return
     */
    public int getHeight() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.height;
    }

    /**
     * Sets the coordinate of window on the screen
     * @param x
     * @param y
     */
    public void setCoordinate(int x, int y) {
        if(mLayoutParams == null) return;

        mLayoutParams.x = x;
        mLayoutParams.y = y;
    }

    /**
     * Gets the x coordinate of window on the screen
     *
     * @return
     */
    public int getX() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.x;
    }

    /**
     * Gets the y coordinate of window on the screen
     *
     * @return
     */
    public int getY() {
        return  (mLayoutParams == null) ? -1 : mLayoutParams.y;
    }

    /**
     * Register a callback to be invoked when this view is touched
     *
     * @param listener
     */
    public void setOnEventListener(OnEventListener listener) {
        mEventListener = listener;
    }

    /**
     * Register a callback to be invoked when layout is completed of this view
     *
     * @param listener
     */
    public void setOnLayoutCompleteListener(OnLayoutCompleteListener listener) {
        mOnLayoutCompleteListener = listener;
    }

    /**
     * Sets the pixel format of layout parameter of Window
     *
     * @param pixelFormat
     */
    public void setLayoutFormat(int pixelFormat) {
        mLayoutParams.format = pixelFormat;
    }

    /**
     * Displays the window on the screen
     */
    public void layout() {
        if(mWindowManager == null) return;
        if(mLayout == null) return;
        if(mLayoutParams == null) return;

        if(!mIsLayoutDisplayed) {
            ViewTreeObserver vto = mLayout.getViewTreeObserver();
            if(vto != null)
                vto.addOnGlobalLayoutListener(this);

            mWindowManager.addView(mLayout, mLayoutParams);

            /**
             * Resize the window to fix the content.
             * Note: Calls the function View.measure() before you need to add view to parent.
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

        mIsLayoutDisplayed = true;

        Debug.dumpLog(TAG, "displays layout to screen");
    }

    /**
     * Closes this window and releases the resource of window
     */
    public void dismiss() {
        if(mWindowManager == null) return;
        if(mLayout == null) return;
        if(!mIsLayoutDisplayed) return;

        mWindowManager.removeView(mLayout);
        mIsLayoutDisplayed = false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Debug.dumpLog(TAG, "onTouch()->x=" + motionEvent.getX() + " y=" + motionEvent.getY()
            + " w=" + view.getWidth() + " h=" + view.getHeight());

        if(!mIsMovable) return false;

        /**
         * Updates the newly position of this Window according to given x, y position of the
         * OnTouch event.
         */
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

        /**
         * Fires callback of onTouch of OnEventListener
         */
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

    /**
     * OnEventListener allows to catch the onTouch event of FloatWindow.
     */
    public interface OnEventListener {
        /**
         * Called when the onTouch event of FloatWindow is triggered
         *
         * @param v
         * @param motionEvent
         */
        public void onTouch(View v, MotionEvent motionEvent);
    }

    /**
     * OnLayoutCompleteListener allows to catch the onLayoutCompleted event of FloatWindow (same as
     * ViewTreeObserver.OnGlobalLayoutListener).
     */
    public interface OnLayoutCompleteListener {
        /**
         * Called when the OnGlobalLayoutListener of FloatWindow of ViewTreeObserver is triggered
         */
        public void onLayoutCompleted();
    }
}
