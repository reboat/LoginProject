package com.daily.news.login.baseview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daily.news.login.R;
import com.zjrb.core.utils.UIUtils;

/**
 * 提示重置密码及手机号登录对话框,标题若为多行文字每行居中显示
 *
 * @author sishuqun
 */
public class TipDialog extends Dialog {

    private Context mContext;
    private View view;
    private TextView tv_title;
    private TextView button_cancel;
    private TextView button_ok;
    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onCancel();

        void onOK();
    }

    public TipDialog(Context context) {
        super(context, R.style.confirm_dialog);
        mContext = context;
        initView();
    }

    private void initView() {
        view = LayoutInflater.from(mContext).inflate(
                R.layout.module_core_dialog_tip_layout, null);
        tv_title = (TextView) view.findViewById(R.id.tv_confirm_title);
        button_cancel = (TextView) view.findViewById(R.id.Button_Cancel);
        button_ok = (TextView) view.findViewById(R.id.Button_OK);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOK();
                }
                dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (view != null) {
            setContentView(view);
            configDialog();
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public TipDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置确定按钮文案
     *
     * @param okText
     */
    public TipDialog setOkText(String okText) {
        if (!TextUtils.isEmpty(okText)) {
            button_ok.setText(okText);
        } else {
            button_ok.setText(mContext.getResources().getString(R.string.zb_mobile_ok));
        }
        return this;
    }

    /**
     * 配置对话框
     */
    private void configDialog() {
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        //设置对话框居中
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = UIUtils.getScreenW() * 5 / 6;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        //因为某些机型是虚拟按键的,所以要加上以下设置防止挡住按键.
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public TipDialog setOnConfirmListener(TipDialog.OnConfirmListener listener) {
        mListener = listener;
        return this;
    }
}
