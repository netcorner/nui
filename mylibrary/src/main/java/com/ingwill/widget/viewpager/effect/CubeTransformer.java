package com.ingwill.widget.viewpager.effect;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.nineoldandroids.view.ViewHelper;

public class CubeTransformer implements ViewPager.PageTransformer {
	
	/**
	 * position����ָ����ҳ���������Ļ���ĵ�λ�á�����һ����̬���ԣ�������ҳ��Ĺ�����ı䡣��һ��ҳ����������Ļ�ǣ����ֵ��0��
	 * ��һ��ҳ��ո��뿪��Ļ���ұ�ʱ�����ֵ��1��������Ҳҳ��ֱ������һ��ʱ������һ��ҳ���λ����-0.5����һ��ҳ���λ����0.5��������Ļ��ҳ���λ��
	 * ��ͨ��ʹ������setAlpha()��setTranslationX()����setScaleY()����������ҳ������ԣ��������Զ���Ļ���������
	 */
	@Override
	public void transformPage(View view, float position) {
		if (position <= 0) {
			//�������󻬶�Ϊ��ǰView
			
			//������ת���ĵ㣻
			ViewHelper.setPivotX(view, view.getMeasuredWidth());
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			
			//ֻ��Y������ת����
			ViewHelper.setRotationY(view, 90f * position);
		} else if (position <= 1) {
			//�������һ���Ϊ��ǰView
			ViewHelper.setPivotX(view, 0);
			ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
			ViewHelper.setRotationY(view, 90f * position);
		}
	}
}
