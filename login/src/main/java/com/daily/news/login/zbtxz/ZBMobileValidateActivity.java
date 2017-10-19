package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.daily.news.login.LoginActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.GetSmsCodeTask;
import com.daily.news.login.task.MobileValidateTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.base.toolbar.holder.DefaultTopBarHolder2;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙报通行证实名制手机验证页面
 * <p>
 * created by wanglinjie on 2016/11/23
 */
public class ZBMobileValidateActivity extends BaseActivity {
    @BindView(R2.id.dt_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText etSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView tvTerification;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;
    @BindView(R2.id.iv_top_jump)
    TextView mTvJump;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;

    private TimerManager.TimerTask timerTask;

    /**
     * 是否来自评论登录
     */
    private boolean isCommentLogin = false;

    /**
     * 是否来自于评论的实名制
     */
    private boolean isCommentActivity = false;


    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(Key.IS_COMMENT_LOGIN)) {
                isCommentLogin = intent.getBooleanExtra(Key.IS_COMMENT_LOGIN, false);
            }
            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        setContentView(R.layout.module_zbtxz_mobile_validate);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化标题
     */
    private void initView() {
        if (!isCommentActivity) {
            mTvJump.setVisibility(View.VISIBLE);
            mTvJump.setText(getString(R.string.zb_mobile_jump));
        } else {
            mTvJump.setVisibility(View.GONE);
        }
        mTvTitle.setText(getString(R.string.zb_mobile_valideta_tip));
        btConfirm.setText(getString(R.string.zb_mobile_submit));
        tvTerification.setText(getString(R.string.zb_sms_verication));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.cancel(timerTask);
    }


    private DefaultTopBarHolder2 topBarHolder;

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        topBarHolder = TopBarFactory.createDefault2(view, this);
        topBarHolder.setTopBarText(getString(R.string.zb_mobile_contact));
        //来自评论实名制  不显示跳过
        if (isCommentLogin) {
            topBarHolder.setViewVisible(topBarHolder.getRightText(), View.GONE);
        }
        return topBarHolder.getView();
    }

    @OnClick({R2.id.tv_sms_verification, R2.id.bt_confirm, R2.id.iv_top_jump})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        //获取验证码需要先输入手机号
        if (view.getId() == R.id.tv_sms_verification) {
            if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                getValidateCode(dtAccountText.getText().toString());
            } else {
                if (dtAccountText.getText().toString().equals("")) {
                    T.showShort(ZBMobileValidateActivity.this, getString(R.string
                            .zb_phone_num_inout_error));
                } else {
                    T.showShort(ZBMobileValidateActivity.this, getString(R.string
                            .zb_phone_num_error));
                }
            }
            //提交
        } else if (view.getId() == R.id.bt_confirm) {
            //验证码
            if (etSmsText.getText() != null && !TextUtils.isEmpty(etSmsText.getText().toString())) {
                //进入账号密码登录页面
                if (dtAccountText.getText() != null && !TextUtils.isEmpty(dtAccountText.getText()
                        .toString())) {
                    mobileValidate(dtAccountText.getText().toString(), etSmsText.getText().toString());
                } else {
                    T.showShort(ZBMobileValidateActivity.this, getString(R.string
                            .zb_phone_num_inout_error));
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        } else {
            finish();
        }

    }


    /**
     * 提交验证码
     *
     * @param mobile
     * @param smsCode 登录ZB服务器
     *                返回浙报服务器的token
     */
    private void mobileValidate(final String mobile, final String smsCode) {
        //短信验证码验证
        new MobileValidateTask(new APIExpandCallBack<Void>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBMobileValidateActivity.this, errMsg);
            }

            @Override
            public void onSuccess(Void bean) {
                //设置回调数据
                UserBiz.get().getAccount().setMobile(mobile);
                setResult(RESULT_OK);
                finish();
            }
        }).setTag(this).exe(mobile, smsCode);
    }

    /**
     * 获取短信验证码
     *
     * @param mobile 手机号
     */
    private void getValidateCode(final String mobile) {
        PermissionManager.get().request(ZBMobileValidateActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        new GetSmsCodeTask(new APIExpandCallBack<Void>() {
                            @Override
                            public void onError(String errMsg, int errCode) {
                                T.showShortNow(ZBMobileValidateActivity.this, errMsg);
                            }

                            @Override
                            public void onSuccess(Void bean) {
                                startTimeCountDown();
                                T.showShortNow(getActivity(), getString(R.string
                                        .zb_sms_send));
                            }
                        }).setTag(this).exe(mobile);
                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBMobileValidateActivity.this, getString(R.string
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
                    tvTerification.setBackground(null);
                    tvTerification.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
    }

    @Override
    public void finish() {
        super.finish();
        AppManager.get().finishActivity(LoginActivity.class);
    }
}
