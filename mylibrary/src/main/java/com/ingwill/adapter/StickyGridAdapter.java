package com.ingwill.adapter;

import android.app.Activity;

import com.ingwill.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

/**
 * Created by netcorner on 16/12/11.
 */
public abstract class StickyGridAdapter<T>  extends ListAdapter<T> implements
        StickyGridHeadersSimpleAdapter {

    public StickyGridAdapter(List<T> sourceDateList, Activity context) {
        super(sourceDateList,context);
    }

}
