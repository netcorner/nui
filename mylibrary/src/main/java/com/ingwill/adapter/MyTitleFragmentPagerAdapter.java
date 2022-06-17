package com.ingwill.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by netcorner on 17/3/22.
 */

public class MyTitleFragmentPagerAdapter extends MyFragmentPagerAdapter {
    private String[] titles;
    public MyTitleFragmentPagerAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
        super(fm, list);
        this.titles=titles;
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void notifyDataSetChanged(List<Fragment> list){
        this.list=list;
        super.notifyDataSetChanged();
    }
}
