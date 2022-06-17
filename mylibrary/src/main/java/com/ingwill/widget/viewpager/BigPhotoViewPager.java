package com.ingwill.widget.viewpager;

/**
 * Created by netcorner on 16/11/25.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 解决  photoview 与viewpager 组合时 图片缩放的错误 ；异常：.IllegalArgumentException: pointerIndex out of range
 */
public class BigPhotoViewPager extends DecoratorViewPager {


    public BigPhotoViewPager(Context context) {
        super(context);
        setCanScroll(true);
    }

    public BigPhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCanScroll(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

}
