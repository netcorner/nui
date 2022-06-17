package com.ingwill.listener;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.view.View;

public class NavigationFinishClickListener implements View.OnClickListener {

    private final Activity activity;

    public NavigationFinishClickListener(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        ActivityCompat.finishAfterTransition(activity);
    }

}
