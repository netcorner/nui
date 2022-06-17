package com.ingwill.widget.imageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by netcorner on 16/8/6.
 */
public class DoubleClickImageView extends ImageView {
    protected Context mContext;
    public DoubleClickImageView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DoubleClickImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public DoubleClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }
    protected void init(){
        mDetector = new GestureDetector(mContext,
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
                            onTapClickListener.onDoubleClick(DoubleClickImageView.this);
                        }
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if(onTapClickListener!=null) {
                            onTapClickListener.onClick(DoubleClickImageView.this);
                        }
                        return super.onSingleTapConfirmed(e);
                    }

                });
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        // 判断并执行action
        if (mDetector.onTouchEvent(ev)) {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }



    /**
     * 手势处理
     */
    private GestureDetector mDetector ;
    private OnTapClickListener onTapClickListener;
    public void setOnTapClickListener(OnTapClickListener onTapClickListener){
        this.onTapClickListener=onTapClickListener;
    }
    public  interface OnTapClickListener{
        void onDoubleClick(View v);
        void onClick(View v);
    }

}
