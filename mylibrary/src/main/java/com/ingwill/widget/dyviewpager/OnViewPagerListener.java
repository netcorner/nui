package com.ingwill.widget.dyviewpager;

/**
 * Created by 史久锋
 * email: shijf1984@gmail.com
 * 用于ViewPagerLayoutManager的监听
 */

public interface OnViewPagerListener {

    /*释放的监听*/
    void onPageRelease(boolean isNext, int position);

    /*选中的监听以及判断是否滑动到底部*/
    void onPageSelected(int position, boolean isBottom);

    /*布局完成的监听*/
    void onLayoutComplete();

    //第一个向左滑动
    void onFirstSlideLeft();
    //最后一个向右滑动
    void onLastSlideRight();
}
