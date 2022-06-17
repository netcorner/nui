package com.ingwill.widget.viewpager.effect;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class InRightUpTransformer implements ViewPager.PageTransformer {

	@Override
	public void transformPage(View view, float position) {
		int pageHeight = view.getHeight();
		if (position < -1) {
			view.setAlpha(1);
			view.setTranslationY(0);
		} else if (position <= 0) {
			view.setTranslationY(pageHeight * -position);
			view.setAlpha(1 + position);

			// Android 3.1���°汾�������淽����
			// ViewHelper.setTranslationY(view, pageHeight * -position);
			// ViewHelper.setAlpha(view, 1 + position);
		} else if (position <= 1) {
			view.setTranslationY(view.getHeight() * -position);
			view.setAlpha(1 - position);

			// Android 3.1���°汾�������淽����
			// ViewHelper.setTranslationY(view, pageHeight * -position);
			// ViewHelper.setAlpha(view, 1 - position);
		} else {
			view.setTranslationY(0);
			view.setAlpha(1);
		}
	}

}
