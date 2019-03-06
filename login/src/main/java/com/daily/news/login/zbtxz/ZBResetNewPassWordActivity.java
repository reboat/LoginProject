package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.ZBUtils;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.listener.ZbFindPasswordListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 重置密码输入新密码界面
 */
public class ZBResetNewPassWordActivity extends BaseActivity implements SkipScoreInterface {

    @BindView(R2.id.tv_tip)
    TextView tvTip;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.iv_see)
    ImageView ivSee;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;

    private boolean isClick = false;
    private boolean isCommentActivity = false;
    String phoneNum;
    String sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_reset_password_confirm);
        ButterKnife.bind(this);
        initView();
        getIntentData(getIntent());
    }

    /**
     * @param intent 获取传递到重置密码页面的数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {

            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }

            phoneNum = intent.getStringExtra("phoneNum");
            sms = intent.getStringExtra("sms");
        }

    }

    /**
     * 设置密码重置文案初始化
     */
    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(etPasswordText, true);
        ivSee.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        btConfirm.setText(getString(R.string.zb_confirm));
        tvTip.setText(getString(R.string.zb_set_password_tip));
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
    }

    @OnClick({R2.id.bt_confirm, R2.id.iv_see})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        if (view.getId() == R.id.bt_confirm) {
            findPassword();
        } else {
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
     * 重置密码
     */
    private void findPassword() {
        if (etPasswordText.getText().length() < 6) {
            T.showShortNow(ZBResetNewPassWordActivity.this, "密码长度不可小于6位");
            return;
        }
        LoadingDialogUtils.newInstance().getLoginingDialog("正在重置");
        ZbPassport.findPassword(phoneNum, sms, etPasswordText.getText().toString(), new ZbFindPasswordListener() {
            @Override
            public void onSuccess(@Nullable String passData) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                // 跳转到账号密码登录页面,手机号自动填充,密码清空
                finish();
                // 关闭 密码登录页面
                AppManager.get().finishActivity(ZBResetPasswordActivity.class);
                AppManager.get().finishActivity(ZBPasswordLoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mobile", phoneNum);
                Nav.with(ZBResetNewPassWordActivity.this).setExtras(bundle).toPath(RouteManager.ZB_PASSWORD_LOGIN);
//                ZbPassport.login(phoneNum, etPasswordText.getText().toString(), new ZbLoginListener() {
//                    @Override
//                    public void onSuccess(LoginInfo bean, @Nullable String passData) {
//                        if (bean != null) {
//                            loginValidate(phoneNum, bean.getToken());
//                        } else {
//                            LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_login_error));
//                            T.showShortNow(ZBResetNewPassWordActivity.this, getString(R.string.zb_login_error)); // 登录失败
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int errorCode, String errorMessage) {
//                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_login_error));
//                        T.showShortNow(ZBResetNewPassWordActivity.this, errorMessage);
//                    }
//                });
//                T.showShort(ZBResetNewPassWordActivity.this, "找回密码成功,重新进行登录");
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errorMessage);

            }
        });
    }

    private Bundle bundle;

    /**
     * 登录认证
     */
    private void loginValidate(final String phone, String authCode) {
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
                    // 关闭 密码登录页面
                    AppManager.get().finishActivity(ZBResetPasswordActivity.class);
                    AppManager.get().finishActivity(ZBPasswordLoginActivity.class);
                    // 关闭登录入口页
                    AppManager.get().finishActivity(LoginMainActivity.class);
//                    }
/*                    else if (TextUtils.equals(type, "definition")) { // 个性化账号 需要判断是否需要进入绑定页面
                        if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) { // 未绑定过,个性化账号进入绑定手机号界面
                            if (bundle == null) {
                                bundle = new Bundle();
                            }
                            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
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
        }).setTag(this).exe(phone, phone, "phone_number", authCode);
        /*new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false,errMsg);
                new Analytics.AnalyticsBuilder(getContext(), "A0001", "600017", "AppTabClick", false)
                        .setEvenName("浙报通行证，在设置新密码页面，重置密码")
                        .setPageType("密码重置页")
                        .setEventDetail("重置密码")
                        .setIscuccesee(false)
                        .pageType("密码重置页")
                        .clickTabName("重置密码")
                        .build()
                        .send();
//                T.showShortNow(ZBResetNewPassWord.this, errMsg);
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                    new Analytics.AnalyticsBuilder(getContext(), "A0001", "600017", "AppTabClick", false)
                            .setEvenName("浙报通行证，在设置新密码页面，重置密码")
                            .setPageType("密码重置页")
                            .setEventDetail("重置密码")
                            .setIscuccesee(true)
                            .pageType("密码重置页")
                            .clickTabName("重置密码")
                            .build()
                            .send();
                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功
                    ZBUtils.showPointDialog(bean);
                    if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) {// 进入实名制页面
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                        Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_VERIFICATION);
                        // 关闭 验证码页面／短信验证码登录页
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);

                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                    } else {
                        // 关闭 验证码页面／短信验证码登录页
                        AppManager.get().finishActivity(ZBResetPWSmsLogin.class);
                        // 关闭 密码登录页面
                        AppManager.get().finishActivity(ZBLoginActivity.class);
                        // 关闭本页面 （短信验证码登录页面）
                        finish();
                        // 关闭登录入口页
                        AppManager.get().finishActivity(LoginMainActivity.class);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,"密码重置失败");
//                    T.showShortNow(ZBResetNewPassWord.this, "密码重置失败");
                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", mAccountID, mAccountID, mAccountID,0);*/

    }
}
