package com.ingwill.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by shijiufeng on 2017/11/15.
 */

public class SquareDecoratorViewPager extends DecoratorViewPager {
    public SquareDecoratorViewPager(Context context) {
        super(context);
    }

    public SquareDecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
