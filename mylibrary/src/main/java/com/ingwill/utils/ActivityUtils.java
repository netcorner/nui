package com.ingwill.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;


/**
 * activity 处理工具集
 * Created by netcorner on 2020/8/14.
 */
public final class ActivityUtils {

    private ActivityUtils() {}

    /**
     * 判断 activity 是否开启着
     * @param activity
     * @return
     */
    public static boolean isAlive(Activity activity) {
        return activity != null && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) && !activity.isFinishing();
    }

    /**
     * 重新开启 activity
     * @param activity
     */
    public static void recreate(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.recreate();
        } else {
            Intent intent = activity.getIntent();
            intent.setClass(activity, activity.getClass());
            activity.startActivity(intent);
            activity.finish();
            activity.overridePendingTransition(0, 0);
        }
    }

    /**
     * 异步性重启 activity
     * @param activity
     */
    public static void recreateDelayed(@NonNull final Activity activity) {
        HandlerUtils.post(new Runnable() {
            @Override
            public void run() {
                recreate(activity);
            }
        });
    }

}
