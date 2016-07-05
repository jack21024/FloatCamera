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
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;


import com.jack.library.R;

import java.util.ArrayList;
import java.util.List;

class BubbleTrashLayout extends BubbleBaseLayout {
    public static final int VIBRATION_DURATION_IN_MS = 70;
    private boolean magnetismApplied = false;
    private boolean attachedToWindow = false;

    public BubbleTrashLayout(Context context) {
        super(context);
    }

    public BubbleTrashLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleTrashLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    List<BubbleActionView> mBubbleActionViews;

    public void init() {
        mBubbleActionViews = new ArrayList<BubbleActionView>();
        mBubbleActionViews.addAll(getBubbleActionViewInViewGroup(this));

        if (mBubbleActionViews.isEmpty()) {
            throw new IllegalArgumentException("Trash Layout must contain at least one BubbleActionView");
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
    }

    @Override
    public void setVisibility(int visibility) {
        if (attachedToWindow) {
            if (visibility != getVisibility()) {
                if (visibility == VISIBLE) {
                    playAnimationShown();
                } else {

                }
            }
        }
        super.setVisibility(visibility);
    }

    void playAnimationShown() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(400);
        animator.start();
    }

    void applyMagnetism() {
        if(mOverlappedBubbleActionIndex != mLastOverlappedBubbleActionIndex) {
            releaseMagnetism(mLastOverlappedBubbleActionIndex);
        }
        if (!magnetismApplied) {
            magnetismApplied = true;
            int resId = R.animator.bubble_trash_shown_magnetism_animator;
            playAnimation(resId, mOverlappedBubbleActionIndex);
        }
    }

    void vibrate() {
        final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_DURATION_IN_MS);
    }

    void releaseMagnetism() {
        releaseMagnetism(mLastOverlappedBubbleActionIndex);
    }

    private void releaseMagnetism(int index) {
        if (magnetismApplied) {
            magnetismApplied = false;
            int resId = R.animator.bubble_trash_hide_magnetism_animator;
            playAnimation(resId, index);
        }
    }

    private void playAnimation(int animationResourceId, int bubbleIndex) {
        if (!isInEditMode()) {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), animationResourceId);
            animator.setTarget(bubbleIndex == -1 ? getChildAt(0) : mBubbleActionViews.get(bubbleIndex));
            animator.start();
        }
    }

    public static final int NOT_FOUND = -1;

    public int checkIfBubbleIsOverTrash(BubbleLayout bubble) {
        mLastOverlappedBubbleActionIndex = mOverlappedBubbleActionIndex;
        mOverlappedBubbleActionIndex = NOT_FOUND;
        if (getVisibility() == View.VISIBLE) {
            int bubbleWidth = bubble.getMeasuredWidth();
            int bubbleHeight = bubble.getMeasuredHeight();
            int bubbleLeft = bubble.getViewParams().x;
            int bubbleRight = bubbleLeft + bubbleWidth;
            int bubbleTop = bubble.getViewParams().y;
            int bubbleBottom = bubbleTop + bubbleHeight;

            int index = 0;
            for (BubbleActionView actionView : mBubbleActionViews) {
                if (actionView.isOverlappedWithBubble(bubbleLeft, bubbleRight, bubbleTop, bubbleBottom)) {
                    mOverlappedBubbleActionIndex = index;
                    break;
                }
                index++;
            }

        }
        return mOverlappedBubbleActionIndex;
    }

    private int mOverlappedBubbleActionIndex = -1;
    private int mLastOverlappedBubbleActionIndex = -1;

    private List<BubbleActionView> getBubbleActionViewInViewGroup(ViewGroup vg) {
        List<BubbleActionView> actionViews = new ArrayList<BubbleActionView>();
        View child;
        for (int i = 0, count = vg.getChildCount(); i < count; i++) {
            child = vg.getChildAt(i);
            if (child instanceof BubbleActionView) {
                actionViews.add((BubbleActionView) child);
            } else if (child instanceof ViewGroup) {
                actionViews.addAll(getBubbleActionViewInViewGroup((ViewGroup) child));
            }
        }
        return actionViews;
    }

    public BubbleActionView getOverlappedBubbleAction() {
        return mBubbleActionViews.get(mOverlappedBubbleActionIndex);
    }


}
