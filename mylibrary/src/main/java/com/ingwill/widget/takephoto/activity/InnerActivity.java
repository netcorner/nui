package com.ingwill.widget.takephoto.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.ingwill.activity.BaseActivity;
import com.ingwill.mylibrary.R;
import com.ingwill.widget.swipback.SwipeBackHelper;
import com.ingwill.widget.swipback.SwipeBackLayout;

/**
 * Created by netcorner on 15/9/3.
 */
public abstract class InnerActivity extends BaseActivity {


    /**
     * 头部名称
     * @return
     */
    public abstract String getHeaderTitle();

    public  boolean isIconOpt(){
        return false;
    }
    public  boolean optCtx(){
        return false;
    }


    /**
     * 是否需要操作按钮
     * @return
     */
    public String getOptionButton(){
        return null;
    }

    protected SwipeBackLayout layout;
    protected TextView headertitle;


    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(getLayoutId());
        //设置头部标题
        headertitle = (TextView) findViewById(R.id.headertitle);
        headertitle.setText(getHeaderTitle());

        optBtn = (TextView) findViewById(R.id.optBtn);
        optCtx=findViewById(R.id.optCtx);
        if(getOptionButton()!=null){
            optCtx.setVisibility(View.VISIBLE);
            optBtn.setText(getOptionButton());
        }

        if(isIconOpt()){
            optIconCtx =findViewById(R.id.optIconCtx);
            optIconCtx.setVisibility(View.VISIBLE);
            optCtx.setVisibility(View.GONE);
        }

        closeWindow(R.id.back);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    public TextView getOptBtn() {
        return optBtn;
    }
    public View getOptCtx() {
        return optCtx;
    }
    private View optCtx;
    private TextView optBtn;
    protected View optIconCtx;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeWindow( R.anim.slide_right_in, R.anim.slide_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
