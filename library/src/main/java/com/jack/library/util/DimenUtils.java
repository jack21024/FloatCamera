package com.jack.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

public class DimenUtils {
    private static DisplayMetrics mMetrics = null;
    private static Resources mResource = null;

    private static final int DP_TO_PX = TypedValue.COMPLEX_UNIT_DIP;
    private static final int SP_TO_PX = TypedValue.COMPLEX_UNIT_SP;
    private static final int PX_TO_DP = TypedValue.COMPLEX_UNIT_MM + 1;
    private static final int PX_TO_SP = TypedValue.COMPLEX_UNIT_MM + 2;
    private static final int DP_TO_PX_SCALE_H = TypedValue.COMPLEX_UNIT_MM + 3;
    private static final int DP_SCALE_H = TypedValue.COMPLEX_UNIT_MM + 4;
    private static final int DP_TO_PX_SCALE_W = TypedValue.COMPLEX_UNIT_MM + 5;

    public static final int DENSITY_LOW = 120;
    public static final int DENSITY_MEDIUM = 160;
    public static final int DENSITY_HIGH = 240;
    public static final int DENSITY_XHIGH = 320;

    // -- dimens convert
    private static float applyDimension(int unit, float value, DisplayMetrics metrics) {
        switch (unit) {
            case DP_TO_PX:
            case SP_TO_PX:
                return TypedValue.applyDimension(unit, value, metrics);
            case PX_TO_DP:
                return value / metrics.density;
            case PX_TO_SP:
                return value / metrics.scaledDensity;
            case DP_TO_PX_SCALE_H:
                return TypedValue.applyDimension(DP_TO_PX, value * getScaleFactorH(), metrics);
            case DP_SCALE_H:
                return value * getScaleFactorH();
            case DP_TO_PX_SCALE_W:
                return TypedValue.applyDimension(DP_TO_PX, value * getScaleFactorW(), metrics);
        }
        return 0;
    }
    public static int dp2px(Context context, float value) {
        return (int) applyDimension(DP_TO_PX, value, context.getResources().getDisplayMetrics());
    }
    public static int dp2px(float value) {
        if(!isInited()) {
            return (int)value;
        }
        return (int) applyDimension(DP_TO_PX, value, mMetrics);
    }

    public static float px2dp(float value) {
        if(!isInited()) {
            return (int)value;
        }
        return (int) applyDimension(PX_TO_DP, value, mMetrics);
    }

    public static int dp2pxScaleW(float value) {
        if(!isInited()) {
            return (int)value;
        }
        return (int) applyDimension(DP_TO_PX_SCALE_W, value, mMetrics);
    }

    public static int dp2pxScaleH(float value) {
        if(!isInited()) {
            return (int)value;
        }
        return (int) applyDimension(DP_TO_PX_SCALE_H, value, mMetrics);
    }

    public static int dpScaleH(float value) {
        if(!isInited()) {
            return (int)value;
        }
        return (int) applyDimension(DP_SCALE_H, value, mMetrics);
    }

    public final static float BASE_SCREEN_WIDH = 720f;
    public final static float BASE_SCREEN_HEIGHT = 1280f;
    public final static float BASE_SCREEN_DENSITY = 2f;
    public static Float sScaleW, sScaleH;

    /**
     * 如果要计算的值已经经过dip计算，则使用此结果，如果没有请使用getScaleFactorWithoutDip
     */
    public static float getScaleFactorW() {
        if (sScaleW == null) {
            sScaleW = (getScreenWidth() * BASE_SCREEN_DENSITY) / (getDensity() * BASE_SCREEN_WIDH);
        }
        return sScaleW;
    }

    public static float getScaleFactorH() {
        if (sScaleH == null) {
            sScaleH = (getScreenHeight() * BASE_SCREEN_DENSITY)
                    / (getDensity() * BASE_SCREEN_HEIGHT);
        }
        return sScaleH;
    }

    public static int getScreenWidth() {
        if(!isInited()) {
            return 720;
        }
        return mMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        if(!isInited()) {
            return 1280;
        }
        return mMetrics.heightPixels;
    }

    public static float getScreenDensity() {
        if(!isInited()) {
            return 1.0f;
        }
        return mMetrics.density;
    }


    public static float getDensity() {
        if(!isInited()) {
            return 2;
        }
        return mMetrics.density;
    }

    public static int getScreenLayoutSize(Context context) {
        final int screenSize = context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize;
    }

    public static void createLayout(View view, int w, int h) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        if (w != -3)
            params.width = w;
        if (h != -3)
            params.height = h;
        view.setLayoutParams(params);
    }

    public static void createListviewLayout(View view, int w, int h) {
        ListView.LayoutParams lp = (ListView.LayoutParams) view
                .getLayoutParams();
        if (lp == null) {
            if (w == -3)
                w = LayoutParams.MATCH_PARENT;
            if (h == -3)
                h = LayoutParams.MATCH_PARENT;
            lp = new ListView.LayoutParams(w, h);
            view.setLayoutParams(lp);
        }
    }

    public static void updateLayout(View view, int w, int h) {
        if (view == null)
            return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        if (w != -3)
            params.width = w;
        if (h != -3)
            params.height = h;
        view.setLayoutParams(params);
    }

    public static void updateLayoutMargin(View view, int l, int t, int r, int b) {
        if (view == null)
            return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;
        if (params instanceof RelativeLayout.LayoutParams) {
            updateMargin(view, (RelativeLayout.LayoutParams) params, l, t, r, b);
        } else if (params instanceof LinearLayout.LayoutParams) {
            updateMargin(view, (LinearLayout.LayoutParams) params, l, t, r, b);
        } else if (params instanceof FrameLayout.LayoutParams) {
            updateMargin(view, (FrameLayout.LayoutParams) params, l, t, r, b);
        }
    }

    public static void updateRelativeLeftToRight(View leftview, int leftMargin,
                                                 View rightview, int rightMargin, int between) {
        RelativeLayout.LayoutParams lpLeft = (RelativeLayout.LayoutParams) leftview
                .getLayoutParams();
        lpLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpLeft.leftMargin = leftMargin;
        lpLeft.rightMargin = between;
        // clear before
        lpLeft.addRule(RelativeLayout.RIGHT_OF, -1);
        leftview.setLayoutParams(lpLeft);

        RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) rightview
                .getLayoutParams();
        lpRight.addRule(RelativeLayout.RIGHT_OF, leftview.getId());
        lpRight.rightMargin = rightMargin;
        // clear before
        lpRight.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        lpRight.leftMargin = 0;
        rightview.setLayoutParams(lpRight);
    }

    private static void updateMargin(View view, ViewGroup.MarginLayoutParams params, int l, int t,
                                     int r, int b) {
        if (view == null)
            return;
        if (l != -3)
            params.leftMargin = l;
        if (t != -3)
            params.topMargin = t;
        if (r != -3)
            params.rightMargin = r;
        if (b != -3)
            params.bottomMargin = b;
        view.setLayoutParams(params);
    }

    public static int getWindowWidth() {
        if(!isInited()){
            return 1280;
        }
        return mMetrics.widthPixels;
    }

    public static int getWindowHeight() {
        if(!isInited()){
            return 720;
        }
        return mMetrics.heightPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    public static int getStatusBarHeight2() {
        int statusBarHeight = 0;
        try {
            Class<?> cl = Class.forName("com.android.internal.R$dimen");
            Object obj = cl.newInstance();
            Field field = cl.getField("status_bar_height");

            int x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mResource.getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int sp2px(float value) {
        if(null == mMetrics) {
            return (int)value;
        }
        return (int) applyDimension(SP_TO_PX, value, mMetrics);
    }

    public static int getContentHeight2(Context context) {
        int height = mMetrics.heightPixels - getStatusBarHeight2();
        return height;
    }

    public static boolean isInited() {
        return (null != mMetrics);
    }

    @SuppressWarnings("infer")
    public synchronized static boolean initMetrics(Context context) {
        if(null != mMetrics) {
            return true;
        }
        if(null == context) {
            return false;
        }
        mResource = context.getResources();
        if(null == mResource) {
            return  false;
        }
        mMetrics = mResource.getDisplayMetrics();
        if(null != mMetrics) {
            return true;
        }
        return false;
    }

    public static int getNavigationBarHeight(Context context){
        if(context == null){
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
