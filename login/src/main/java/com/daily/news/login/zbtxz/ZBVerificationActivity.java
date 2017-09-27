package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.ZBRegisterValidateTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发送验证码注册账号页面
 * Created by wanglinjie.
 * create time:2017/8/11  下午2:49
 */

public class ZBVerificationActivity extends BaseActivity {

    @BindView(R2.id.dt_account_text)
    TextView dtAccountText;
    @BindView(R2.id.et_sms_code)
    EditText etSmsCode;
    @BindView(R2.id.tv_resend)
    TextView tvResend;
    @BindView(R2.id.bt_register)
    TextView btRegister;
    @BindView(R2.id.tv_notify)
    TextView tvNotify;

    public String mUuid = "";
    public String mAccountID = "";
    public String mPassWord = "";

    //短信验证码
    private TimerManager.TimerTask timerTask;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            mUuid = data.getQueryParameter(Key.UUID);
            mAccountID = data.getQueryParameter(Key.ACCOUNTID);
            mPassWord = data.getQueryParameter(Key.PASSWORD);
        }
    }

    private void initView() {
        btRegister.setText(getString(R.string.zb_confirm));
        tvResend.setText(getString(R.string.zb_login_resend));
        tvNotify.setText(getString(R.string.zb_input_sms_tip));
        String phoneVerificationCode = String.format(getString(R.string.zb_reg_tel),
                mAccountID);
        SpannableStringBuilder sb = createPhoneNumberBoldSpannable(phoneVerificationCode);
        dtAccountText.setText(sb);
    }

    /**
     * 设置短信验证页面中间号码的颜色样式等
     *
     * @param phoneVerificationCode
     * @return
     */
    private SpannableStringBuilder createPhoneNumberBoldSpannable(String phoneVerificationCode) {
        SpannableStringBuilder sb = new SpannableStringBuilder(phoneVerificationCode);
        final StyleSpan boldStyleSpan = new StyleSpan(Typeface.NORMAL);
        int boldStart = phoneVerificationCode.indexOf("+86");
        int boldEnd = phoneVerificationCode.indexOf("收到的");
        sb.setSpan(boldStyleSpan, boldStart, boldEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_register_verification);
        ButterKnife.bind(this);
        getIntentData(getIntent());
        initView();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_register_toolbar)).getView();
    }

    @OnClick({R2.id.bt_register, R2.id.tv_resend})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        //注册账号 获取token
        if (view.getId() == R.id.bt_register) {
            if (!mUuid.equals("") && !etSmsCode.getText().toString().equals("")) {
                regAndLogin(mUuid, etSmsCode.getText().toString());
            }
            //重新发送验证码
        } else {
            getverificationPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.cancel(timerTask);
    }

    /**
     * 开始倒计时
     * 重复访问获取验证码的时间是多少  60s  3次  一天最多5次
     */
    private void startTimeCountDown() {
        tvResend.setEnabled(false);
        //倒计时
        timerTask = new TimerManager.TimerTask(1000, 1000) {
            @Override
            public void run(long count) {
                long value = (60 - count);
                tvResend.setBackgroundResource(R.drawable.border_timer_text_bg);
                tvResend.setText("(" + value + ")" + getString(R.string.zb_login_get_validationcode_again));
                if (value == 0) {
                    TimerManager.cancel(this);
                    tvResend.setEnabled(true);
                    tvResend.setBackground(null);
                    tvResend.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
    }

    /**
     * 获取短信验证码
     */
    private void getverificationPermission() {
        WoaSdk.getSmsCaptcha(ZBVerificationActivity.this,
                mAccountID,
                mPassWord,
                new OnGetSmsCaptchaListener() {
                    @Override
                    public void onFailure(int i, String s) {
                        //关闭验证码
                        TimerManager.cancel(timerTask);
                        T.showShort(ZBVerificationActivity.this, s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        //获取uuid
                        //发送短信成功以后才计时，否则不进行计时
                        startTimeCountDown();
                        mUuid = s;
                    }
                });

    }

    /**
     * 先注册账号再登录
     */
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode) {
        WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

            @Override
            public void onFailure(int i, String s) {
                T.showShort(ZBVerificationActivity.this, s);
            }

            @Override
            public void onSuccess(String s) {
                //注册验证
                if (null == s || s.isEmpty()) {
                    T.showShort(ZBVerificationActivity.this, getString(R.string.zb_reg_error));
                } else {
                    loginZBServer(WoaSdk.getTokenInfo().getSessionId());
                }
            }
        });
    }

    /**
     * 注册验证接口
     *
     * @param sessionId 返回浙报服务器的token
     */
    private void loginZBServer(String sessionId) {
        //注册验证
        new ZBRegisterValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBVerificationActivity.this, getString(R.string.zb_reg_error));
            }

            @Override
            public void onSuccess(@NonNull ZBLoginBean bean) {
                UserBiz userBiz = UserBiz.get();
                userBiz.setZBLoginBean(bean);
                //设置回调数据
                setResult(RESULT_OK);
                onBackPressed();
            }
        }).setTag(this).exe(sessionId);
    }
}
