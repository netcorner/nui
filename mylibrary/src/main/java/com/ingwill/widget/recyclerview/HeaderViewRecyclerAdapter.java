package com.ingwill.widget.recyclerview;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by shijiufeng on 2019/4/17.
 */
public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter {
    private final int HEADER = 1;
    private final int FOOTER = 2;
    private ArrayList<View> mHeadView;
    private ArrayList<View> mFootView;
    private RecyclerView.Adapter mAdapter;

    public HeaderViewRecyclerAdapter(ArrayList<View> HeadView, ArrayList<View> FootView, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        if (HeadView == null) {
            //为了防止空指针异常
            mHeadView = new ArrayList<>();
        } else {
            mHeadView = HeadView;
        }

        if (FootView == null) {
            mFootView = new ArrayList<>();
        } else {
            mFootView = FootView;
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //头部修改newHeadViewHolder的方法，mFooterView，有几个头，就需要几个headViewHolder
        if (viewType == HEADER) {
            return new HeadViewHolder(mHeadView.get(0));
        } else if (viewType == FOOTER) {
            //脚部,修改newHeadViewHolder的方法，mFooterView，有多个
            return new FootViewHolder(mFootView.get(0));
        }
        //body部分，暴露出去操作
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    //判断view的类型，头，身体，脚
    @Override
    public int getItemViewType(int position) {
        int headcount = getHeadCount();
        //返回头部
        if (position < headcount) {
            //返回头部类型，
            return HEADER;
        }

        //body类型
        final int midPosition = position - headcount;
        int itemCount = 0;
        if (mAdapter != null) {
            itemCount = mAdapter.getItemCount();
            if (midPosition < itemCount) {
                //返回type不要写死了，body的类型可能不一致
                return mAdapter.getItemViewType(midPosition);
            }
        }

        //Footer类型
        return FOOTER;

    }


    /**
     * 和数据绑定，这里只做body部分的绑定
     * 头和脚的数据绑定逻辑都是在外部操作的
     * 传入前都已经绑定好l
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headcount = getHeadCount();
        if (position < headcount) {
            //头部数据绑定，外部传入前已经操作，这里不再操作
            return;
        }

        //body数据绑定
        final int midPosition = position - headcount;
        int itemcount = 0;
        if (mAdapter != null) {
            itemcount = mAdapter.getItemCount();
            if (midPosition < itemcount) {
                //暴露出去自由操作,传入的是调整后的位置，而不是算上头角的位置
//                mAdapter.onBindViewHolder(holder, position);
                mAdapter.onBindViewHolder(holder, midPosition);
                return;
            }
        }
        //脚部数据绑定，和头一样，啥也不用操作

    }

    @Override
    public int getItemCount() {
        //身体部分不为空
        if (mAdapter != null) {
            return getHeadCount() + getFootCount() + mAdapter.getItemCount();
        } else {
            //只有头和脚的情况下
            return getHeadCount() + getFootCount();
        }
    }


    private int getFootCount() {
        return mFootView.size();
    }

    public int getHeadCount() {
        return mHeadView.size();
    }

    /**
     * 面两个head viewholder 没什么卵用
     * viewholder是为了相同的item减少findviewbyid的时间
     * 头部holder和尾部holder没有共性的findviewbyid
     * 为了拓展方便只得创建，但是不会用到
     */

    class HeadViewHolder extends RecyclerView.ViewHolder {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }

}