package com.ingwill.widget.listview;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ingwill.mylibrary.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by netcorner on 16/12/12.
 */
public class BaseLoadMoreFooter {
    public static final int STATE_DISABLE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_ENDLESS = 3;
    public static final int STATE_FAILED = 4;

    @IntDef({STATE_DISABLE, STATE_LOADING, STATE_FINISHED, STATE_ENDLESS, STATE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {}

    public interface OnLoadMoreListener {
        void onLoadMore();

    }

    protected View iconLoading,endLayout;

    protected TextView tvText;

    @State
    private int state = STATE_DISABLE;

    private final OnLoadMoreListener loadMoreListener;

    public View footerView;

    public BaseLoadMoreFooter(@NonNull Context context, @NonNull OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
        footerView = LayoutInflater.from(context).inflate(R.layout.footer_load_more, null);
        iconLoading=footerView.findViewById(R.id.icon_loading);
        endLayout=footerView.findViewById(R.id.endLayout);
        tvText=(TextView)footerView.findViewById(R.id.tv_text);
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLoadMore();
            }
        });


    }

    public void checkLoadMore() {
        if (getState() == STATE_ENDLESS || getState() == STATE_FAILED) {
            setState(STATE_LOADING);
            loadMoreListener.onLoadMore();
        }
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        if (this.state != state) {
            this.state = state;
            switch (state) {
                case STATE_DISABLE:
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.GONE);
                    tvText.setClickable(false);
                    endLayout.setVisibility(View.GONE);//暂时去掉底部end显示层
                    break;
                case STATE_LOADING:
                    endLayout.setVisibility(View.GONE);
                    iconLoading.setVisibility(View.VISIBLE);
                    tvText.setVisibility(View.GONE);
                    tvText.setClickable(false);
                    break;
                case STATE_FINISHED:
                    endLayout.setVisibility(View.GONE);
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(R.string.load_more_nomore);
                    tvText.setClickable(false);
                    break;
                case STATE_ENDLESS:
//                    endLayout.setVisibility(View.GONE);
//                    iconLoading.setVisibility(View.GONE);
//                    tvText.setVisibility(View.VISIBLE);
//                    tvText.setText(R.string.load_more_endless);
//                    tvText.setClickable(true);

                    endLayout.setVisibility(View.GONE);
                    iconLoading.setVisibility(View.VISIBLE);
                    tvText.setVisibility(View.GONE);
                    tvText.setText(R.string.load_more_endless);
                    tvText.setClickable(true);
                    break;
                case STATE_FAILED:
                    endLayout.setVisibility(View.GONE);
                    iconLoading.setVisibility(View.GONE);
                    tvText.setVisibility(View.VISIBLE);
                    tvText.setText(R.string.load_more_fail);
                    tvText.setClickable(true);
                    break;
                default:
                    throw new AssertionError("Unknow state.");
            }
        }
    }
}
