package com.daily.news.login.baseview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.daily.news.login.R;
import com.zjrb.core.utils.UIUtils;

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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.module_login_pop, null);
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
        // 设置弹出窗体需要软键盘，
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //再设置模式，和Activity的一样，覆盖。
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 在view上方显示
     * @param view
     */
    public void showAboveView(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (!isShowing()) {
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWidth / 2, location[1] - popupHeight - UIUtils.dip2px(1.0f));
        }
    }
}
