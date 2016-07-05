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

import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

final class BubblesLayoutCoordinator {
    private static BubblesLayoutCoordinator INSTANCE;
    private WeakReference<BubbleTrashLayout> mTrashViewRef;
    private WindowManager windowManager;
    private WeakReference<BubblesService> mBubbleServiceRef;

    private boolean mIsHoveredAction = false;
    private ArrayList<OnPositionChangedListener> mOnPositionChangedListener = new ArrayList<>();
    public interface OnPositionChangedListener {
        void onHoverAction(int action, BubbleLayout bubble);
        void onLeaveAction(BubbleLayout bubble);
    }
    public void setPositionChangedListener(OnPositionChangedListener listener) {
        mOnPositionChangedListener.add(listener);
    }

    private static BubblesLayoutCoordinator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BubblesLayoutCoordinator();
        }
        return INSTANCE;
    }

    private BubblesLayoutCoordinator() {
    }

    private int mCurrentAction = BubbleTrashLayout.NOT_FOUND;

    public void notifyBubblePositionChanged(BubbleLayout bubble, int x, int y) {
        if (mTrashViewRef != null && mTrashViewRef.get()!=null) {
            mTrashViewRef.get().setVisibility(View.VISIBLE);

            int overlappedAction = mTrashViewRef.get().checkIfBubbleIsOverTrash(bubble);

            if (overlappedAction != BubbleTrashLayout.NOT_FOUND) {
                Log.i("bubble", "Overlapped action: " + overlappedAction);

                mTrashViewRef.get().applyMagnetism();
                applyTrashMagnetismToBubble(bubble, overlappedAction);

                if (mCurrentAction != overlappedAction) {
                    mCurrentAction = overlappedAction;
                    releaseBubble(bubble);
                }

                if(!mIsHoveredAction) {
                    mIsHoveredAction = true;
                    if (mOnPositionChangedListener != null)
                        for(OnPositionChangedListener listener : mOnPositionChangedListener) {
                            listener.onHoverAction(overlappedAction, bubble);
                        }
                }
            } else {
                mTrashViewRef.get().releaseMagnetism();
                releaseBubble(bubble);
            }
        }
    }

    private void releaseBubble(BubbleLayout bubble) {
        if(mIsHoveredAction) {
            mIsHoveredAction = false;
            if (mOnPositionChangedListener != null)
                for(OnPositionChangedListener listener : mOnPositionChangedListener) {
                    listener.onLeaveAction(bubble);
                }
        }
    }

    private void applyTrashMagnetismToBubble(BubbleLayout bubble, int index) {
        if(mTrashViewRef.get()!=null) {
            BubbleActionView bubbleActionView = mTrashViewRef.get().getOverlappedBubbleAction();
            int trashCenterX = (bubbleActionView.getRelativeLeft(bubbleActionView) + (bubbleActionView.getMeasuredWidth() / 2));
            int trashCenterY = (bubbleActionView.getRelativeTop(bubbleActionView) + (bubbleActionView.getMeasuredHeight() / 2));
            int x = (trashCenterX - (bubble.getMeasuredWidth() / 2));
            int y = (trashCenterY - (bubble.getMeasuredHeight() / 2));
            bubble.getViewParams().x = x;
            bubble.getViewParams().y = y;

            windowManager.updateViewLayout(bubble, bubble.getViewParams());
        }
    }

    public boolean notifyBubbleRelease(BubbleLayout bubble) {
        boolean actionFired = false;
        if (mTrashViewRef != null && mTrashViewRef.get() != null) {
            if (mTrashViewRef.get().checkIfBubbleIsOverTrash(bubble) != BubbleTrashLayout.NOT_FOUND) {
                bubble.setFiredAction(mTrashViewRef.get().getOverlappedBubbleAction().getId());
                if (mBubbleServiceRef.get() != null) {
                    mBubbleServiceRef.get().removeBubble(bubble);
                }
                actionFired = true;
            }
            mTrashViewRef.get().setVisibility(View.GONE);
        }
        return actionFired;
    }

    public static class Builder {
        private BubblesLayoutCoordinator layoutCoordinator;

        public Builder(BubblesService service) {
            layoutCoordinator = getInstance();
            layoutCoordinator.mBubbleServiceRef = new WeakReference<BubblesService>(service);
        }

        public Builder setTrashView(BubbleTrashLayout trashView) {
            layoutCoordinator.mTrashViewRef = new WeakReference<BubbleTrashLayout>(trashView);
            return this;
        }

        public Builder setWindowManager(WindowManager windowManager) {
            layoutCoordinator.windowManager = windowManager;
            return this;
        }

        public BubblesLayoutCoordinator build() {
            return layoutCoordinator;
        }
    }


}
