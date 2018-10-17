package com.daily.news.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daily.news.login.baseview.TipPopup;
import com.daily.news.login.task.UserProtectBean;
import com.daily.news.login.task.UserProtectTask;
import com.daily.news.login.task.ZBLoginValidateTask;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.ZBUtils;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.Entity.LoginInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ZbConstants;
import com.zjrb.passport.listener.ZbCaptchaSendListener;
import com.zjrb.passport.listener.ZbLoginListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * Date: 2018/8/14
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 注册登录界面
 */
public class LoginMainActivity extends BaseActivity {
    @BindView(R2.id.iv_logo)
    ImageView mIvLogo;
    @BindView(R2.id.et_account_text)
    EditText mEtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText mEtSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView mTvSmsVerification;
    @BindView(R2.id.ly_sms_code)
    LinearLayout mLySmsCode;
    @BindView(R2.id.btn_login)
    TextView mBtnLogin;
    @BindView(R2.id.tv_password_login)
    TextView mTvPasswordLogin;
    @BindView(R2.id.tv_module_login_wx)
    TextView mTvModuleLoginWx;
    @BindView(R2.id.ll_module_login_wx)
    LinearLayout mLlModuleLoginWx;
    @BindView(R2.id.tv_module_login_qq)
    TextView mTvModuleLoginQq;
    @BindView(R2.id.ll_module_login_qq)
    LinearLayout mLlModuleLoginQq;
    @BindView(R2.id.tv_module_login_wb)
    TextView mTvModuleLoginWb;
    @BindView(R2.id.ll_module_login_wb)
    LinearLayout mLlModuleLoginWb;
    @BindView(R2.id.tv_link_tip)
    TextView mTvLinkTip;
    @BindView(R2.id.tv_link)
    TextView mTvLink;
    @BindView(R2.id.iv_phone_close)
    ImageView mIvPhoneClose;
    @BindView(R2.id.iv_sms_close)
    ImageView mIvSmsClose;
    private UmengAuthUtils mUmengUtils;

    private Bundle bundle;
    private String mobile;


    /**
     * 验证码定时器
     */
    private TimerManager.TimerTask timerTask;
    private boolean isFromComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_main);
        getIntentData(getIntent());
        ButterKnife.bind(this);

        initLoginRV();
        LoginHelper.get().setLogin(true); // 标记开启
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        handleLastLogin();
    }

    /**
     * 处理上次登录的状态
     */
    private void handleLastLogin() {
        boolean isPhone = SPHelper.get().get("isPhone", false);
        String lastLogin = SPHelper.get().get("last_login", "");
        if (isPhone) { // 显示上次登录的手机号及头像
            String lastLogo = SPHelper.get().get("last_logo", "");
            if (!TextUtils.isEmpty(lastLogin) && AppUtils.isMobileNum(lastLogin)) {
                mEtAccountText.setText(lastLogin);
                mEtAccountText.setSelection(lastLogin.length());
                mIvPhoneClose.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions();
                options.placeholder(R.mipmap.default_user_icon);
                options.centerCrop();
                options.circleCrop();
                Glide.with(this).load(lastLogo).apply(options).into(mIvLogo);
            }
        } else { // 显示三方登录的气泡
            if (TextUtils.equals(lastLogin, "wei_xin")) {
                showPopup(mLlModuleLoginWx);
            } else if (TextUtils.equals(lastLogin, "wei_bo")) {
                showPopup(mLlModuleLoginWb);
            } else if (TextUtils.equals(lastLogin, "qq")) {
                showPopup(mLlModuleLoginQq);
            }
        }
    }

    private void showPopup(View view) {
        new TipPopup(getActivity()).showAboveView(view);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_login_register)).getView();
    }

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isFromComment = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
            if (intent.hasExtra("mobile")) {
                mobile = intent.getStringExtra("mobile");
            }
        }
    }

    /**
     * 初始化滚动列表数据
     */
    private void initLoginRV() {
        AppUtils.setEditTextInhibitInputSpace(mEtAccountText, false);
        if (!TextUtils.isEmpty(mobile)) {
            mEtAccountText.setText(mobile);
            mIvPhoneClose.setVisibility(View.VISIBLE);
        } else {
            mIvPhoneClose.setVisibility(View.GONE);
        }
        mIvSmsClose.setVisibility(View.GONE);
        mEtAccountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mIvPhoneClose.setVisibility(View.VISIBLE);
                } else {
                    mIvPhoneClose.setVisibility(View.GONE);
                }
            }
        });
        mEtSmsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mIvSmsClose.setVisibility(View.VISIBLE);
                } else {
                    mIvSmsClose.setVisibility(View.GONE);
                }
            }
        });
        mBtnLogin.setText(getString(R.string.zb_login));
        mTvSmsVerification.setText(getString(R.string.zb_sms_verication));
        mTvPasswordLogin.setText(getString(R.string.zb_login_by_password));
        mTvModuleLoginWx.setText(getString(R.string.zb_login_type_wx));
        mTvModuleLoginQq.setText(getString(R.string.zb_login_type_qq));
        mTvModuleLoginWb.setText(getString(R.string.zb_login_type_wb));
        mTvLinkTip.setText(getText(R.string.zb_login_tip));
        mTvLink.setText(getText(R.string.zb_reg_link));
    }


    /**
     * @param v 点击注册
     */
    @OnClick({R2.id.btn_login, R2.id.ll_module_login_wx, R2.id.ll_module_login_qq,
            R2.id.ll_module_login_wb, R2.id.tv_sms_verification, R2.id.tv_password_login, R2.id.iv_phone_close, R2.id.iv_sms_close, R2.id.tv_link})
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (v.getId() == R.id.btn_login) { // 登录
            // 若当前未输入手机号，则弹出toast"请输入手机号"
            // 若当前未输入验证码，则弹出toast"请输入验证码"
            // 若输入验证码错误，弹出toast“验证码输入错误”
            // 若当前输入的手机号为未注册的手机号，输入短信验证码验证成功之后，则将自动注册并登录成功，跳转回登录前对应的页面。
            // 若当前输入的手机号为已注册的手机号，输入短信验证码验证成功之后，则登录成功，跳转回登录前对应的页面。
            if (!TextUtils.isEmpty(mEtSmsText.getText().toString())) {
                if (AppUtils.isMobileNum(mEtAccountText.getText().toString())) { // 手机号验证码登录不需要先进行绑定的校验,因为该接口未注册的手机号会自动注册通行证
                    LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                    doLogin(mEtAccountText.getText().toString(), mEtSmsText.getText().toString());
                } else {
                    if (TextUtils.isEmpty(mEtAccountText.getText().toString())) {
                        T.showShort(LoginMainActivity.this, getString(R.string
                                .zb_phone_num_inout_error));
                    } else {
                        T.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                    }
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        } else if (v.getId() == R.id.ll_module_login_wx) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.WEIXIN, isFromComment);
        } else if (v.getId() == R.id.ll_module_login_qq) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.QQ, isFromComment);
        } else if (v.getId() == R.id.ll_module_login_wb) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.SINA, isFromComment);
        } else if (v.getId() == R.id.tv_sms_verification) { // 发送登录验证码
            if (AppUtils.isMobileNum(mEtAccountText.getText().toString())) {
                sendSmsCaptcha();
            } else {
                if (TextUtils.isEmpty(mEtAccountText.getText().toString())) {
                    T.showShort(LoginMainActivity.this, getString(R.string
                            .zb_phone_num_inout_error));
                } else {
                    T.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                }
            }
        } else if (v.getId() == R.id.tv_password_login) { // 账号密码登录
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_PASSWORD_LOGIN);
        } else if (v.getId() == R.id.iv_phone_close) { // 清空手机号
            mEtAccountText.setText("");
            mIvPhoneClose.setVisibility(View.GONE);
        } else if (v.getId() == R.id.iv_sms_close) { // 清空验证码
            mEtSmsText.setText("");
            mIvSmsClose.setVisibility(View.GONE);
        } else if (v.getId() == R.id.tv_link) {
            Nav.with(LoginMainActivity.this).toPath("/login/ZBUserProtectActivity");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUmengUtils != null) {
            mUmengUtils.onResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("login_successful"));
        if (UserBiz.get().isLoginUser()) {
            setResult(Activity.RESULT_OK);
        }
        super.finish();
        LoginHelper.get().finish(); // 登录结束

    }

    /**
     * 获取用户协议地址
     */
    private String urlData = "";

    private void getUserProject() {

        new UserProtectTask(new APIExpandCallBack<UserProtectBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(LoginMainActivity.this, errMsg);
            }

            @Override
            public void onSuccess(UserProtectBean bean) {
                if (bean != null) {
                    urlData = bean.getUser_agreement();
                }
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString("url", urlData);
                Nav.with(LoginMainActivity.this).setExtras(bundle).toPath("/login/ZBUserProtectActivity");
            }
        }).setTag(this).exe();
    }

    /**
     * 发送验证码,需进行权限校验
     */
    private void sendSmsCaptcha() {
        PermissionManager.get().request(LoginMainActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        //短信登录
                        ZbPassport.sendCaptcha(ZbConstants.Sms.LOGIN, mEtAccountText.getText().toString(), new ZbCaptchaSendListener() {
                            @Override
                            public void onSuccess(@Nullable String passData) {
                                startTimeCountDown();
                                //提示短信已发送成功
                                T.showShortNow(LoginMainActivity.this, getString(R
                                        .string.zb_sms_send));
                            }

                            @Override
                            public void onFailure(int errorCode, String errorMessage) {
                                TimerManager.cancel(timerTask);
                                T.showShort(LoginMainActivity.this, errorMessage);
                            }
                        });

                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(LoginMainActivity.this, getString(R.string
                                .tip_permission_denied));

                    }

                    @Override
                    public void onElse(List<String> deniedPerms, List<String>
                            neverAskPerms) {
                    }
                }, Permission.PHONE_READ_PHONE_STATE);
    }


    /**
     * 手机号验证码登录
     *
     * @param phone
     * @param captcha
     */
    private void doLogin(final String phone, String captcha) {
        ZbPassport.loginCaptcha(phone, captcha, new ZbLoginListener() {

            @Override
            public void onSuccess(LoginInfo loginInfo, @Nullable String passData) {
                if (loginInfo != null) {
                    // 登录认证
                    loginValidate(phone, loginInfo.getToken());
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                    T.showShortNow(LoginMainActivity.this, getString(R.string.zb_login_error)); // 登录失败
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                T.showShort(LoginMainActivity.this, errorMessage);
            }
        });
    }

    /**
     * 登录认证
     *
     * @param phone
     * @param token
     */
    private void loginValidate(final String phone, String token) {
        new ZBLoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                new Analytics.AnalyticsBuilder(getContext(), "A0001", "600016", "Login", false)
                        .setEvenName("浙报通行证，手机号/个性账号/邮箱登录成功")
                        .setPageType("主登录页")
                        .setEventDetail("手机号/个性账号/邮箱")
                        .setIscuccesee(false)
                        .pageType("主登录页")
                        .loginType("手机号;个性账号;邮箱")
                        .build()
                        .send();
//                T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                    new Analytics.AnalyticsBuilder(getContext(), "A0001", "600016", "Login", false)
                            .setEvenName("浙报通行证，手机号/个性账号/邮箱登录成功")
                            .setPageType("主登录页")
                            .setEventDetail("手机号/个性账号/邮箱")
                            .setIscuccesee(true)
                            .pageType("主登录页")
                            .loginType("手机号;个性账号;邮箱")
                            .userID(bean.getSession().getAccount_id())
                            .mobilePhone(bean.getAccount().getMobile())
                            .build()
                            .send();
                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功
                    ZBUtils.showPointDialog(bean);
                    SPHelper.get().put("isPhone", true).commit();
                    SPHelper.get().put("last_login", phone).commit();
                    SPHelper.get().put("last_logo", bean.getAccount() == null ? "": bean.getAccount().getImage_url());
                    finish();
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, "登录失败");
                }
            }
        }).setTag(this).exe(phone, phone, "phone_number", token);
    }

    /**
     * 开始倒计时
     * 重复访问获取验证码的时间是多少  60s  3次  一天最多5次
     */
    private void startTimeCountDown() {
        mTvSmsVerification.setEnabled(false);
        //倒计时
        timerTask = new TimerManager.TimerTask(1000, 1000) {
            @Override
            public void run(long count) {
                long value = (60 - count);
                mTvSmsVerification.setBackgroundResource(R.drawable.border_timer_text_bg);
                mTvSmsVerification.setTextColor(getResources().getColor(R.color.tc_999999));
                mTvSmsVerification.setText("(" + value + ")" + getString(R.string
                        .zb_login_get_validationcode_again));
                if (value == 0) {
                    TimerManager.cancel(this);
                    mTvSmsVerification.setEnabled(true);
                    mTvSmsVerification.setBackgroundResource(R.drawable
                            .module_login_bg_sms_verification);
                    mTvSmsVerification.setTextColor(getResources().getColor(R.color.tc_f44b50));
                    mTvSmsVerification.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
    }

}

