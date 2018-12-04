package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daily.news.login.LoginMainActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.baseview.TipDialog;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.ZBLoginValidateTask;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.db.SPHelper;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.domain.base.SkipScoreInterface;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.widget.dialog.LoadingIndicatorDialog;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.ZBUtils;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.core.utils.webjs.WebJsCallBack;
import com.zjrb.passport.Entity.LoginInfo;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbLoginListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 账号密码登录页面(手机号密码登录,个性化账号登录)
 */

public class ZBPasswordLoginActivity extends BaseActivity implements SkipScoreInterface {
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


    private WebJsCallBack callback;
    private LoadingIndicatorDialog loginDialog;

    /**
     * 是否点击了可视密码
     */
    private boolean isClick = false;
    private boolean isFromComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_zbtxz_login);
        getIntentData(getIntent());
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
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
        AppUtils.setEditTextInhibitInputSpace(dtAccountText, false);
        if (!TextUtils.isEmpty(mobile)) {
            dtAccountText.setText(mobile);
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
            dtAccountText.setCursorVisible(true);
            //登录 需要判定手机/邮箱/个性账号
        } else if (view.getId() == R.id.tv_login) {
            if (dtAccountText.getText().toString().isEmpty()) {
                T.showShort(this, getString(R.string.zb_phone_num_empty));
                //纯数字
            } else if (etPasswordText.getText().toString().isEmpty()) {
                T.showShort(this, getString(R.string.zb_phone_password_empty));
            } else if (AppUtils.isNumeric(dtAccountText.getText().toString())) {
                if (AppUtils.isMobileNum(dtAccountText.getText().toString())) { // 手机号登录
                    LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                    // 不需要进行绑定校验
                    doLogin(dtAccountText.getText().toString(), etPasswordText.getText().toString());
//                    checkBind(dtAccountText.getText().toString(), etPasswordText.getText().toString());
                } else {
                    T.showShort(this, getString(R.string.zb_phone_num_error));
                }
                //非纯数字
            } else if (!AppUtils.isNumeric(dtAccountText.getText().toString())) { // 邮箱或个性化登录 需求修改为引导用户使用手机号登录或注册
//                doLogin(dtAccountText.getText().toString(), etPasswordText.getText().toString(), "definition");
                new TipDialog(ZBPasswordLoginActivity.this).setTitle(getResources().getString(R.string.zb_mobile_login_title)).setOkText(getResources().getString(R.string.zb_mobile_login_ok)).setOnConfirmListener(new TipDialog.OnConfirmListener() {
                    @Override
                    public void onCancel() {
                        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                    }

                    @Override
                    public void onOK() {
                        // 跳转到主登录界面
                        Nav.with(getActivity()).toPath(RouteManager.LOGIN_ACTIVITY);
                    }
                }).show();
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
//            AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
//
//            if (bundle == null) {
//                bundle = new Bundle();
//            }
//            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_SMS_TYPE);
//            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
//            Nav.with(this).toPath(RouteManager.LOGIN_ACTIVITY);

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
                    LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
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
     * @param text
     * @param password
     */
    private void doLogin(final String text, String password) {
        if (password == null) {
            T.showShort(ZBPasswordLoginActivity.this, "密码不能为空");
        } else if (password.length() < 6) {
            T.showShort(ZBPasswordLoginActivity.this, "密码长度小于6位");
        } else {
            ZbPassport.login(text, password, new ZbLoginListener() {
                @Override
                public void onSuccess(LoginInfo bean, @Nullable String passData) {
                    if (bean != null) {
                        loginValidate(text, bean.getToken());
                    } else {
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                        T.showShortNow(ZBPasswordLoginActivity.this, getString(R.string.zb_login_error)); // 登录失败
                    }
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    if (errorCode == ErrorCode.ERROR_PHONE_LGOIIN_NEED_RESET) { // 需要重置密码才能登陆的情况
                        new TipDialog(ZBPasswordLoginActivity.this).setTitle(getResources().getString(R.string.zb_mobile_login_title_reset)).setOkText(getResources().getString(R.string.zb_mobile_reset_password)).setOnConfirmListener(new TipDialog.OnConfirmListener() {
                            @Override
                            public void onCancel() {
                                LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                            }

                            @Override
                            public void onOK() {
                                // 跳转到设置密码页面
                                Nav.with(getActivity()).toPath(RouteManager.ZB_RESET_PASSWORD);
                            }
                        }).show();
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
     * @param phone
     * @param token
     */
    private void loginValidate(final String phone, String token) {
        new ZBLoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_login_error));
                new Analytics.AnalyticsBuilder(getContext(), "A0001", "600016", "Login",false)
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
                    new Analytics.AnalyticsBuilder(getContext(), "A0001", "600016", "Login",false)
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
//                    if (TextUtils.equals(type, "phone_number")) {
                    SPHelper.get().put("isPhone", true).commit();
                    SPHelper.get().put("last_login", phone).commit();  // wei_xin, wei_bo, qq
                    SPHelper.get().put("last_logo", bean.getAccount() == null ? "" : bean.getAccount().getImage_url()).commit();
                    ZBUtils.showPointDialog(bean);
                    finish();
                    AppManager.get().finishActivity(LoginMainActivity.class);
//                    }
/*                    else if (TextUtils.equals(type, "definition")) { // 个性化账号 需要判断是否需要进入绑定页面
                        if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) { // 未绑定过,个性化账号进入绑定手机号界面
                            if (bundle == null) {
                                bundle = new Bundle();
                            }
                            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
                            Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_BIND);
                            // 关闭短信验证码页面（可能不存在）
                            // AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                            // TODO: 2018/9/10 需要结束吗
                            finish();
                        }
                    }*/
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,"登录失败");
                }
            }
        }).setTag(this).exe(phone, phone, "phone_number", token);
    }

}
