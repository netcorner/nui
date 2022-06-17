package com.ingwill.widget.viewpager.effect;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.nineoldandroids.view.ViewHelper;

public class RotateTransformer implements ViewPager.PageTransformer {

	/**
	 * page��Ȼֵ�þ��ǻ����е��Ǹ�view��position������float������ƽʱ����intλ����Ϣ�����ǵ�ǰ����״̬��һ����ʾ��
	 * ���統��������ȫ��ʱ
	 * ��position��0�������󻬶���ʹ���ұ߸պ���һ����������Ļʱ��position��1�����ǰһҹ����һҳ�������Ļռһ��ʱ
	 * ��ǰһҳ��position��-0.5����һҳ��posiotn��0.5�����Ը��position��ֵ���ǾͿ�������������Ҫ��alpha��x/y��Ϣ��
	 */
	@Override
	public void transformPage(View view, float position) {
		if (position < -1) {
		} else if (position <= 0) {
			ViewHelper.setScaleX(view, 1 + position);
			ViewHelper.setScaleY(view, 1 + position);
			ViewHelper.setRotation(view, 360 * position);
		} else if (position <= 1) {
			ViewHelper.setScaleX(view, 1 - position);
			ViewHelper.setScaleY(view, 1 - position);
			ViewHelper.setRotation(view, 360 * position);
		} else {
		}
	}

}
