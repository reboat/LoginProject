package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.SessionIdBean;
import com.daily.news.login.eventbus.CloseZBLoginEvent;
import com.daily.news.login.eventbus.ZBCloseActEvent;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.InitTask;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.domain.eventbus.EventBase;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙报通行证登录页
 * created by wanglinjie on 2016/11/23
 */

public class ZBLoginActivity extends BaseActivity implements OnCheckAccountExistListener, OnLoginListener {
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

    /**
     * 请求码
     */
    private int REQUEST_CODE = 0x1;

    @NonNull
    private UmengAuthUtils mUmengUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_zbtxz_login);
        ButterKnife.bind(this);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login)).getView();
    }

    private void initView() {
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        tvLogin.setText(getString(R.string.zb_login));
        tvVerification.setText(getString(R.string.zb_login_sms));
        tvForgetPassword.setText(getString(R.string.zb_forget_password));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(EventBase event) {
        if (event instanceof CloseZBLoginEvent) {
            EventBus.getDefault().removeStickyEvent(event);
            isLoginSuccess = true;
            finish();
        } else if (event instanceof ZBCloseActEvent) {
            EventBus.getDefault().removeStickyEvent(event);
            isLoginSuccess = true;
            finish();
        }

    }

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
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBResetPWSmsLogin")
                    .buildUpon()
                    .appendQueryParameter(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE)
                    .build(), REQUEST_CODE);
            //短信验证码登录
        } else if (view.getId() == R.id.tv_verification_btn) {
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBResetPWSmsLogin")
                    .buildUpon()
                    .appendQueryParameter(Key.LOGIN_TYPE, Key.Value.LOGIN_SMS_TYPE)
                    .build(), REQUEST_CODE);
            //密码可视
        } else if (view.getId() == R.id.verification_code_see_btn) {
            int length = etPasswordText.getText().toString().length();
            if (!isClick) {
                //开启
                ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_see));
                etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etPasswordText.setSelection(length);
                isClick = true;
            } else {
                //隐藏
                ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
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
        initTest(s);
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
            public void onSuccess(@NonNull ZBLoginBean bean) {
                UserBiz userBiz = UserBiz.get();
                userBiz.setZBLoginBean(bean);
                //进入实名制页面
                Nav.with(ZBLoginActivity.this).to(Uri.parse("http://www.8531.cn/login/ZBMobileValidateActivity")
                        .buildUpon()
                        .build(), REQUEST_CODE);
            }
        }).setTag(this).exe(s, "BIANFENG", dtAccountText.getText(), dtAccountText.getText(), dtAccountText.getText());
    }

    /**
     * 测试获取sessionId
     *
     * @param s 边锋sessionId
     *          获取sessionId
     */
    //TODO WLJ  单独测试登录模块使用
    private void initTest(final String s) {
        new InitTask(new APIExpandCallBack<SessionIdBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
            }

            @Override
            public void onSuccess(@NonNull SessionIdBean result) {
                UserBiz.get().setSessionId(result.getSession().getId());
                loginVerification(s);

            }
        }).setTag(this).exe();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data        第三方登录回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean isLoginSuccess = false;

    @Override
    public void finish() {
        if (isLoginSuccess) {
            UserBiz.get().loginSuccess();
        } else {
            UserBiz.get().loginCancel();
        }
        if (AppManager.get().getCount() > 1) {
            super.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (AppManager.get()
                .getCount() > 1 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && !ClickTracker.isDoubleClick()) {
            isLoginSuccess = false;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
