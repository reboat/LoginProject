package com.daily.news.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.daily.news.login.baseview.TipDialog;
import com.daily.news.login.baseview.TipPopup;
import com.daily.news.login.task.VersionCheckTask;
import com.daily.news.login.task.ZBLoginValidateTask;
import com.daily.news.login.util.LoginUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.permission.IPermissionCallBack;
import com.zjrb.core.permission.Permission;
import com.zjrb.core.permission.PermissionManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.Entity.AuthInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbAuthListener;
import com.zjrb.passport.listener.ZbResultListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.biz.core.DailyActivity;
import cn.daily.news.biz.core.UserBiz;
import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.nav.Nav;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.ui.dialog.ZbGraphicDialog;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;
import cn.daily.news.biz.core.umeng.UmengAuthUtils;
import cn.daily.news.biz.core.update.CheckUpdateTask;
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


    /**
     * 验证码定时器
     */
    private CountDownTimer timer;
    boolean isPhone;
    String lastLogin;

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
        try {
            new VersionCheckTask(new APIExpandCallBack<Void>() {
                @Override
                public void onSuccess(Void data) {
                }

                @Override
                public void onError(String errMsg, int errCode) {
                    super.onError(errMsg, errCode);
                    if (errCode == 52005) {
                        new TipDialog(LoginMainActivity.this).
                                setOkText(getResources().getString(R.string.zb_mobile_update)).
                                setTitle(errMsg).setOnConfirmListener(new TipDialog.OnConfirmListener() {
                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onOK() {
                                CheckUpdateTask.checkUpdate(LoginMainActivity.this);
                            }
                        }).show();
                    }
                }
            }).setTag(this).exe();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (!isPhone){ // 显示三方登录的气泡
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
                T.showShort(LoginMainActivity.this, getString(R.string
                        .zb_phone_num_empty));
            } else {
                if (!TextUtils.isEmpty(mEtSmsText.getText().toString())) {
                    if (AppUtils.isMobileNum(mEtAccountText.getText().toString())) { // 手机号验证码登录不需要先进行绑定的校验,因为该接口未注册的手机号会自动注册通行证
                        LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                        doLogin(mEtAccountText.getText().toString(), mEtSmsText.getText().toString());
                    } else {
                        T.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                    }
                } else {
                    T.showShortNow(this, getString(R.string.zb_input_sms_verication));
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
                sendSmsCaptcha();
            } else {
                if (TextUtils.isEmpty(mEtAccountText.getText().toString())) {
                    T.showShort(LoginMainActivity.this, getString(R.string
                            .zb_phone_num_empty));
                } else {
                    T.showShort(LoginMainActivity.this, getString(R.string.zb_phone_num_error));
                }
            }
        } else if (v.getId() == R.id.tv_password_login) { // 账号密码登录
            Nav.with(LoginMainActivity.this).toPath(RouteManager.ZB_PASSWORD_LOGIN);
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
        if (timer != null) {
            timer.cancel();
        }
        mInputHelper.removeViews();
        if (UserBiz.get().isLoginUser()) {
            setResult(Activity.RESULT_OK);
        }
        super.finish();
        LoginHelper.get().finish(); // 登录结束

    }

    /**
     * 发送验证码,需进行权限校验
     */
    private void sendSmsCaptcha() {
        PermissionManager.get().request(LoginMainActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {

                        ZbPassport.sendCaptcha(mEtAccountText.getText().toString(), "", new ZbResultListener() {
                            @Override
                            public void onSuccess() {
                                startTimeCountDown();
                                //提示短信已发送成功
                                T.showShortNow(LoginMainActivity.this, getString(R
                                        .string.zb_sms_send));
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
                                                    }
                                                }

                                                @Override
                                                public void onRightClick() {
                                                    if (TextUtils.isEmpty(zbGraphicDialog.getEtGraphic().getText().toString())) {
                                                        T.showShort(LoginMainActivity.this, "请先输入图形验证码");
                                                    } else {
                                                        ZbPassport.sendCaptcha(mEtAccountText.getText().toString(), zbGraphicDialog.getEtGraphic().getText().toString(), new ZbResultListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                T.showShort(LoginMainActivity.this, "验证通过");
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
                                                                T.showShort(LoginMainActivity.this, errorMessage);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onRefreshImage() {
                                                    String url = ZbPassport.getGraphicsCode() + "?time="+ SystemClock.elapsedRealtime();
                                                    GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("Cookie", ZbPassport.getZbConfig().getCookie()).build());
                                                    Glide.with(LoginMainActivity.this).load(glideUrl).into(zbGraphicDialog.getIvGrahpic());
                                                }
                                            }));
                                    zbGraphicDialog.show();
                                } else {
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    T.showShort(LoginMainActivity.this, errorMessage);
                                }
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
        ZbPassport.loginCaptcha(phone, captcha, new ZbAuthListener() {

            @Override
            public void onSuccess(AuthInfo loginInfo) {
                if (loginInfo != null) {
                    // 登录认证
                    loginValidate(phone, loginInfo.getCode());
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
     * @param authCode
     */
    private void loginValidate(final String phone, String authCode) {
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
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    // TODO: 2019/3/27 没有phone_number,绑定手机号
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
                    SPHelper.get().put("last_logo", bean.getAccount() == null ? "": bean.getAccount().getImage_url()).commit();
                    finish();
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

}

