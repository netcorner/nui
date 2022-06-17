package com.ingwill.widget.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by netcorner on 16/4/27.
 */
public class FixedViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public FixedViewPager(Context context) {
        super(context);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public boolean isCanScroll() {
        return isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        if (isCanScroll) {
            return super.onTouchEvent(arg0);
        } else {
            return false;
        }

    }
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item);
    }
    private int lastX1,lastX2;
    private boolean isInterceptChildViewMoveTouch=false;

    public boolean isInterceptChildViewMoveTouch() {
        return isInterceptChildViewMoveTouch;
    }

    public void setInterceptChildViewMoveTouch(boolean interceptChildViewMoveTouch) {
        isInterceptChildViewMoveTouch = interceptChildViewMoveTouch;
    }

    private boolean isCanFirstScroll=false;

    public boolean isCanFirstScroll() {
        return isCanFirstScroll;
    }

    public void setCanFirstScroll(boolean canFirstScroll) {
        isCanFirstScroll = canFirstScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (isCanScroll) {
            if(isInterceptChildViewMoveTouch) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastX1 = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lastX2 = (int) event.getRawY();
                        int dy = Math.abs(lastX2 - lastX1);
                        if (dy > 0) return true;
                        break;
                }
            }
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }


}
