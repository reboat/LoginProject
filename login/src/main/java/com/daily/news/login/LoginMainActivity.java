package com.daily.news.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daily.news.login.baseview.TipPopup;
import com.daily.news.login.task.ZBLoginValidateTask;
import com.daily.news.login.util.LoginUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.common.glide.GlideApp;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.Entity.AuthInfo;
import com.zjrb.passport.Entity.ClientInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbAuthListener;
import com.zjrb.passport.listener.ZbGraphicListener;
import com.zjrb.passport.listener.ZbInitListener;
import com.zjrb.passport.listener.ZbResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.analytics.AnalyticsManager;
import cn.daily.news.biz.core.DailyActivity;
import cn.daily.news.biz.core.UserBiz;
import cn.daily.news.biz.core.model.BaseData;
import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.nav.Nav;
import cn.daily.news.biz.core.network.compatible.APICallBack;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIPostTask;
import cn.daily.news.biz.core.ui.dialog.ZBSingleDialog;
import cn.daily.news.biz.core.ui.dialog.ZbGraphicDialog;
import cn.daily.news.biz.core.ui.toast.ZBToast;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;
import cn.daily.news.biz.core.umeng.UmengAuthUtils;
import cn.daily.news.biz.core.utils.LoadingDialogUtils;
import cn.daily.news.biz.core.utils.LoginHelper;
import cn.daily.news.biz.core.utils.MultiInputHelper;
import cn.daily.news.biz.core.utils.RouteManager;
import cn.daily.news.biz.core.utils.YiDunUtils;
import cn.daily.news.biz.core.utils.ZBUtils;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * Date: 2018/8/14
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 注册登录界面
 */
public class LoginMainActivity extends DailyActivity {
    private static final int CODE_LOG_OFF = 52004;//已经注销
    @BindView(R2.id.et_account_text)
    EditText mEtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText mEtSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView mTvSmsVerification;
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
    private UmengAuthUtils mUmengUtils;

    private Bundle bundle;
    private MultiInputHelper mInputHelper;
    private Intent intent;


    /**
     * 验证码定时器
     */
    private CountDownTimer timer;
    boolean isPhone;
    String lastLogin;
    private TipPopup mPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_main);
        ButterKnife.bind(this);
        initLoginRV();
        LoginHelper.get().setLogin(true); // 标记开启
        //创建输入监听辅助类，传入提交按钮view
        mInputHelper = new MultiInputHelper(mBtnLogin);
        //添加需要监听的textview
        mInputHelper.addViews(mEtAccountText, mEtSmsText);
        // 获取上次登录数据
        isPhone = SPHelper.get().get("isPhone", false);
        lastLogin = SPHelper.get().get("last_login", "");
        intent = getIntent();
        if (isPhone) { // 显示上次登录的手机号及头像
            if (!TextUtils.isEmpty(lastLogin) && AppUtils.isMobileNum(lastLogin)) {
                mEtAccountText.setText(lastLogin);
                mEtAccountText.setSelection(lastLogin.length());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 容错,如果cookie为空的时候,重新调用一次init接口
        if (TextUtils.isEmpty(ZbPassport.getZbConfig().getCookie())) {
            ZbPassport.initApp(new ZbInitListener() {
                @Override
                public void onSuccess(ClientInfo info) {
                    if (info != null) {
                        ZbPassport.getZbConfig().setSignatureKey(info.getSignature_key()); // 设置签名密钥,30分钟有效期
                    }
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            handleLastThirdLogin();
        }
    }

    /**
     * 处理上次三方登录的状态,popupwindow在onCreate里面show会出现badToken的问题
     */
    private void handleLastThirdLogin() {
        if (!isPhone) { // 显示三方登录的气泡
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
        mPopup = new TipPopup(getActivity());
        mPopup.showAboveView(view);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return BIZTopBarFactory.createDefaultForLogin(view, this).getView();
    }


    /**
     * 初始化滚动列表数据
     */
    private void initLoginRV() {
        AppUtils.setEditTextInhibitInputSpace(mEtAccountText, false);
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
            R2.id.ll_module_login_wb, R2.id.tv_sms_verification, R2.id.tv_password_login, R2.id.tv_link})
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
            if (TextUtils.isEmpty(mEtAccountText.getText().toString())) {
                ZBToast.showShort(LoginMainActivity.this, getString(R.string
                        .zb_phone_num_empty));
            } else {
                if (!TextUtils.isEmpty(mEtSmsText.getText().toString())) {
                    if (AppUtils.isMobileNum(mEtAccountText.getText().toString())) { // 手机号验证码登录不需要先进行绑定的校验,因为该接口未注册的手机号会自动注册通行证
                        LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                        doLogin(mEtAccountText.getText().toString(), mEtSmsText.getText().toString());
                    } else {
                        ZBToast.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                    }
                } else {
                    ZBToast.showShort(this, getString(R.string.zb_input_sms_verication));
                }
            }

        } else if (v.getId() == R.id.ll_module_login_wx) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.WEIXIN);
        } else if (v.getId() == R.id.ll_module_login_qq) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.QQ);
        } else if (v.getId() == R.id.ll_module_login_wb) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.SINA);
        } else if (v.getId() == R.id.tv_sms_verification) { // 发送登录验证码
            if (AppUtils.isMobileNum(mEtAccountText.getText().toString())) {
                checkLogOff(mEtAccountText.getText().toString());
                mEtSmsText.requestFocus();
                new Analytics.AnalyticsBuilder(getContext(), "700052", "AppTabClick", false)
                        .name("点击获取短信验证码")
                        .pageType("登录注册页")
                        .clickTabName("短信验证码")
                        .build()
                        .send();
            } else {
                if (TextUtils.isEmpty(mEtAccountText.getText().toString())) {
                    ZBToast.showShort(LoginMainActivity.this, getString(R.string
                            .zb_phone_num_empty));
                } else {
                    ZBToast.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                }
            }
        } else if (v.getId() == R.id.tv_password_login) { // 账号密码登录
            Nav.with(LoginMainActivity.this).toPath(RouteManager.ZB_PASSWORD_LOGIN);
            new Analytics.AnalyticsBuilder(getContext(), "700053", "AppTabClick", false)
                    .name("点击通过账号密码登录")
                    .pageType("登录注册页")
                    .clickTabName("通过账号密码登录")
                    .build()
                    .send();
        } else if (v.getId() == R.id.tv_link) {
            Nav.with(LoginMainActivity.this).toPath("/login/ZBUserProtectActivity");
        }
    }

    private void checkLogOff(String phoneNumber) {
        new CheckLogOffTask().exe(phoneNumber);
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
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("LoginMainIsLoginUser", UserBiz.get().isLoginUser());
        setResult(Activity.RESULT_OK, intent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("login_successful"));
        if (timer != null) {
            timer.cancel();
        }
        mInputHelper.removeViews();
        super.finish();
        LoginHelper.get().finish(); // 登录结束

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    /**
     * 发送验证码,需进行权限校验
     */
    private void sendSmsCaptcha() {
        ZbPassport.sendCaptcha(mEtAccountText.getText().toString(), "", new ZbResultListener() {
            @Override
            public void onSuccess() {
                startTimeCountDown();
                //提示短信已发送成功
                ZBToast.showShort(LoginMainActivity.this, getString(R.string.zb_sms_send));
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                // 需要图形验证码的情况
                if (errorCode == ErrorCode.ERROR_NEED_GRRPHICS) {
                    final ZbGraphicDialog zbGraphicDialog = new ZbGraphicDialog(LoginMainActivity.this);
                    zbGraphicDialog.setBuilder(new ZbGraphicDialog.Builder()
                            .setMessage("请先验证图形验证码")
                            .setOkText("确定")
                            .setOnClickListener(new ZbGraphicDialog.OnDialogClickListener() {
                                @Override
                                public void onLeftClick() {
                                    if (zbGraphicDialog.isShowing()) {
                                        zbGraphicDialog.dismiss();
                                        new Analytics.AnalyticsBuilder(getContext(), "700060", "AppTabClick", false)
                                                .name("取消输入图形验证码")
                                                .pageType("登录注册页")
                                                .clickTabName("取消")
                                                .build()
                                                .send();
                                    }
                                }

                                @Override
                                public void onRightClick() {
                                    if (TextUtils.isEmpty(zbGraphicDialog.getEtGraphic().getText().toString())) {
                                        ZBToast.showShort(LoginMainActivity.this, "请先输入图形验证码");
                                    } else {
                                        ZbPassport.sendCaptcha(mEtAccountText.getText().toString(), zbGraphicDialog.getEtGraphic().getText().toString(), new ZbResultListener() {
                                            @Override
                                            public void onSuccess() {
                                                ZBToast.showShort(LoginMainActivity.this, "验证通过");
                                                if (zbGraphicDialog.isShowing()) {
                                                    zbGraphicDialog.dismiss();
                                                }
                                                startTimeCountDown(); // 开始倒计时
                                            }

                                            @Override
                                            public void onFailure(int errorCode, String errorMessage) {
                                                if (timer != null) {
                                                    timer.cancel();
                                                }
                                                ZBToast.showShort(LoginMainActivity.this, errorMessage);
                                            }
                                        });
                                        new Analytics.AnalyticsBuilder(getContext(), "700059", "AppTabClick", false)
                                                .name("确认输入图形验证码")
                                                .pageType("登录注册页")
                                                .clickTabName("确认")
                                                .build()
                                                .send();
                                    }
                                }

                                @Override
                                public void onRefreshImage() {
                                    ZbPassport.getGraphics(new ZbGraphicListener() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            if (bytes != null) {
                                                GlideApp.with(LoginMainActivity.this).load(bytes).diskCacheStrategy(DiskCacheStrategy.NONE).into(zbGraphicDialog.getIvGrahpic());
                                            }
                                        }

                                        @Override
                                        public void onFailure(int errorCode, String errorMessage) {
                                            ZBToast.showShort(LoginMainActivity.this, errorMessage);
                                        }
                                    });
                                }
                            }));
                    zbGraphicDialog.show();
                } else {
                    if (timer != null) {
                        timer.cancel();
                    }
                    ZBToast.showShort(LoginMainActivity.this, errorMessage);
                }
            }
        });
    }


    /**
     * 手机号验证码登录
     *
     * @param phone
     * @param captcha
     */
    private void doLogin(final String phone, String captcha) {
        // 客户端判断验证码为6位数字
        if (captcha.length() != 6) {
            LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
            ZBToast.showShort(LoginMainActivity.this, "验证码错误");
            return;
        }
        ZbPassport.loginCaptcha(phone, captcha, new ZbAuthListener() {

            @Override
            public void onSuccess(AuthInfo loginInfo) {
                if (loginInfo != null) {
                    // 登录认证
                    loginValidate(phone, loginInfo.getCode());
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                    ZBToast.showShort(LoginMainActivity.this, getString(R.string.zb_login_error)); // 登录失败
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                ZBToast.showShort(LoginMainActivity.this, errorMessage);
            }
        });
    }

    /**
     * 登录认证
     *
     * @param phone
     * @param authCode
     */
    private void loginValidate(final String phone, String authCode) {
        new ZBLoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                // 只需要埋点击登录按钮，不用区分最后是否成功
                new Analytics.AnalyticsBuilder(getContext(), "A0001", "Login", false)
                        .name("手机号登录注册成功")
                        .pageType("登录注册页")
                        .action(phone)
                        .loginType("手机号")
                        .build()
                        .send();
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    UserBiz userBiz = UserBiz.get();
                    if (bean.getAccount() != null && !TextUtils.isEmpty(bean.getAccount().getPhone_number())) { // 手机号验证码登录,若结果未返回phoneNum,也跳绑定界面
                        userBiz.setZBLoginBean(bean);
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                        AnalyticsManager.login(bean.getSession().getAccount_id());
                        new Analytics.AnalyticsBuilder(getContext(), "A0001", "Login", false)
                                .name("手机号登录注册成功")
                                .pageType("登录注册页")
                                .action(phone)
                                .mobilePhone(phone)
                                .loginType("手机号")
                                .userID(bean.getSession().getAccount_id())
                                .build()
                                .send();
                        LoginHelper.get().setResult(true); // 设置登录成功
                        ZBUtils.showPointDialog(bean);
                        SPHelper.get().put("isPhone", true).commit();
                        SPHelper.get().put("last_login", phone).commit();
                        finish();
                    } else {
                        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putString("LoginSessionId", bean.getSession().getId());
                        Nav.with(LoginMainActivity.this).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_BIND);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, "登录失败");
                }
            }
        }).setTag(this).exe(phone, phone, "phone_number", authCode, YiDunUtils.getToken(cn.daily.news.biz.core.constant.Key.YiDun.Type.REG));
    }

    /**
     * 开始倒计时
     * 重复访问获取验证码的时间是多少  60s  3次  一天最多5次
     */
    private void startTimeCountDown() {
        timer = LoginUtil.startCountDownTimer(this, mTvSmsVerification, 60);
    }


    private class CheckLogOffCallBack extends APICallBack<BaseData> {
        @Override
        public void onSuccess(BaseData data) {
            sendSmsCaptcha();
        }

        @Override
        public void onError(String errMsg, int errCode) {
            if (errCode == CODE_LOG_OFF) {
                new ZBSingleDialog.Builder()
                        .setMessage("该账号已注销，换个账号试试吧！")
                        .setConfirmText("知道了");
            } else {
                ZBToast.showShort(getContext(), errMsg);
            }
        }
    }

    private class CheckLogOffTask extends APIPostTask<BaseData> {
        public CheckLogOffTask() {
            super(new CheckLogOffCallBack());
        }

        @Override
        public void onSetupParams(Object... params) {
            put("phone_number",params[0]);
        }

        @Override
        public String getApi() {
            return "/api/account/is_logoff";
        }
    }
}

