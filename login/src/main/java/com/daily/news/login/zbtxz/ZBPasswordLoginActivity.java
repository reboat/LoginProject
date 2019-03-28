package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.daily.news.login.LoginMainActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.baseview.TipDialog;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.ZBLoginValidateTask;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.utils.AppManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.Entity.AuthInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbAuthListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.analytics.AnalyticsManager;
import cn.daily.news.biz.core.DailyActivity;
import cn.daily.news.biz.core.UserBiz;
import cn.daily.news.biz.core.constant.IKey;
import cn.daily.news.biz.core.model.AccountBean;
import cn.daily.news.biz.core.model.SkipScoreInterface;
import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.nav.Nav;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.task.UploadCidTask;
import cn.daily.news.biz.core.ui.dialog.ZbGraphicDialog;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;
import cn.daily.news.biz.core.utils.LoadingDialogUtils;
import cn.daily.news.biz.core.utils.LoginHelper;
import cn.daily.news.biz.core.utils.MultiInputHelper;
import cn.daily.news.biz.core.utils.RouteManager;
import cn.daily.news.biz.core.utils.YiDunUtils;
import cn.daily.news.biz.core.utils.ZBUtils;

import static cn.daily.news.biz.core.UserBiz.KEY_CID;
import static cn.daily.news.biz.core.UserBiz.SP_NAME;
import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 账号密码登录页面(手机号密码登录,6.0版本不再支持个性化账号登录)
 */

public class ZBPasswordLoginActivity extends DailyActivity implements SkipScoreInterface {
    @BindView(R2.id.dt_account_text)
    EditText etAccountText;
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
    private MultiInputHelper mInputHelper;

    /**
     * 是否点击了可视密码
     */
    private boolean isClick = false;
    private boolean isFromComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_password_login);
        getIntentData(getIntent());
        ButterKnife.bind(this);
        initView();
        //创建输入监听辅助类，传入提交按钮view
        mInputHelper = new MultiInputHelper(tvLogin);
        //添加需要监听的textview
        mInputHelper.addViews(etAccountText, etPasswordText);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return BIZTopBarFactory.createDefaultForLogin(view, this).getView();
    }

    private String mobile;

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

    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(etPasswordText, true);
        AppUtils.setEditTextInhibitInputSpace(etAccountText, false);
        if (!TextUtils.isEmpty(mobile)) {
            etAccountText.setText(mobile);
        }
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        tvLogin.setText(getString(R.string.zb_login));
        tvVerification.setText(getString(R.string.zb_login_sms));
        tvForgetPassword.setText(getString(R.string.zb_forget_password));
    }

    @OnClick({R2.id.dt_account_text, R2.id.tv_login,
            R2.id.tv_forget_password_btn, R2.id.verification_code_see_btn,
            R2.id.tv_verification_btn})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        if (view.getId() == R.id.dt_account_text) {
            etAccountText.setCursorVisible(true);
            //登录 需要判定手机/邮箱/个性账号
        } else if (view.getId() == R.id.tv_login) {
            if (etAccountText.getText().toString().isEmpty()) {
                T.showShort(this, getString(R.string.zb_phone_num_empty));
                //纯数字
            } else if (etPasswordText.getText().toString().isEmpty()) {
                T.showShort(this, getString(R.string.zb_phone_password_empty));
            } else if (AppUtils.isNumeric(etAccountText.getText().toString())) {
                if (AppUtils.isMobileNum(etAccountText.getText().toString())) { // 手机号登录
                    LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                    // 不需要进行绑定校验
                    doLogin(etAccountText.getText().toString(), etPasswordText.getText().toString());
//                    checkBind(etAccountText.getText().toString(), etPasswordText.getText().toString());
                } else {
                    T.showShort(this, getString(R.string.zb_phone_num_error));
                }
                //非纯数字
            }
            //重置密码
        } else if (view.getId() == R.id.tv_forget_password_btn) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE);
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_RESET_PASSWORD);
            //短信验证码登录
        } else if (view.getId() == R.id.tv_verification_btn) {
            finish();
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE);
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.LOGIN_ACTIVITY);
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

   /* private void checkBind(final String phone, final String password) {
        ZbPassport.checkBindState(phone, new ZbCheckPhoneListener() {
            @Override
            public void onSuccess(boolean isBind) {
                if (isBind) {
                    doLogin(phone, password);
                } else {
                    // 提示注册?
                    LoadingDialogUtils.newInstance().NoText();
                    T.showShort(ZBPasswordLoginActivity.this, "账号不存在或者密码错误，请您重新输入");                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
            }
        });
    }*/


    private Bundle bundle;

    /**
     * 登录
     *
     * @param text
     * @param password
     */
    private void doLogin(final String text, final String password) {
        if (password == null) {
            T.showShort(ZBPasswordLoginActivity.this, "密码不能为空");
        } else if (password.length() < 6) {
            T.showShort(ZBPasswordLoginActivity.this, "密码长度小于6位");
        } else {
            ZbPassport.loginCustom(text, password, "", new ZbAuthListener() {
                @Override
                public void onSuccess(AuthInfo info) {
                    if (info != null) {
                        loginValidate(text, info.getCode());
                    } else {
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                        T.showShortNow(ZBPasswordLoginActivity.this, getString(R.string.zb_login_error)); // 登录失败
                    }
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    if (errorCode == ErrorCode.ERROR_NEED_RESET_PASSWORD) { // 需要重置密码才能登陆的情况
                        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                        new TipDialog(ZBPasswordLoginActivity.this).setTitle(getResources().getString(R.string.zb_mobile_login_title_reset)).setOkText(getResources().getString(R.string.zb_mobile_reset_password)).setOnConfirmListener(new TipDialog.OnConfirmListener() {
                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onOK() {
                                // 跳转到设置密码页面
                                Nav.with(getActivity()).toPath(RouteManager.ZB_RESET_PASSWORD);
                            }
                        }).show();
                    } else if (errorCode == ErrorCode.ERROR_NEED_GRRPHICS) {
                        final ZbGraphicDialog zbGraphicDialog = new ZbGraphicDialog(ZBPasswordLoginActivity.this);
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
                                            T.showShort(ZBPasswordLoginActivity.this, "请先输入图形验证码");
                                        } else {
                                            ZbPassport.loginCustom(text, password, zbGraphicDialog.getEtGraphic().getText().toString(), new ZbAuthListener() {
                                                @Override
                                                public void onSuccess(AuthInfo info) {
                                                    if (info != null) {
                                                        loginValidate(text, info.getCode());
                                                    } else {
                                                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                                                        T.showShortNow(ZBPasswordLoginActivity.this, getString(R.string.zb_login_error)); // 登录失败
                                                    }
                                                }

                                                @Override
                                                public void onFailure(int errorCode, String errorMessage) {
                                                    T.showShort(ZBPasswordLoginActivity.this, errorMessage);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onRefreshImage() {
                                        String url = ZbPassport.getGraphicsCode() + "?time=" + SystemClock.elapsedRealtime();
                                        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("Cookie", ZbPassport.getZbConfig().getCookie()).build());
                                        Glide.with(ZBPasswordLoginActivity.this).load(glideUrl).into(zbGraphicDialog.getIvGrahpic());
                                    }
                                }));
                        zbGraphicDialog.show();
                    } else {
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                        T.showShortNow(ZBPasswordLoginActivity.this, errorMessage);
                    }
                }
            });
        }
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
                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    if (userBiz.isCertification()) {
                        //新华智云设置userID
                        AnalyticsManager.setAccountId(UserBiz.get().getAccountID());
                        AccountBean account = bean.getAccount();
                        SensorsDataAPI.sharedInstance().login(bean.getSession().getAccount_id());
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
                        try {
                            JSONObject properties = new JSONObject();
                            properties.put("userID", bean.getSession().getAccount_id());
                            properties.put("mobilePhone", bean.getAccount().getMobile());
                            new Analytics.AnalyticsBuilder(ZBPasswordLoginActivity.this, null, null, null, false)
                                    .setProfile(properties)
                                    .build()
                                    .send();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LoginHelper.get().setResult(true); // 设置登录成功
                        SPHelper.get().put("isPhone", true).commit();
                        SPHelper.get().put("last_login", phone).commit();  // wei_xin, wei_bo, qq
                        ZBUtils.showPointDialog(bean);
                        String clientId = SPHelper.get(SP_NAME).get(KEY_CID, "");
                        if (!TextUtils.isEmpty(clientId)) {
                            new UploadCidTask(null).exe(clientId);
                        }
                        finish();
                        AppManager.get().finishActivity(LoginMainActivity.class);
                    } else {
                        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                        Nav.with(ZBPasswordLoginActivity.this).toPath(RouteManager.ZB_MOBILE_BIND);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, "登录失败");
                }
            }
        }).setTag(this).exe(phone, phone,
                "phone_number", authCode, YiDunUtils.getToken(cn.daily.news.biz.core.constant.Key.YiDun.Type.REG));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInputHelper.removeViews();
    }
}
