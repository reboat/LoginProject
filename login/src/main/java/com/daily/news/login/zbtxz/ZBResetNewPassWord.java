package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.OnResetPasswordListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.LoginActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置密码页面
 * <p>
 * Created by wanglinjie.
 * create time:2017/8/11  下午4:56
 */
public class ZBResetNewPassWord extends BaseActivity {

    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.tv_tip)
    TextView tvTip;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.iv_see)
    ImageView ivSee;
    @BindView(R2.id.fy_password)
    FrameLayout fyPassword;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;

    public String mUuid = "";
    public String mAccountID = "";
    private boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_reset_password_confirm);
        ButterKnife.bind(this);
        initView();
        getIntentData(getIntent());
    }

    /**
     * @param intent 获取传递到重置密码页面的数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            mUuid = data.getQueryParameter(Key.UUID);
            mAccountID = data.getQueryParameter(Key.ACCOUNTID);
        }
    }

    /**
     * 设置密码重置文案初始化
     */
    private void initView() {
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        btConfirm.setText(getString(R.string.zb_confirm));
        tvTip.setText(getString(R.string.zb_set_password_tip));
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
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
        WoaSdk.resetPassword(this,
                mAccountID,
                mUuid,
                etPasswordText.getText().toString(),
                new OnResetPasswordListener() {

                    @Override
                    public void onFailure(int i, String s) {
                        T.showShort(ZBResetNewPassWord.this, s);
                    }

                    @Override
                    public void onSuccess() {
                        WoaSdk.login(ZBResetNewPassWord.this,
                                mAccountID,
                                etPasswordText.getText().toString(),
                                new OnLoginListener() {

                                    @Override
                                    public void onFailure(int i, String s) {
                                        T.showShort(ZBResetNewPassWord.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s, String s1, String s2) {
                                        regGetToken(s);
                                    }
                                });
                    }
                });
    }

    /**
     * 重置密码后需要进行登录
     */
    private void regGetToken(String sessionId) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBResetNewPassWord.this, errMsg);
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功

                    if (!userBiz.isCertification()) { // 进入实名制页面
                        Nav.with(getActivity()).toPath("/login/ZBMobileValidateActivity");
                        // 关闭 验证码页面／短信验证码登录页
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);

                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                    } else {
                        // 关闭 验证码页面／短信验证码登录页
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);
                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                        // 关闭登录入口页
                        AppManager.get().finishActivity(LoginActivity.class);
                    }
                } else {
                    T.showShortNow(ZBResetNewPassWord.this, "密码重置失败");
                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", mAccountID, mAccountID, mAccountID);

    }
}
