package com.tk.wechatalbum.utils;

import android.content.Context;

public class DensityUtil {

    public final static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}