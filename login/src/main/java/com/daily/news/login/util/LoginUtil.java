package com.daily.news.login.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.daily.news.login.R;

/**
 * Date: 2019/3/26 11:17 AM
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: TODO
 */
public class LoginUtil {

    public static CountDownTimer startCountDownTimer(final Context context, final TextView textView, int count) {
        textView.setEnabled(false);
        //倒计时
        int c = count + 1;
        return new CountDownTimer(c * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long value = millisUntilFinished / 1000;
                textView.setTextColor(context.getResources().getColor(R.color._bfbfbf));
                textView.setText(value + context.getString(R.string
                        .zb_login_get_validationcode_again));
            }

            @Override
            public void onFinish() {
                textView.setEnabled(true);
                textView.setTextColor(context.getResources().getColor(R.color._d12324));
                textView.setText(context.getString(R.string
                        .zb_login_resend));
            }
        }.start();
    }

}
