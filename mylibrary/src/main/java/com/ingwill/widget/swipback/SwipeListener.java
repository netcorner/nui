package com.ingwill.widget.swipback;

public interface SwipeListener {
        void onScroll(float percent, int px);
        void onEdgeTouch();
        /**
         * Invoke when scroll percent over the threshold for the first ic_fly_gray
         */
        void onScrollToClose();
    }