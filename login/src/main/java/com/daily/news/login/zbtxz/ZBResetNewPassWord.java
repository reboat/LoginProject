package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.OnResetPasswordListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.LoginActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.api.task.UploadCidTask;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;
import cn.daily.news.biz.core.global.Key.YiDun.Type;
import cn.daily.news.biz.core.utils.YiDunUtils;

import static com.zjrb.core.common.biz.UserBiz.KEY_CID;
import static com.zjrb.core.common.biz.UserBiz.SP_NAME;
import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * 重置密码页面
 * <p>
 * Created by wanglinjie.
 * create time:2017/8/11  下午4:56
 */
public class ZBResetNewPassWord extends BaseActivity implements SkipScoreInterface {

    @BindView(R2.id.tv_tip)
    TextView tvTip;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.iv_see)
    ImageView ivSee;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;

    public String mUuid = "";
    public String mAccountID = "";
    private boolean isClick = false;
    private boolean isCommentActivity = false;

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
            if (intent.hasExtra(Key.UUID)) {
                mUuid = intent.getStringExtra(Key.UUID);
            }

            if (intent.hasExtra(Key.ACCOUNTID)) {
                mAccountID = intent.getStringExtra(Key.ACCOUNTID);
            }

            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
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
            T.showShortNow(ZBResetNewPassWord.this, "密码长度不可小于6位");
            return;
        }
        LoadingDialogUtils.newInstance().getLoginingDialog("正在重置");
        WoaSdk.resetPassword(this,
                mAccountID,
                mUuid,
                etPasswordText.getText().toString(),
                new OnResetPasswordListener() {

                    @Override
                    public void onFailure(int i, String s) {
                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false,s);
//                        T.showShort(ZBResetNewPassWord.this, s);
                    }

                    @Override
                    public void onSuccess() {
                        WoaSdk.login(ZBResetNewPassWord.this,
                                mAccountID,
                                etPasswordText.getText().toString(),
                                new OnLoginListener() {

                                    @Override
                                    public void onFailure(int i, String s) {
                                        LoadingDialogUtils.newInstance().dismissLoadingDialog(false,s);
//                                        T.showShort(ZBResetNewPassWord.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s, String s1, String s2) {
                                        regGetToken(s);
                                    }
                                });
                    }
                });
    }

    private Bundle bundle;

    /**
     * 重置密码后需要进行登录
     */
    private void regGetToken(String sessionId) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errMsg);
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
                        AppManager.get().finishActivity(LoginActivity.class);
                    }
                    String clientId = SPHelper.get(SP_NAME).get(KEY_CID, "");
                    if (!TextUtils.isEmpty(clientId)) {
                        new UploadCidTask(null).exe(clientId);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,"密码重置失败");
//                    T.showShortNow(ZBResetNewPassWord.this, "密码重置失败");
                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", mAccountID, mAccountID, mAccountID,0,YiDunUtils.getToken(Type.REG));

    }
}
