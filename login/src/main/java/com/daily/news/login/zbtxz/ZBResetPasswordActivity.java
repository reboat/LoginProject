package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbResultListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 忘记密码界面
 */
public class ZBResetPasswordActivity extends BaseActivity {

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
     * 验证码定时器
     */
    private TimerManager.TimerTask timerTask;

    private boolean isCommentActivity = false;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
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

        if (mTvTitle.getVisibility() == View.GONE) {
            mTvTitle.setVisibility(View.VISIBLE);
        }
        tvChangeLoginType.setText(getString(R.string.zb_input_sms_tip));
        btConfirm.setText("确定");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.cancel(timerTask);
    }


    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login))
                .getView();
    }

    @OnClick({R2.id.tv_sms_verification, R2.id.bt_confirm})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        //获取验证码需要先输入手机号
        if (view.getId() == R.id.tv_sms_verification) {
            if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                getverificationPermission(dtAccountText.getText().toString());
            } else {
                if (TextUtils.isEmpty(dtAccountText.getText().toString())) {
                    T.showShort(ZBResetPasswordActivity.this, getString(R.string
                            .zb_phone_num_empty));
                } else {
                    T.showShort(ZBResetPasswordActivity.this, getString(R.string.zb_phone_num_error));
                }
            }
        } else if (view.getId() == R.id.bt_confirm) {
            if (etSmsText.getText() != null && !TextUtils.isEmpty(etSmsText.getText().toString())) {
                if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                    doNext(dtAccountText.getText().toString(), etSmsText.getText().toString());
                } else {
                    if (TextUtils.isEmpty(dtAccountText.getText().toString())) {
                        T.showShort(ZBResetPasswordActivity.this, getString(R.string
                                .zb_phone_num_empty));
                    } else {
                        T.showShort(ZBResetPasswordActivity.this, getString(R.string.zb_phone_num_error));
                    }
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        }

    }

    public void doNext(final String phoneNum, final String sms) {
//        ZbPassport.verifyCaptcha(ZbConstants.Sms.FIND, phoneNum, sms, new ZbCaptchaVerifyListener() {
//            @Override
//            public void onSuccess(boolean isValid, @Nullable String passData) {
//                if (isValid) {
//                    LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
//                    if (bundle == null) {
//                        bundle = new Bundle();
//                    }
//                    bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
//                    bundle.putString("phoneNum", phoneNum);
//                    bundle.putString("sms", sms);
//                    Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager
//                            .ZB_RESET_NEW_PASSWORD);
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(int errorCode, String errorMessage) {
//                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errorMessage);
//
//            }
//        });
        ZbPassport.checkCaptcha(phoneNum, sms, new ZbResultListener() {
            @Override
            public void onSuccess() {
                LoadingDialogUtils.newInstance().dismissLoadingDialogNoText();
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                bundle.putString("phoneNum", phoneNum);
                bundle.putString("sms", sms);
                Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager
                        .ZB_RESET_NEW_PASSWORD);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false, errorMessage);
            }
        });

    }


    private Bundle bundle;

    /**
     * 获取短信验证权限
     */
    private void getverificationPermission(final String phoneNum) {
        PermissionManager.get().request(ZBResetPasswordActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
//                        ZbPassport.sendCaptcha(ZbConstants.Sms.FIND, phoneNum, new ZbCaptchaSendListener() {
//                            @Override
//                            public void onSuccess(@Nullable String passData) {
//                                startTimeCountDown();
//                                //提示短信已发送成功
//                                T.showShortNow(ZBResetPasswordActivity.this, getString(R
//                                        .string.zb_sms_send));
//                            }
//
//                            @Override
//                            public void onFailure(int errorCode, String errorMessage) {
//                                TimerManager.cancel(timerTask);
//                                T.showShort(ZBResetPasswordActivity.this, errorMessage);
//                            }
//                        });
                        ZbPassport.sendCaptcha(phoneNum, "", new ZbResultListener() {
                            @Override
                            public void onSuccess() {
                                startTimeCountDown();
                                //提示短信已发送成功
                                T.showShortNow(ZBResetPasswordActivity.this, getString(R
                                        .string.zb_sms_send));
                            }

                            @Override
                            public void onFailure(int errorCode, String errorMessage) {
                                // TODO: 2019/3/7 图形验证码操作
                                if (errorCode == ErrorCode.ERROR_NEED_GRRPHICS) {

                                } else {
                                    TimerManager.cancel(timerTask);
                                    T.showShort(ZBResetPasswordActivity.this, errorMessage);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBResetPasswordActivity.this, getString(R.string
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
        timerTask = new TimerManager.TimerTask(1000, 1000) {
            @Override
            public void run(long count) {
                long value = (60 - count);
                tvTerification.setBackgroundResource(R.drawable.border_timer_text_bg);
                tvTerification.setTextColor(getResources().getColor(R.color.tc_999999));
                tvTerification.setText("(" + value + ")" + getString(R.string
                        .zb_login_get_validationcode_again));
                if (value == 0) {
                    TimerManager.cancel(this);
                    tvTerification.setEnabled(true);
                    //TODO  WLJ 夜间模式
                    tvTerification.setBackgroundResource(R.drawable
                            .module_login_bg_sms_verification);
                    tvTerification.setTextColor(getResources().getColor(R.color.tc_f44b50));
                    tvTerification.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
    }

}
