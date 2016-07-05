/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package com.txusballesteros.bubbles;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ViewAnimator;

import com.jack.library.R;
import com.jack.library.util.DimenUtils;


public class BubbleLayout extends BubbleBaseLayout {
    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    private OnBubbleRemoveListener onBubbleRemoveListener;
    private OnBubbleClickListener onBubbleClickListener;
    private OnBubblePositionChangedListener onBubblePositionChangedListener;
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long lastTouchDown;
    private MoveAnimator animator;
    private int width;
    private WindowManager windowManager;
    private boolean shouldStickToWall = true;
    private int mFiredAction = -1;


    public void setFiredAction(int actionId) {
        mFiredAction = actionId;
    }

    public void setOnBubbleRemoveListener(OnBubbleRemoveListener listener) {
        onBubbleRemoveListener = listener;
    }

    public void setOnBubbleClickListener(OnBubbleClickListener listener) {
        onBubbleClickListener = listener;
    }

    public void setOnBubblePositionChangedListener(OnBubblePositionChangedListener listener) {
        onBubblePositionChangedListener = listener;
    }

    public BubbleLayout(Context context) {
        super(context);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    public void setShouldStickToWall(boolean shouldStick) {
        this.shouldStickToWall = shouldStick;
    }

    void notifyBubbleRemoved() {
        if (onBubbleRemoveListener != null) {
            if (mFiredAction != -1) {
                onBubbleRemoveListener.onBubbleRemovedWithActionFired(this, mFiredAction);
            } else {
                onBubbleRemoveListener.onBubbleRemoved(this);
            }
        }
    }

    private void initializeView() {
        setClickable(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        playAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    initialX = getViewParams().x;
                    initialY = getViewParams().y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    playAnimationClickDown();
                    lastTouchDown = System.currentTimeMillis();
                    updateSize();
                    animator.stop();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int x = initialX + (int) (event.getRawX() - initialTouchX);
                    int y = initialY + (int) (event.getRawY() - initialTouchY);
                    if(y < 0){
                        y = 0;
                    }
                    getViewParams().x = x;
                    getViewParams().y = y;
                    updatePosition();
                    if (getLayoutCoordinator() != null) {
                        getLayoutCoordinator().notifyBubblePositionChanged(this, x, y);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Point lastPoint = goToWall();
                    boolean actionFired = false;
                    if (getLayoutCoordinator() != null) {
                        actionFired = getLayoutCoordinator().notifyBubbleRelease(this);
                        playAnimationClickUp();
                    }
                    if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                        if (onBubbleClickListener != null) {
                            onBubbleClickListener.onBubbleClick(this);
                        }
                    }
                    if(!actionFired && onBubblePositionChangedListener != null && lastPoint != null) {
                        onBubblePositionChangedListener.onPositionChanged(lastPoint.x, lastPoint.y);
                    }

                    break;
                }

            }
        }
        return super.onTouchEvent(event);
    }


    private void playAnimation() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
                int w = dm.widthPixels;
                int middle = w / 2;
                float nearestXWall = getViewParams().x >= middle ? w : 0;
                float translateX = nearestXWall == 0 ? -getWidth() : getWidth();
                ObjectAnimator animator = ObjectAnimator.ofFloat(BubbleLayout.this, ViewAnimator.X, translateX, 0);
                animator.setInterpolator(new OvershootInterpolator());
                animator.setDuration(400);
                animator.start();
                return true;
            }
        });

    }

    private void playAnimationClickDown() {
        if (!isInEditMode()) {
            int resId = R.animator.bubble_down_click_animator;
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), resId);
            animator.setTarget(this);
            animator.start();
        }
    }

    private void playAnimationClickUp() {
        if (!isInEditMode()) {
            int resId = R.animator.bubble_up_click_animator;
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), resId);
            animator.setTarget(this);
            animator.start();
        }
    }

    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = (size.x - this.getWidth());

    }

    public interface OnBubbleRemoveListener {
        void onBubbleRemoved(BubbleLayout bubble);
        void onBubbleRemovedWithActionFired(BubbleLayout bubble, int actionId);
    }

    public interface OnBubbleClickListener {
        void onBubbleClick(BubbleLayout bubble);
    }

    public interface OnBubblePositionChangedListener {
        void onPositionChanged(int x, int y);
    }

    public Point goToWall() {
        if (shouldStickToWall) {
            int middle = width / 2;
            //make the bubble fit the side of screen, because the bubble image has padding
            float offsetX = Math.round(DimenUtils.dp2px(4));
            float nearestXWall = getViewParams().x >= middle ? (width + offsetX): -offsetX;
            animator.start(nearestXWall, getViewParams().y);
            return new Point(Math.round(nearestXWall), getViewParams().y);
        }
        return null;
    }

    private void move(float deltaX, float deltaY) {
        getViewParams().x += deltaX;
        getViewParams().y += deltaY;
        initialX = getViewParams().x;
        initialY = getViewParams().y;
        updatePosition();
    }


    private class MoveAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() != null && getRootView().getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX - getViewParams().x) * progress;
                float deltaY = (destinationY - getViewParams().y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);

        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (shouldStickToWall) {
            int middle = height / 2;
            float nearestXWall = mX >= middle ? width : 0;
            getViewParams().x = (int) nearestXWall;
        }

        getViewParams().y = (int) (height * ((float) mY / width));

        updatePosition();
    }

    private void updatePosition() {
        if (isShown()) {
            try {
                windowManager.updateViewLayout(this, getViewParams());
                mX = getViewParams().x;
                mY = getViewParams().y;
            } catch (Exception e) {

            }
        }
    }

    private int mX;
    private int mY;

    public boolean isTouchedOnce() {
        return lastTouchDown != 0;
    }
}
