package com.txusballesteros.bubbles;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Jasper on 16/1/15.
 */
public class BubbleActionView extends RelativeLayout {

    public BubbleActionView(Context context) {
        super(context);
    }

    public BubbleActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isOverlappedWithBubble(int bubbleLeft, int bubbleRight, int bubbleTop, int bubbleBottom) {
        int trashWidth = getMeasuredWidth();
        int trashHeight = getMeasuredHeight();
        int trashLeft = (getRelativeLeft(this));
        int trashRight = (trashLeft + trashWidth);
        int trashTop = (getRelativeTop(this));
        int trashBottom = (trashTop + trashHeight);

        Rect rect1 = new Rect(trashLeft, trashTop, trashRight, trashBottom);
        Rect rect2 = new Rect(bubbleLeft, bubbleTop, bubbleRight, bubbleBottom);

        return rect1.intersect(rect2);
    }

    public int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    public int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
