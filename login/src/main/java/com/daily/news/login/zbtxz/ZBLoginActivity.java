package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.LoginActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.domain.AccountBean;
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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.biz.core.global.Key.YiDun.Type;
import cn.daily.news.biz.core.utils.YiDunUtils;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * 浙报通行证登录页
 * created by wanglinjie on 2016/11/23
 */

public class ZBLoginActivity extends BaseActivity implements OnCheckAccountExistListener, SkipScoreInterface {
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
                if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                    LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                    WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), this);
                } else {
                    T.showShort(this, getString(R.string.zb_phone_num_error));
                }
                //非纯数字
            } else if (!AppUtils.isNumeric(dtAccountText.getText().toString())) {
                LoadingDialogUtils.newInstance().getLoginingDialog("正在登录");
                WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), this);
            }
            //重置密码
        } else if (view.getId() == R.id.tv_forget_password_btn) {

            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_RESET_TYPE);
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_SMS_LOGIN);
            //短信验证码登录
        } else if (view.getId() == R.id.tv_verification_btn) {
            AppManager.get().finishActivity(ZBResetPWSmsLogin.class);

            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString(Key.LOGIN_TYPE, Key.Value.LOGIN_SMS_TYPE);
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_SMS_LOGIN);

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


    /**
     * 验证账号是否存在及登录 (注意:个性化账号可能在边锋的体系中存在,但是不在浙江新闻的历史数据库中,所以某个时间节点注册的个性化账号可能不被认为是历史账户)
     *
     * @param b true/false
     * @param s phone number
     */
    @Override
    public void onSuccess(boolean b, @NonNull String s) {
        if (b) {
            WoaSdk.login(this, s, etPasswordText.getText().toString(), new OnLoginListener() {
                @Override
                public void onSuccess(String s, String s1, String s2) {
                    if (!AppUtils.isNumeric(dtAccountText.getText().toString())) { // 个性化账号
                        loginVerification(s, false);
                    } else {
                        loginVerification(s, true);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                    T.showShort(getApplicationContext(), s);
                }
            });
        } else {
            LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
            if (!AppUtils.isNumeric(dtAccountText.getText().toString())) { // 个性化账号
                T.showShort(this, "不支持此类型账号登录，请使用手机号登录注册");
            } else {
                T.showShort(this, "账号不存在或者密码错误，请您重新输入");
            }
        }

    }

    @Override
    public void onFailure(int i, String s) {
        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
        if (!AppUtils.isNumeric(dtAccountText.getText().toString())) { // 个性化账号
            T.showShort(this, "不支持此类型账号登录，请使用手机号登录注册");
        } else {
            T.showShort(this, "账号不存在或者密码错误，请您重新输入");
        }
    }

    private Bundle bundle;

    /**
     * @param s       登录验证
     * @param isPhone true： 手机号登录  false：个性化账号
     */
    private void loginVerification(String s, final boolean isPhone) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                if (!TextUtils.isEmpty(errMsg)) { // 不支持个性化账号登录的提示
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errMsg);
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
                }
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
                    AccountBean account = bean.getAccount();
                    boolean isCertificate = (account != null && !TextUtils.isEmpty(account.getMobile())); // 是否实名认证
                    SensorsDataAPI.sharedInstance().login(bean.getSession().getAccount_id());
                    // 埋点逻辑暂时不动 未认证的个性化账号登录成功,之后可能会放到认证成功之后
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
                        new Analytics.AnalyticsBuilder(ZBLoginActivity.this, null, null, null, false)
                                .setProfile(properties)
                                .build()
                                .send();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isPhone || isCertificate) { // 手机号登录或者实名过的个性化账号登录
                        UserBiz userBiz = UserBiz.get();
                        userBiz.setZBLoginBean(bean);
                        LoginHelper.get().setResult(true); // 设置登录成功
                        ZBUtils.showPointDialog(bean);
                        if (isPhone) { // 手机号登录
                            if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) { // 未实名的,进入实名制页面 后面条件是避免评论登录二次跳转实名界面的
                                if (bundle == null) {
                                    bundle = new Bundle();
                                }
                                bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
                                Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_VERIFICATION);
                                // 关闭短信验证码页面（可能不存在）
                                AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                                finish();
                            } else { // 登录成功，关闭相关页面
                                // 关闭短信验证码页面（可能不存在）
                                AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                                finish();
                                // 关闭登录入口页面
                                AppManager.get().finishActivity(LoginActivity.class);
                            }
                        } else { // 已实名的个性化的情况,登录成功,关闭相关页面
                            // 关闭短信验证码页面（可能不存在）
                            AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                            finish();
                            // 关闭登录入口页面
                            AppManager.get().finishActivity(LoginActivity.class);
                        }
                    } else { // 未实名过的个性化账号登录,实名认证界面不允许跳过,且验证过手机号后,才认为登录成功
                        LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putSerializable(Key.SPECIAL_LOGIN_KEY, bean);
                        bundle.putBoolean(Key.IS_SPECIAL_LOGIN, true);
                        Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_VERIFICATION);
                        // 关闭短信验证码页面（可能不存在）
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        finish();
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false, getString(R.string.zb_login_error));
//                    T.showShortNow(ZBLoginActivity.this, getString(R.string.zb_login_error));
                }
            }
        }).setTag(this).exe(s, "BIANFENG", dtAccountText.getText(), dtAccountText.getText(),
                dtAccountText.getText(), 0, YiDunUtils.getToken(Type.REG));
    }

}
