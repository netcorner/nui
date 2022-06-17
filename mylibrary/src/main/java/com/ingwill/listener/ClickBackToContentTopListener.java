package com.ingwill.listener;

import androidx.annotation.NonNull;
import android.view.View;


public class ClickBackToContentTopListener implements View.OnClickListener {

    private final IBackToContentTopView backToContentTopView;

    public ClickBackToContentTopListener(@NonNull IBackToContentTopView backToContentTopView) {
        this.backToContentTopView = backToContentTopView;
    }


    @Override
    public void onClick(View view) {
        backToContentTopView.backToContentTop();
    }
}
