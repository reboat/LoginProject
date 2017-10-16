package com.daily.news.login.zbtxz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnLoginListener;
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
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙报通行证登录页
 * created by wanglinjie on 2016/11/23
 */

public class ZBLoginActivity extends BaseActivity implements OnCheckAccountExistListener,
        OnLoginListener {
    @BindView(R2.id.dt_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.tv_login)
    TextView tvLogin;
    @BindView(R2.id.tv_verification_btn)
    TextView tvVerification;
    @BindView(R2.id.tv_forget_password_btn)
    TextView tvForgetPassword;
    @BindView(R2.id.verification_code_see_btn)
    ImageView ivSee;

    /**
     * 是否点击了可视密码
     */
    private boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_zbtxz_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
    }

    private void initView() {
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        tvLogin.setText(getString(R.string.zb_login));
        tvVerification.setText(getString(R.string.zb_login_sms));
        tvForgetPassword.setText(getString(R.string.zb_forget_password));
    }

    private Bundle bundle;

    @OnClick({R2.id.dt_account_text, R2.id.tv_login,
            R2.id.tv_forget_password_btn, R2.id.verification_code_see_btn,
            R2.id.tv_verification_btn})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        if (view.getId() == R.id.dt_account_text) {
            dtAccountText.setCursorVisible(true);
            //登录
        } else if (view.getId() == R.id.tv_login) {
            if (dtAccountText.getText().toString().isEmpty()) {
                T.showShort(this, getString(R.string.zb_phone_num_empty));
            } else if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), this);
            } else {
                T.showShort(this, getString(R.string.zb_phone_num_error));
            }
            //重置密码
        } else if (view.getId() == R.id.tv_forget_password_btn) {

            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_SMS_LOGIN);
            //短信验证码登录
        } else if (view.getId() == R.id.tv_verification_btn) {
            AppManager.get().finishActivity(ZBResetPWSmsLogin.class);

            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_SMS_TYPE);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_SMS_LOGIN);

            //密码可视
        } else if (view.getId() == R.id.verification_code_see_btn) {
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
     * 验证账号是否存在及登录
     *
     * @param b true/false
     * @param s phone number
     */
    @Override
    public void onSuccess(boolean b, @NonNull String s) {
        if (b) {
            WoaSdk.login(this, s, etPasswordText.getText().toString(), this);
        } else {
            T.showShort(this, getString(R.string.zb_account_not_exise));
        }

    }

    @Override
    public void onFailure(int i, String s) {
        T.showShort(this, s);
    }

    /**
     * @param s  sessionId
     * @param s1
     * @param s2 登录回调
     */
    @Override
    public void onSuccess(String s, String s1, String s2) {
        loginVerification(s);
    }

    /**
     * @param s 登录验证
     */
    private void loginVerification(String s) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功

                    if (!userBiz.isCertification()) { // 进入实名制页面
                        Nav.with(getActivity()).toPath(RouteManager.ZB_MOBILE_VERIFICATION);
                        // 关闭短信验证码页面（可能不存在）
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        finish();
                    } else { // 登录成功，关闭相关页面
                        // 关闭短信验证码页面（可能不存在）
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        finish();
                        // 关闭登录入口页面
                        AppManager.get().finishActivity(LoginActivity.class);
                    }
                } else {
                    T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
                }
            }
        }).setTag(this).exe(s, "BIANFENG", dtAccountText.getText(), dtAccountText.getText(),
                dtAccountText.getText());
    }

}
