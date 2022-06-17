package com.ingwill.utils;

import android.os.Handler;

/**
 * Created by netcorner on 16/9/5.
 * 异步多任务提交数据，防止数据提交过快时数据无效的问题
 */
public class SynMultiSubmit {
    private int index=0;
    private Runnable runnable;
    private Handler handler;
    public void submit(final int times,final SynMultiCallback synMultiCallback){
        if(times>0) {
            index = 0;
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    synMultiCallback.run(index);
                    index++;
                    if (index < times) handler.postDelayed(runnable, 500);
                }
            };
            handler.post(runnable);
        }
    }
    public interface SynMultiCallback{
        void run(int index);
    }
}
