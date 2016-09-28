package com.tk.wechatalbum.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by TK on 2016/9/28.
 */

public class EnableLayout extends LinearLayout {
    private boolean showShadow;

    public EnableLayout(Context context) {
        super(context);
    }

    public EnableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if(!showShadow){
//
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }

//
//    public void setEnable(boolean enable) {
//        this.enable = enable;
//    }
}
