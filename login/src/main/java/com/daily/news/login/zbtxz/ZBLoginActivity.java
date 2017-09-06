package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.SessionIdBean;
import com.daily.news.login.bean.ZBLoginBean;
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
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.domain.eventbus.EventBase;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.ui.widget.DeleteEditText;
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

public class ZBLoginActivity extends BaseActivity implements TextWatcher, OnCheckAccountExistListener, OnLoginListener {
    @BindView(R2.id.dt_account_text)
    DeleteEditText dtAccountText;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.bt_login)
    Button btLogin;
    @BindView(R2.id.tv_verification_btn)
    TextView tvVerification;
    @BindView(R2.id.tv_forget_password_btn)
    TextView tvForgetPassword;
    @BindView(R2.id.verification_code_see_btn)
    ImageView ivSee;
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;

    /**
     * 是否点击了可视密码
     */
    private boolean isClick = false;

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
        ivLogo.setBackgroundResource(R.mipmap.module_login_day_zbtxz);
//        if (!ThemeMode.isNightMode()) {
//            ivSee.setBackgroundResource(R.mipmap.module_login_day_password_unsee);
//        } else {
//            ivSee.setBackgroundResource(R.mipmap.module_login_night_password_unsee);
//        }
        etPasswordText.addTextChangedListener(this);
        btLogin.setText(getString(R.string.zb_login));
        btLogin.setEnabled(false);
        btLogin.setBackgroundResource(R.drawable.border_zblogin_btn_bg);
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

    @OnClick({R2.id.dt_account_text, R2.id.bt_login,
            R2.id.tv_forget_password_btn, R2.id.verification_code_see_btn,
            R2.id.tv_verification_btn})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        if (view.getId() == R.id.dt_account_text) {
            dtAccountText.setCursorVisible(true);
            //登录
        } else if (view.getId() == R.id.bt_login) {
            if (!ClickTracker.isDoubleClick()) {
                WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), this);
            }
            //重置密码
        } else if (view.getId() == R.id.tv_forget_password_btn) {
            if (!ClickTracker.isDoubleClick()) {
                Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBResetPWSmsLogin")
                        .buildUpon()
                        .appendQueryParameter(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE)
                        .build(), 0);
            }
            //短信验证码登录
        } else if (view.getId() == R.id.tv_verification_btn) {
            if (!ClickTracker.isDoubleClick()) {
                Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBResetPWSmsLogin")
                        .buildUpon()
                        .appendQueryParameter(Key.LOGIN_TYPE, Key.Value.LOGIN_SMS_TYPE)
                        .build(), 0);
            }
            //密码可视
        } else if (view.getId() == R.id.verification_code_see_btn) {
            int length = etPasswordText.getText().toString().length();
            if (length > 0) {
                if (!isClick) {
                    //开启
//                    if (!ThemeMode.isNightMode()) {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_day_password_see);
//                    } else {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_night_password_see);
//                    }
                    etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPasswordText.setSelection(length);
                    isClick = true;
                } else {
                    if (!ThemeMode.isNightMode()) {
                        ivSee.setBackgroundResource(R.mipmap.module_login_day_password_unsee);
                    } else {
                        ivSee.setBackgroundResource(R.mipmap.module_login_night_password_unsee);
                    }
                    //隐藏
                    etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPasswordText.setSelection(length);
                    isClick = false;
                }
            } else {
                T.showShort(this, getString(R.string.zb_passwprd_readable));
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
     * @param s
     * 登录验证
     */
    private void  loginVerification(String s){
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
            }

            @Override
            public void onSuccess(@NonNull ZBLoginBean result) {
                if (result.getResultCode() == 0) {
//                    UserBiz userBiz = UserBiz.get();
//                    userBiz.setAvatar("");
//                    userBiz.setNickName(accountText.getText().toString());
//                    userBiz.setUserId(result.getUserId());
//                    userBiz.setSessionId(result.getSessionId());
//
//                    new GetCidTask(null).setTag(ZBLoginActivity.this).exe(UserBiz.get().getClientID());
//                    if (AppManager.get().getCount() < 1) {
//                        startActivity(new Intent(UIUtils.getActivity(), MainActivity.class));
//                    }
//                    isLoginSuccess = true;
//                    finish();
                } else {
                    T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
                }

            }
        }).setTag(this).exe(s, "BIANFENG", dtAccountText.getText(), dtAccountText.getText(), dtAccountText.getText());
    }

    /**
     * 测试获取sessionId
     * @param s 边锋sessionId
     * 获取sessionId
     */
    private void initTest(final String s){
        new InitTask(new APIExpandCallBack<SessionIdBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
            }

            @Override
            public void onSuccess(@NonNull SessionIdBean result) {
                if (result.getResultCode() == 0) {
                    UserBiz.get().setSessionId(result.getSession().getId());
                    loginVerification(s);
                } else {
                    T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
                }

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
        //微信不走这里
        if (mUmengUtils != null) {
            mUmengUtils.getDialog();
            mUmengUtils.onResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (etPasswordText != null && etPasswordText.getText().toString().length() < 6) {
            btLogin.setEnabled(false);
            btLogin.setBackgroundResource(R.drawable.border_zblogin_btn_bg);
        } else {
            btLogin.setEnabled(true);
            btLogin.setBackgroundResource(R.drawable.border_login_zb_password_bg);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            if (etPasswordText.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isClick = false;
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
