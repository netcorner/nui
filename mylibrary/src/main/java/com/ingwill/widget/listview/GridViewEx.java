package com.ingwill.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by netcorner on 16/12/16.
 */
public class GridViewEx extends GridViewWithHeaderAndFooter {
    private final OnScrollListenerProxy scrollListenerProxy = new OnScrollListenerProxy();

    public GridViewEx(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public GridViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public GridViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.setOnScrollListener(scrollListenerProxy);
    }

    @Override
    @Deprecated
    public void setOnScrollListener(OnScrollListener listener) {
        scrollListenerProxy.setOnScrollListener(listener);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        scrollListenerProxy.addOnScrollListener(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        scrollListenerProxy.removeOnScrollListener(listener);
    }

    public void clearOnScrollListeners() {
        scrollListenerProxy.clearOnScrollListeners();
    }



    private float downEventX, downEventY;
    private int scrollMode; // 0初始化 1左右滑动 2上下滑动
    private static final int SCROLL_MODE_IDLE = 0;
    private static final int SCROLL_MODE_HORIZONTAL = 1;
    private static final int SCROLL_MODE_VERTICAL = 2;
    private ListViewEx.OnTapClickListener onTapClickListener;
    public void setOnTapClickListener(ListViewEx.OnTapClickListener onTapClickListener){
        this.onTapClickListener=onTapClickListener;
    }
    public  interface OnTapClickListener{
        void onDownSlide(View v);//列表下滑 关闭窗口用到
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(onTapClickListener!=null) {
            if(isListViewReachTopEdge()) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scrollMode = SCROLL_MODE_IDLE;
                        downEventX = ev.getRawX();
                        downEventY = ev.getRawY();
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (scrollMode == SCROLL_MODE_HORIZONTAL) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                            // 已被定性为左右滑动，只在这个view内部处理即可
                        } else if (scrollMode == SCROLL_MODE_VERTICAL) {
                            // 已被定性为上下滑动，此view可以不必处理，不消费此touch事件
                            //return false;
                            return setDisallowInterceptTouchEventOther(ev);
                        } else if (scrollMode == SCROLL_MODE_IDLE) {
                            float distanceX = Math.abs(ev.getRawX() - downEventX);
                            float distanceY = Math.abs(ev.getRawY() - downEventY);
                            if (distanceX > distanceY && distanceX > 5) {
                                scrollMode = SCROLL_MODE_HORIZONTAL;
                                getParent().requestDisallowInterceptTouchEvent(false);
                            } else if (distanceY > distanceX && distanceY > 5) {
                                scrollMode = SCROLL_MODE_VERTICAL;
                                //return false;
                                return setDisallowInterceptTouchEventOther(ev);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollMode = SCROLL_MODE_IDLE;
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        scrollMode = SCROLL_MODE_IDLE;
                        break;
                    default:
                        break;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    private boolean setDisallowInterceptTouchEventOther(MotionEvent ev){
        if(onTapClickListener!=null) {
            float distanceY = ev.getRawY() - downEventY;
            if (distanceY > 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                onTapClickListener.onDownSlide(this);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            return super.dispatchTouchEvent(ev);
        }else{
            return false;
        }
    }

    public boolean isListViewReachTopEdge() {
        boolean result=false;
        if(getFirstVisiblePosition()==0){
            final View topChildView = getChildAt(0);
            result=topChildView.getTop()==0;
        }
        return result ;
    }
}
