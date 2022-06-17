package com.ingwill.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by netcorner on 16/12/11.
 */
public abstract class ListAdapter<T>  extends BaseAdapter {
    protected List<T> sourceDateList;


    protected Activity context;
    public ListAdapter(List<T> sourceDateList, Activity context) {
        this.sourceDateList = sourceDateList;
        this.context=context;
    }



//    /**
//     * 局部更新数据，调用一次getView()方法；Google推荐的做法
//     *
//     * @param listView 要更新的listview
//     * @param position 要更新的位置
//     */
//    public void notifyDataSetChanged(ListView listView, int position) {
//        /**第一个可见的位置**/
//        int firstVisiblePosition = listView.getFirstVisiblePosition();
//        /**最后一个可见的位置**/
//        int lastVisiblePosition = listView.getLastVisiblePosition();
//
//        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
//        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
//            /**获取指定位置view对象**/
//            View view = listView.getChildAt(position - firstVisiblePosition);
//            getView(position, view, listView);
//        }
//    }

    public void  updateListView(List<T> list){
        this.sourceDateList=list;
        notifyDataSetChanged();
    }
    public void  updateListView(int page,List<T> list){
        if(page>1){
            sourceDateList.addAll(list);
        }else{
            sourceDateList=list;
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return sourceDateList.size();
    }

    @Override
    public Object getItem(int i) {
        return sourceDateList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
