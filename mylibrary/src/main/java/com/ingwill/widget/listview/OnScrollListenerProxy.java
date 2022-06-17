package com.ingwill.widget.listview;

import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netcorner on 16/12/12.
 */
public class OnScrollListenerProxy implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener scrollListener;
    private List<AbsListView.OnScrollListener> scrollListenerList;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollListener != null) {
            scrollListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollListenerList != null && scrollListenerList.size() > 0) {
            for (AbsListView.OnScrollListener onScrollListener : scrollListenerList) {
                onScrollListener.onScrollStateChanged(view, scrollState);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (scrollListener != null) {
            scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (scrollListenerList != null && scrollListenerList.size() > 0) {
            for (AbsListView.OnScrollListener onScrollListener : scrollListenerList) {
                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    }

    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        scrollListener = listener;
    }

    public void addOnScrollListener(AbsListView.OnScrollListener listener) {
        if (scrollListenerList == null) {
            scrollListenerList = new ArrayList<>();
        }
        scrollListenerList.add(listener);
    }

    public void removeOnScrollListener(AbsListView.OnScrollListener listener) {
        if (scrollListenerList != null) {
            scrollListenerList.remove(listener);
        }
    }

    public void clearOnScrollListeners() {
        if (scrollListenerList != null) {
            scrollListenerList.clear();
        }
    }

}