package com.ingwill.widget.listview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by shijiufeng on 2019/5/21.
 */

public class InnerListview extends ListViewEx {
    public InnerListview(Context context) {
        super(context);
    }

    public InnerListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InnerListview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

         @Override
     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(
                        Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
      super.onMeasure(widthMeasureSpec, heightMeasureSpec1);
     }


}
