package com.ingwill.utils;

import android.graphics.Color;

/**
 * Created by shijiufeng on 2018/3/13.
 */

public class ColorUtils {
    public static int getRGBColor(float process) {
        int alpha =255 - (int) Math.round(process * 255);
        String hex = Integer.toHexString(alpha).toUpperCase();
        if(hex.length()==1) hex="0"+hex;
        return Color.parseColor("#"+hex+"000000");
    }
}
