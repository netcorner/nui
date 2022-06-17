package com.ingwill.widget.gridview;

import android.view.View;
import android.widget.GridView;

/**
 * Created by shijiufeng on 2017/7/30.
 * listview 嵌套gridview使用
 */

public class AutoHeightGridView  extends GridView {
    public AutoHeightGridView(android.content.Context context,
                      android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
