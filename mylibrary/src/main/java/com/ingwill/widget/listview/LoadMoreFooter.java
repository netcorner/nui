package com.ingwill.widget.listview;

import android.content.Context;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class LoadMoreFooter extends BaseLoadMoreFooter {


    public LoadMoreFooter(@NonNull Context context, @NonNull ListViewEx listView, @NonNull OnLoadMoreListener loadMoreListener) {
        super(context,loadMoreListener);
        listView.addFooterView(footerView, null, false);
        listView.addOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    checkLoadMore();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {



            }

        });
    }

    public LoadMoreFooter(@NonNull Context context, @NonNull GridViewEx listView, @NonNull OnLoadMoreListener loadMoreListener) {
        super(context,loadMoreListener);
        listView.addFooterView(footerView, null, false);
        listView.addOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    checkLoadMore();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

        });
    }

    public LoadMoreFooter(@NonNull Context context, @NonNull RecyclerView listView, @NonNull OnLoadMoreListener loadMoreListener) {
        super(context,loadMoreListener);
        //listView.addView(footerView);


        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastItem = 0;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
                    gridLayoutManager.findLastVisibleItemPosition();
                    lastItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == -1) lastItem = gridLayoutManager.findLastVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
                    lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == -1) lastItem = linearLayoutManager.findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = ((StaggeredGridLayoutManager) layoutManager);
                    // since may lead to the final item has ic_more_white than one StaggeredGridLayoutManager the particularity of the so here that is an array
                    // this array into an array of position and then take the maximum value that is the last show the position value
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastPositions);
                    lastItem = findMax(lastPositions);
                }
                if(lastItem == totalItemCount - 1 && (dx > 0 || dy > 0)){
                    checkLoadMore();
                }
            }

            //To find the maximum value in the array
            private int findMax(int[] lastPositions) {
                int max = lastPositions[0];
                for (int value : lastPositions) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }

        });
    }
}
