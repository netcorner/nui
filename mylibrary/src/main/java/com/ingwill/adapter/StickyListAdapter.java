package com.ingwill.adapter;

import android.app.Activity;

import com.ingwill.widget.stickylistheader.StickyListHeadersAdapter;

import java.util.List;

/**
 * Created by netcorner on 16/12/11.
 */
public abstract class StickyListAdapter<T>  extends ListAdapter<T> implements
        StickyListHeadersAdapter {

    public StickyListAdapter(List<T> sourceDateList, Activity context) {
        super(sourceDateList,context);
    }

}
