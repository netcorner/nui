package com.ingwill.widget.dyviewpager;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by 史久锋
 * email: shijf1984@gmail.com
 */

public class ViewPagerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "ViewPagerLayoutManager";
    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private RecyclerView mRecyclerView;
    private int mDrift;//位移，用来判断移动方向




    public ViewPagerLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init();
    }

    public ViewPagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelperEx();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mPagerSnapHelper.attachToRecyclerView(view);
        this.mRecyclerView = view;
        mRecyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }


    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {

        View viewIdle = mPagerSnapHelper.findSnapView(this);
        if(viewIdle!=null) {
            switch (state) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    int positionIdle = getPosition(viewIdle);
                    if (mOnViewPagerListener != null && getChildCount() == 1) {
                        mOnViewPagerListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1);
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    int positionDrag = getPosition(viewIdle);
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    int positionSettling = getPosition(viewIdle);
                    break;
            }
        }
    }

    /**
     * 布局完成后调用
     * @param state
     */
    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (mOnViewPagerListener != null) mOnViewPagerListener.onLayoutComplete();
    }

    /**
     * 监听竖直方向的相对偏移量
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }


    /**
     * 监听水平方向的相对偏移量
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setOnViewPagerListener(OnViewPagerListener listener){
        this.mOnViewPagerListener = listener;
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (mDrift >= 0){
                if (mOnViewPagerListener != null) mOnViewPagerListener.onPageRelease(true,getPosition(view));
            }else {
                if (mOnViewPagerListener != null) mOnViewPagerListener.onPageRelease(false,getPosition(view));
            }

        }
    };

    class PagerSnapHelperEx extends PagerSnapHelper{
        public boolean onFling(int velocityX, int velocityY) {

            View viewIdle = mPagerSnapHelper.findSnapView(ViewPagerLayoutManager.this);
            int positionIdle = getPosition(viewIdle);
            if(positionIdle==0&&velocityX<-100){
                //Log.d("aaaaaaaaaa 向左滑动",velocityX+","+velocityY);
                if (mOnViewPagerListener != null) mOnViewPagerListener.onFirstSlideLeft();
            }else if(positionIdle == (getItemCount() - 1)&&velocityX>100){
                //Log.d("aaaaaaaaaa 向右滑动",velocityX+","+velocityY);
                if (mOnViewPagerListener != null) mOnViewPagerListener.onLastSlideRight();
            }

            return super.onFling(velocityX,velocityY);
        }
    }
}

