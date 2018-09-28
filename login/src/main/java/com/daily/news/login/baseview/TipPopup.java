package com.daily.news.login.baseview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.daily.news.login.R;

/**
 * Date: 2018/9/18 下午4:10
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 气泡提示,显示在某个view上方
 */
public class TipPopup extends PopupWindow {

    private int popupWidth;
    private int popupHeight;


    public TipPopup(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.module_login_pop, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 设置背景透明
        setFocusable(false);
        setTouchable(false);
        setOutsideTouchable(false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = view.getMeasuredWidth();
        popupHeight = view.getMeasuredHeight();
//        System.out.println("width: " + popupWidth + "  height: " + popupHeight);
    }

    /**
     * 在view上方显示
     * @param view
     */
    public void showAboveView(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (!isShowing()) {
//            System.out.println("xxx:  " + location[0] + "  yyy: " + location[1]);
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWidth / 2, location[1] - popupHeight);
        }
    }
}
