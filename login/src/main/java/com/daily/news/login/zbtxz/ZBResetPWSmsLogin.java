package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.biz.core.global.Key.YiDun.Type;
import cn.daily.news.biz.core.utils.YiDunUtils;

/**
 * 短信验证码登录 / 重置密码获取验证码
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
    @BindView(R2.id.tv_title)
    TextView mTvTitle;

    /**
     * 登录类型：true:验证码登录/false:重置密码
     */
    private String login_type = "";
    /**
     * 边锋返回uid
     */
    private String uuid = "";

    /**
     * 验证码定时器
     */
//    private TimerManager.TimerTask timerTask;
    private CountDownTimer timer;


    private boolean isCommentActivity = false;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(Key.LOGIN_TYPE)) {
                login_type = intent.getStringExtra(Key.LOGIN_TYPE);
            }

            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
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

    /**
     * 文案
     */
    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(dtAccountText, false);
        btConfirm.setText(getString(R.string.zb_confirm));
        tvTerification.setText(getString(R.string.zb_sms_verication));
        if (login_type.equals(Key.Value.LOGIN_SMS_TYPE)) {
            tvChangeLoginType.setText(getString(R.string.zb_password_login));
            if (mTvTitle.getVisibility() == View.VISIBLE) {
                mTvTitle.setVisibility(View.GONE);
            }
            btConfirm.setText("登录");
        } else {
            if (mTvTitle.getVisibility() == View.GONE) {
                mTvTitle.setVisibility(View.VISIBLE);
            }
            tvChangeLoginType.setText(getString(R.string.zb_input_sms_tip));
            btConfirm.setText("确定");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TimerManager.cancel(timerTask);
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
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
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string
                            .zb_phone_num_inout_error));
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_error));
                }
            }
            //进入重置密码页面/登录 (手机号/验证码均不能为空)
        } else if (view.getId() == R.id.bt_confirm) {
            //验证码
            if (etSmsText.getText() != null && !TextUtils.isEmpty(etSmsText.getText().toString())) {
                //进入账号密码登录页面
                if (dtAccountText.getText() != null && !TextUtils.isEmpty(dtAccountText.getText()
                        .toString())) {
                    regAndLogin(uuid, etSmsText.getText().toString(), dtAccountText.getText()
                            .toString());
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string
                            .zb_phone_num_inout_error));
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        } else {
            AppManager.get().finishActivity(ZBLoginActivity.class);
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
            Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_LOGIN);
        }

    }


    private Bundle bundle;

    /**
     * 先注册账号再登录
     *
     * @param uid
     * @param smsCode
     * @param phoneNum
     */
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode, @NonNull final
    String phoneNum) {
        if (login_type.equals(Key.Value.LOGIN_SMS_TYPE)) {
            //短信验证
            LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
            WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

                @Override
                public void onFailure(int i, String s) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,s);
//                    T.showShort(ZBResetPWSmsLogin.this, s);
                }

                @Override
                public void onSuccess(String token) {
                    if (null == token || token.isEmpty()) {
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(true,getString(R.string.zb_login_error));
//                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_login_error));
                    } else {
                        loginZBServer(WoaSdk.getTokenInfo().getSessionId());
                    }

                }
            });
        } else {//重置密码验证
            LoadingDialogUtils.newInstance().getLoginingDialog("正在处理");
            Passport.validateSmsCaptch(ZBResetPWSmsLogin.this, new OnValidateSmsCaptchListener() {

                @Override
                public void onFailure(int i, String s) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,s);
//                    T.showShort(ZBResetPWSmsLogin.this, s);
                }

                @Override
                public void onSuccess() {
                    LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                    if (bundle == null) {
                        bundle = new Bundle();
                    }
                    bundle.putString(Key.UUID, uuid);
                    bundle.putString(Key.ACCOUNTID, dtAccountText.getText().toString());
                    bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                    Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager
                            .ZB_RESET_PASSWORD);
                }
            }, dtAccountText.getText().toString(), smsCode);
        }

    }

    /**
     * 短信验证码登录
     * 服务端这边短信验证登录需要我们调用注册验证接口
     *
     * @param sessionId 登录ZB服务器 返回浙报服务器的token
     */
    private void loginZBServer(String sessionId) {
        //登录验证
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false);
                new Analytics.AnalyticsBuilder(getActivity(), "A0001", "600015", "Login",false)
                        .setEvenName("浙报通行证，手机验证码登录成功")
                        .setPageType("登录页")
                        .setEventDetail("手机号登入")
                        .setIscuccesee(false)
                        .pageType("登录页")
                        .loginType("手机号")
                        .build()
                        .send();
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                    new Analytics.AnalyticsBuilder(getActivity(), "A0001", "600015", "Login",false)
                            .setEvenName("浙报通行证，手机验证码登录成功")
                            .setPageType("登录页")
                            .setEventDetail("手机号登入")
                            .setIscuccesee(true)
                            .pageType("登录页")
                            .loginType("手机号")
                            .userID(bean.getSession().getAccount_id())
                            .mobilePhone(bean.getAccount().getMobile())
                            .build()
                            .send();

                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功
                    if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) {
//                    if (!userBiz.isCertification()) { // 进入实名制页面
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                        Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager
                                .ZB_MOBILE_VERIFICATION);
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);
                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                    } else {
                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);
                        // 关闭登录入口页
                        AppManager.get().finishActivity(LoginActivity.class);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_login_error));
//                    T.showShortNow(ZBResetPWSmsLogin.this, getString(R.string.zb_login_error));
                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", dtAccountText.getText().toString(),
                null, dtAccountText.getText().toString(), 1,YiDunUtils.getToken(Type.REG));
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
                                    "",
                                    new OnGetSmsCaptchaListener() {
                                        @Override
                                        public void onFailure(int i, String s) {
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                            T.showShort(ZBResetPWSmsLogin.this, s);
                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            startTimeCountDown();
                                            //获取uuid
                                            uuid = s;
                                            //提示短信已发送成功
                                            T.showShortNow(ZBResetPWSmsLogin.this, getString(R
                                                    .string.zb_sms_send));
                                        }
                                    });
                        } else {//重置密码
                            Passport.getSmsCaptcha(ZBResetPWSmsLogin.this,
                                    new com.bianfeng.passport.OnGetSmsCaptchaListener() {
                                        @Override
                                        public void onFailure(int i, String s) {
//                                            TimerManager.cancel(timerTask);
                                            if (timer != null) {
                                                timer.cancel();
                                            }
                                            T.showShort(ZBResetPWSmsLogin.this, s);
                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            startTimeCountDown();
                                            //获取uuid
                                            uuid = s;
                                            //提示短信已发送成功
                                            T.showShortNow(ZBResetPWSmsLogin.this, getString(R
                                                    .string.zb_sms_send));
                                        }
                                    }, dtAccountText.getText().toString());
                        }

                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string
                                .tip_permission_denied));

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
        timer = new CountDownTimer(61 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long value = millisUntilFinished / 1000;
                tvTerification.setBackgroundResource(R.drawable.border_timer_text_bg);
                tvTerification.setTextColor(getResources().getColor(R.color._999999));
                tvTerification.setText("(" + value + ")" + getString(R.string
                        .zb_login_get_validationcode_again));
            }

            @Override
            public void onFinish() {
                tvTerification.setEnabled(true);
                //TODO  WLJ 夜间模式
                tvTerification.setBackgroundResource(R.drawable
                        .module_login_bg_sms_verification);
                tvTerification.setTextColor(getResources().getColor(R.color._f44b50));
                tvTerification.setText(getString(R.string
                        .zb_login_resend));
            }
        };
        timer.start();
//        timerTask = new TimerManager.TimerTask(1000, 1000) {
//            @Override
//            public void run(long count) {
//                long value = (60 - count);
//                tvTerification.setBackgroundResource(R.drawable.border_timer_text_bg);
//                tvTerification.setTextColor(getResources().getColor(R.color._999999));
//                tvTerification.setText("(" + value + ")" + getString(R.string
//                        .zb_login_get_validationcode_again));
//                if (value == 0) {
//                    TimerManager.cancel(this);
//                    tvTerification.setEnabled(true);
//                    //TODO  WLJ 夜间模式
//                    tvTerification.setBackgroundResource(R.drawable
//                            .module_login_bg_sms_verification);
//                    tvTerification.setTextColor(getResources().getColor(R.color._f44b50));
//                    tvTerification.setText(getString(R.string
//                            .zb_login_resend));
//                }
//            }
//        };
//        TimerManager.schedule(timerTask);
    }

}
