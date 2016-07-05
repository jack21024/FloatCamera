package com.txusballesteros.bubbles;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HoverBubble extends BubbleLayout {

    private static final String TAG = "HoverBubble";

    public boolean mIsHover = false;

    public HoverBubble(Context context) {
        super(context);
        init();
    }

    public HoverBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoverBubble(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setTranslucent(boolean isTransLucent) {
        if(isTransLucent)
            setAlpha(0.3f);
        else
            setAlpha(1.0f);
        invalidate();
    }


    private ViewGroup mHoverWrapper;
    private Drawable mHoverDrawable;
    private View mHoverView;
    public void setHoverResource(Drawable hoverDrawable, ViewGroup hoverWrapper) {
        mHoverDrawable = hoverDrawable;
        mHoverWrapper = hoverWrapper;
    }

    public void showHover() {
        if(mHoverWrapper == null || mHoverDrawable == null) return;
        if(mHoverView != null) return;
        try {
            ImageView iv = new ImageView(getContext());
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
            iv.setImageDrawable(mHoverDrawable);
            iv.setLayoutParams(lp);
            iv.setAlpha(0.5f);
            mHoverWrapper.addView(iv);
            invalidate();

            mHoverView = iv;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHoverListener() {
        getLayoutCoordinator().setPositionChangedListener(mHoverListener);
    }

    public void closeHover() {
        if(mHoverView == null || mHoverWrapper == null) return;
        mHoverWrapper.removeView(mHoverView);
        invalidate();
        mHoverView = null;
    }

    private BubblesLayoutCoordinator.OnPositionChangedListener mHoverListener = new BubblesLayoutCoordinator.OnPositionChangedListener() {
        @Override
        public void onHoverAction(int action, BubbleLayout bubble) {
            if(bubble == HoverBubble.this) {
                showHover();
            }
        }

        @Override
        public void onLeaveAction(BubbleLayout bubble) {
            if(bubble == HoverBubble.this) {
                closeHover();
            }
        }
    };

}
