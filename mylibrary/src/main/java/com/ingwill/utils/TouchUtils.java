package com.ingwill.utils;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by netcorner on 15/11/13.
 * 触碰效果处理工具集
 */
public class TouchUtils {

        /**
         * 给试图添加点击效果,让背景变深
         * */
        public static void addTouchDark(View view ){
            view.setOnTouchListener( VIEW_TOUCH_DARK ) ;
        }

        /**
         * 给试图添加点击效果,让背景变暗
         * */
        public static void addTouchLight(View view ){
            view.setOnTouchListener( VIEW_TOUCH_LIGHT ) ;
        }


        /**
         * 让控件点击时，颜色变深
         * */
        public static final View.OnTouchListener VIEW_TOUCH_DARK = new View.OnTouchListener() {

            public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1,
                    0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
            public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(v instanceof ImageView){
                        ImageView iv = (ImageView) v;
                        iv.setColorFilter( new ColorMatrixColorFilter(BT_SELECTED) ) ;
                    }else{
                        v.getBackground().setColorFilter( new ColorMatrixColorFilter(BT_SELECTED) );
                        v.setBackgroundDrawable(v.getBackground());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(v instanceof ImageView){
                        ImageView iv = (ImageView) v;
                        iv.setColorFilter( new ColorMatrixColorFilter(BT_NOT_SELECTED) ) ;
                    }else{
                        v.getBackground().setColorFilter(
                                new ColorMatrixColorFilter(BT_NOT_SELECTED));
                        v.setBackgroundDrawable(v.getBackground());
                    }
                }
                return false;
            }
        };

        /**
         * 让控件点击时，颜色变暗
         * */
        public static final View.OnTouchListener VIEW_TOUCH_LIGHT = new View.OnTouchListener(){

            public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 50, 0, 1,
                    0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
            public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(v instanceof ImageView){
                        ImageView iv = (ImageView) v;
                        iv.setDrawingCacheEnabled(true);

                        iv.setColorFilter( new ColorMatrixColorFilter(BT_SELECTED) ) ;
                    }else{
                        v.getBackground().setColorFilter( new ColorMatrixColorFilter(BT_SELECTED) );
                        v.setBackgroundDrawable(v.getBackground());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(v instanceof ImageView){
                        ImageView iv = (ImageView) v;
                        iv.setColorFilter( new ColorMatrixColorFilter(BT_NOT_SELECTED) ) ;
                        //System.out.println( "变回来" );
                    }else{
                        v.getBackground().setColorFilter(
                                new ColorMatrixColorFilter(BT_NOT_SELECTED));
                        v.setBackgroundDrawable(v.getBackground());
                    }
                }
                return false;
            }
        };
    }
