package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bianfeng.passport.OnValidateSmsCaptchListener;
import com.bianfeng.passport.Passport;
import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.core.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手机验证码登录/重置密码获取验证码
 * Created by wanglinjie.
 * create time:2017/8/11  下午3:49
 */

public class ZBResetPWSmsLogin extends BaseActivity {

    @BindView(R2.id.dt_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText etSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView tvTerification;
    @BindView(R2.id.tv_change_login_type)
    TextView tvChangeLoginType;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;

    /**
     * 登录类型：true:验证码登录/false:重置密码
     */
    private String login_type = "";
    private String uuid = "";

    private TimerManager.TimerTask timerTask;

    /**
     * 请求码
     */
    private int REQUEST_CODE = -1;


    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            login_type = data.getQueryParameter(Key.LOGIN_TYPE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_reset_password);
        ButterKnife.bind(this);
        getIntentData(getIntent());
        initView();
    }

    private void initView() {
        btConfirm.setText(getString(R.string.zb_confirm));
        tvTerification.setText(getString(R.string.zb_sms_verication));
        if (login_type.equals(Key.Value.LOGIN_SMS_TYPE)) {
            tvChangeLoginType.setText(getString(R.string.zb_password_login));
        } else {
            tvChangeLoginType.setText(getString(R.string.zb_input_sms_tip));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.cancel(timerTask);
    }


    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login)).getView();
    }

    @OnClick({R2.id.tv_sms_verification, R2.id.bt_confirm, R2.id.tv_change_login_type})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        //获取验证码需要先输入手机号
        if (view.getId() == R.id.tv_sms_verification) {
            if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                getverificationPermission();
            } else {
                if (dtAccountText.getText().toString().equals("")) {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_inout_error));
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_error));
                }
            }
            //进入重置密码页面/登录 (手机号/验证码均不能为空)
        } else if (view.getId() == R.id.bt_confirm) {
            //验证码
            if (etSmsText.getText() != null && !TextUtils.isEmpty(etSmsText.getText().toString())) {
                //进入账号密码登录页面
                if (dtAccountText.getText() != null && !TextUtils.isEmpty(dtAccountText.getText().toString())) {
                    regAndLogin(uuid, etSmsText.getText().toString(), dtAccountText.getText().toString());
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_inout_error));
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        } else {
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBLoginActivity")
                    .buildUpon()
                    .build(), REQUEST_CODE);
        }

    }


    /**
     * 先注册账号再登录
     *
     * @param uid
     * @param smsCode
     * @param phoneNum
     */
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode, @NonNull final String phoneNum) {
        if (login_type.equals(Key.Value.LOGIN_SMS_TYPE)) {
            //短信验证
            WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

                @Override
                public void onFailure(int i, String s) {
                    T.showShort(ZBResetPWSmsLogin.this, s);
                }

                @Override
                public void onSuccess(String token) {
                    if (null == token || token.isEmpty()) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_reg_error));
                    } else {
                        loginZBServer(WoaSdk.getTokenInfo().getSessionId(), phoneNum);
                    }

                }
            });
        } else {//重置密码验证
            Passport.validateSmsCaptch(ZBResetPWSmsLogin.this, new OnValidateSmsCaptchListener() {

                @Override
                public void onFailure(int i, String s) {
                    T.showShort(ZBResetPWSmsLogin.this, s);
                }

                @Override
                public void onSuccess() {
                    Nav.with(ZBResetPWSmsLogin.this).to(Uri.parse("http://www.8531.cn/login/ZBResetNewPassWord")
                            .buildUpon()
                            .appendQueryParameter(Key.UUID, uuid)
                            .appendQueryParameter(Key.ACCOUNTID, dtAccountText.getText().toString())
                            .build(), REQUEST_CODE);

                }
            }, dtAccountText.getText().toString(), smsCode);
        }

    }

    /**
     * 短信验证码登录
     *
     * @param sessionId
     * @param phoneNum  登录ZB服务器
     *                  返回浙报服务器的token
     */
    private void loginZBServer(String sessionId, final String phoneNum) {
        //登录验证
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBResetPWSmsLogin.this, getString(R.string.zb_reg_error));
            }

            @Override
            public void onSuccess(@NonNull ZBLoginBean bean) {
                UserBiz userBiz = UserBiz.get();
                userBiz.setZBLoginBean(bean);
                //进入实名制页面
                Nav.with(UIUtils.getActivity()).to(Uri.parse("http://www.8531.cn/login/ZBMobileValidateActivity")
                        .buildUpon()
                        .build(), 0);
                //设置回调数据
                setResult(RESULT_OK);
                finish();
            }
        }).setTag(this).exe(sessionId, "BIANFENG", dtAccountText.getText().toString(), dtAccountText.getText().toString(), dtAccountText.getText().toString());
    }

    /**
     * 获取短信验证权限
     */
    private void getverificationPermission() {
        PermissionManager.get().request(ZBResetPWSmsLogin.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        if (login_type.equals(Key.Value.LOGIN_SMS_TYPE)) {
                            //短信登录
                            WoaSdk.getSmsCaptcha(ZBResetPWSmsLogin.this,
                                    dtAccountText.getText().toString(),
                                    etSmsText.getText().toString(),
                                    new OnGetSmsCaptchaListener() {
                                        @Override
                                        public void onFailure(int i, String s) {
                                            TimerManager.cancel(timerTask);
                                            T.showShort(ZBResetPWSmsLogin.this, s);
                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            startTimeCountDown();
                                            //获取uuid
                                            uuid = s;
                                            //提示短信已发送成功
                                            T.showShortNow(ZBResetPWSmsLogin.this, getString(R.string.zb_sms_send));
                                        }
                                    });
                        } else {//重置密码
                            Passport.getSmsCaptcha(ZBResetPWSmsLogin.this,
                                    new com.bianfeng.passport.OnGetSmsCaptchaListener() {
                                        @Override
                                        public void onFailure(int i, String s) {
                                            TimerManager.cancel(timerTask);
                                            T.showShort(ZBResetPWSmsLogin.this, s);
                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            startTimeCountDown();
                                            //获取uuid
                                            uuid = s;
                                            //提示短信已发送成功
                                            T.showShortNow(ZBResetPWSmsLogin.this, getString(R.string.zb_sms_send));
                                        }
                                    }, dtAccountText.getText().toString());
                        }

                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.tip_permission_denied));

                    }

                    @Override
                    public void onElse(List<String> deniedPerms, List<String>
                            neverAskPerms) {
                    }
                }, Permission.PHONE_READ_PHONE_STATE);
    }

    /**
     * 开始倒计时
     * 重复访问获取验证码的时间是多少  60s  3次  一天最多5次
     */
    private void startTimeCountDown() {
        tvTerification.setEnabled(false);
        //倒计时
        timerTask = new TimerManager.TimerTask(1000, 1000) {
            @Override
            public void run(long count) {
                long value = (60 - count);
                tvTerification.setBackgroundResource(R.drawable.border_timer_text_bg);
                tvTerification.setTextColor(getResources().getColor(R.color.tc_999999));
                tvTerification.setText("(" + value + ")" + getString(R.string.zb_login_get_validationcode_again));
                if (value == 0) {
                    TimerManager.cancel(this);
                    tvTerification.setEnabled(true);
                    tvTerification.setBackground(null);
                    tvTerification.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
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
}
