package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.zjrb.core.base.BaseActivity;
import com.zjrb.core.utils.AppManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.listener.ZbResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.biz.core.model.SkipScoreInterface;
import cn.daily.news.biz.core.nav.Nav;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;
import cn.daily.news.biz.core.utils.LoadingDialogUtils;
import cn.daily.news.biz.core.utils.RouteManager;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 重置密码输入新密码界面
 */
public class ZBResetNewPassWordActivity extends BaseActivity implements SkipScoreInterface {

//    @BindView(R2.id.tv_tip)
//    TextView tvTip;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.iv_see)
    ImageView ivSee;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;

    private boolean isClick = false;
    String phoneNum;
    String sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_reset_password_comfirm);
        ButterKnife.bind(this);
        initView();
        getIntentData(getIntent());
    }

    /**
     * @param intent 获取传递到重置密码页面的数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            phoneNum = intent.getStringExtra("phoneNum");
            sms = intent.getStringExtra("sms");
        }

    }

    /**
     * 设置密码重置文案初始化
     */
    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(etPasswordText, true);
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        btConfirm.setText(getString(R.string.zb_confirm));
//        tvTip.setText(getString(R.string.zb_set_password_tip));
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return BIZTopBarFactory.createDefaultForLogin(view, this).getView();
    }

    @OnClick({R2.id.bt_confirm, R2.id.iv_see})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        if (view.getId() == R.id.bt_confirm) {
            findPassword();
        } else {
            int length = etPasswordText.getText().toString().length();
            if (!isClick) {
                //开启
                ivSee.getDrawable().setLevel(getResources().getInteger(R.integer
                        .level_password_see));
                etPasswordText.setTransformationMethod(HideReturnsTransformationMethod
                        .getInstance());
                etPasswordText.setSelection(length);
                isClick = true;
            } else {
                //隐藏
                ivSee.getDrawable().setLevel(getResources().getInteger(R.integer
                        .level_password_unsee));
                etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etPasswordText.setSelection(length);
                isClick = false;
            }
        }
    }

    /**
     * 重置密码
     */
    private void findPassword() {
        if (etPasswordText.getText().length() < 6) {
            T.showShortNow(ZBResetNewPassWordActivity.this, "密码长度不可小于6位");
            return;
        }
        LoadingDialogUtils.newInstance().getLoginingDialog("正在重置");
        ZbPassport.resetPassword(phoneNum, sms, etPasswordText.getText().toString(), new ZbResultListener() {
            @Override
            public void onSuccess() {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                // 跳转到账号密码登录页面,手机号自动填充,密码清空
                finish();
                // 关闭 密码登录页面
                AppManager.get().finishActivity(ZBResetPasswordActivity.class);
                AppManager.get().finishActivity(ZBPasswordLoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mobile", phoneNum);
                Nav.with(ZBResetNewPassWordActivity.this).setExtras(bundle).toPath(RouteManager.ZB_PASSWORD_LOGIN);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errorMessage);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LoadingDialogUtils.newInstance().dismissLoadingDialog(true); // 解决android.view.WindowLeaked: Activity com.daily.news.login.zbtxz.ZBResetNewPassWordActivity has leaked window DecorView@f9fced3[] that was originally added here
    }

}
