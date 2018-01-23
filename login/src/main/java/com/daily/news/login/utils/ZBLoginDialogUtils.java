package com.daily.news.login.utils;

import android.app.Activity;

import com.zjrb.core.ui.widget.dialog.LoadingIndicatorDialog;
import com.zjrb.core.utils.UIUtils;

/**
 * Created by wanglinjie.
 * create time:2018/1/22  下午5:28
 */

public class ZBLoginDialogUtils {

    private LoadingIndicatorDialog loginDialog;
    private static ZBLoginDialogUtils mDialog;

    private ZBLoginDialogUtils() {
    }

    public static ZBLoginDialogUtils newInstance() {
        if (mDialog == null) {
            synchronized (ZBLoginDialogUtils.class) {
                if (mDialog == null) {
                    mDialog = new ZBLoginDialogUtils();
                }
            }
        }
        return mDialog;

    }


    /**
     * 登录加载框
     */
    public ZBLoginDialogUtils getLoginingDialog(String s) {
        Activity activity = UIUtils.getActivity();
        loginDialog = new LoadingIndicatorDialog(activity);
        if (!activity.isDestroyed()) {
            loginDialog.show();
            loginDialog.setToastText(s);
        }
        return this;
    }

    /**
     * 关闭dialog
     */
    public ZBLoginDialogUtils dismissLoadingDialog(boolean isSuccess) {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.finish(isSuccess);
        }
        return this;
    }

    /**
     * 关闭dialog,no text
     */
    public ZBLoginDialogUtils dismissLoadingDialogNoText() {
        if (loginDialog != null && loginDialog.isShowing()) {
            loginDialog.finish();
        }
        return this;
    }
}
