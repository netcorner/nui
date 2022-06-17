package com.ingwill.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingwill.mylibrary.R;
import com.ingwill.utils.AndroidUtils;

import java.util.List;
import java.util.Map;

public class DropMenuAdapter extends ListAdapter<Map> {

    final class ViewHolder {
        TextView title1;
    }
    private String keyName;
    public DropMenuAdapter(List<Map> list, String keyName, Activity context) {
        super(list,context);
        this.keyName=keyName;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        final Map mContent = sourceDateList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.common_spinner_item, null);
            viewHolder.title1 = (TextView) view.findViewById(R.id.title1);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.title1.getLayoutParams().height= AndroidUtils.dip2px(context,45);
        viewHolder.title1.setText(mContent.get(keyName)+"");
        return view;
    }
}
