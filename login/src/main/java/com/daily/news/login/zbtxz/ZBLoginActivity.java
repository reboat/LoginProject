package com.daily.news.login.zbtxz;

import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.ZBLoginBean;
import com.daily.news.login.eventbus.CloseZBLoginEvent;
import com.daily.news.login.eventbus.ZBCloseActEvent;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.coreprojectlibrary.api.callback.APIExpandCallBack;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.common.base.toolbar.TopBarFactory;
import com.zjrb.coreprojectlibrary.common.biz.UserBiz;
import com.zjrb.coreprojectlibrary.common.manager.AppManager;
import com.zjrb.coreprojectlibrary.domain.eventbus.EventBase;
import com.zjrb.coreprojectlibrary.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.coreprojectlibrary.ui.widget.DeleteEditText;
import com.zjrb.coreprojectlibrary.utils.T;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

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

@Route(path = "/module/login/ZBLoginActivity")
public class ZBLoginActivity extends BaseActivity implements TextWatcher, OnCheckAccountExistListener, OnLoginListener {
    @BindView(R2.id.tv_verification_btn)
    TextView tvVerificationBtn;
    @BindView(R2.id.tv_forget_password_btn)
    TextView tvForgetPasswordBtn;
    @BindView(R2.id.ly_login_button_bar)
    LinearLayout lyLoginButtonBar;
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.dt_account_text)
    DeleteEditText dtAccountText;
    @BindView(R2.id.ly_login)
    LinearLayout lyLogin;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.verification_code_see_btn)
    ImageView verificationCodeSeeBtn;
    @BindView(R2.id.fy_password)
    FrameLayout fyPassword;
    @BindView(R2.id.bt_login)
    Button btLogin;

    private boolean isClick = false;

    @NonNull
    private UmengAuthUtils mUmengUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.module_login_zbtxz_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        etPasswordText.addTextChangedListener(this);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.module_login_toolbar)).getView();
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


    //短信验证成功后登陆需要关闭该页面
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

    @OnClick({R2.id.dt_account_text, R2.id.bt_login, R2.id.tv_forget_password_btn, R2.id.verification_code_see_btn, R2.id.tv_verification_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.dt_account_text:
                dtAccountText.setCursorVisible(true);
                break;
            //登录
            case R2.id.bt_login:
                if (!ClickTracker.isDoubleClick()) {
                    WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), this);
                }
                break;
            //重置密码
            case R2.id.tv_forget_password_btn:
//                if (!ClickTracker.isDoubleClick()) {
//                    startActivity(new Intent(this, ZBVerificationPassWordActivity.class));
//                }
                break;
            //验证码登录
            case R2.id.tv_verification_btn:
                break;
            //密码可视
            case R2.id.verification_code_see_btn:
                int length = dtAccountText.getText().toString().length();
                if (length > 0) {
                    if (!isClick) {
                        //开启
                        etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordText.setSelection(length);
                        isClick = true;
                    } else {
                        //隐藏
                        etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordText.setSelection(length);
                        isClick = false;
                    }
                } else {
                    T.showShort(this, "输入密码后显示明文");
                }
                break;
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
        //登录验证
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBLoginActivity.this, "登录失败");
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
                    T.showShortNow(ZBLoginActivity.this, "登录失败");
                }

            }
        }).setTag(this).exe(s, "ZB", "", dtAccountText.getText(), "");
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
//            SyncUtil.getInstance().syncUserData(this);
        } else {
            UserBiz.get().loginCancel();
        }
        //activity中堆栈要多于1个页面
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
