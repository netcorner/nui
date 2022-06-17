package com.ingwill.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.ingwill.widget.imageview.DoubleClickImageView;

/**
 * Created by shijiufeng on 2018/3/12.
 */

public class DoubleClickLinearLayout extends LinearLayout {
    public DoubleClickLinearLayout(Context context) {
        super(context);
    }

    public DoubleClickLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleClickLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DoubleClickLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 判断并执行action
        if (mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 手势处理
     */
    private GestureDetector mDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2,
                                       float velocityX, float velocityY) {
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if(onTapClickListener!=null) {
                        onTapClickListener.onDoubleClick(DoubleClickLinearLayout.this);
                    }
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if(onTapClickListener!=null) {
                        onTapClickListener.onClick(DoubleClickLinearLayout.this);
                    }
                    return super.onSingleTapConfirmed(e);
                }

            });
    private DoubleClickImageView.OnTapClickListener onTapClickListener;
    public void setOnTapClickListener(DoubleClickImageView.OnTapClickListener onTapClickListener){
        this.onTapClickListener=onTapClickListener;
    }
    public  interface OnTapClickListener{
        void onDoubleClick(View v);
        void onClick(View v);
    }
}
