package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.ZBLoginBean;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.ZBRegisterValidateTask;
import com.zjrb.coreprojectlibrary.api.callback.APIExpandCallBack;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.common.base.toolbar.TopBarFactory;
import com.zjrb.coreprojectlibrary.common.manager.TimerManager;
import com.zjrb.coreprojectlibrary.utils.T;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

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
    @BindView(R2.id.bt_register)
    Button btRegister;
    @BindView(R2.id.tv_notify)
    TextView tvNotify;
    @BindView(R2.id.tv_resend)
    TextView tvResend;


    public String mUuid = "";
    public String mAccountID = "";
    public String mPassWord = "";
    //短信验证码


    private TimerManager.TimerTask timerTask;

    /**
     * @param intent
     * 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            if (intent.hasExtra(Key.UUID)) {
                mUuid = data.getQueryParameter(Key.UUID);
            }
            if (intent.hasExtra(Key.ACCOUNTID)) {
                mAccountID = data.getQueryParameter(Key.ACCOUNTID);
            }
            if (intent.hasExtra(Key.PASSWORD)) {
                mPassWord = data.getQueryParameter(Key.PASSWORD);
            }
        }
    }

    private void initView() {
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
        final StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.NORMAL);
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
        return TopBarFactory.createDefault(view, this, getString(R.string.module_register_toolbar)).getView();
    }

    @OnClick({R2.id.bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.bt_register:
                //注册账号 获取token
                if (!ClickTracker.isDoubleClick()) {
                    if (!mUuid.equals("") && !etSmsCode.getText().toString().equals("") && !mAccountID.isEmpty()) {
                        regAndLogin(mUuid, etSmsCode.getText().toString(), mAccountID);
                    }
                }
                break;
            //重新发送验证码
            case R2.id.tv_resend:
                getverificationPermission();
                break;
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
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode, @NonNull final String phoneNum) {
        WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

            @Override
            public void onFailure(int i, String s) {
                T.showShort(ZBVerificationActivity.this, s);
            }

            @Override
            public void onSuccess(String s) {
                //注册验证
                if (null == s || s.isEmpty()) {
                    T.showShort(ZBVerificationActivity.this, "注册失败");
                } else {
                    loginZBServer(WoaSdk.getTokenInfo().getSessionId(), phoneNum);
                }
            }
        });
    }

    /**
     * 注册验证接口
     *
     * @param sessionId
     * @param phoneNum  登录ZB服务器
     *                  返回浙报服务器的token
     */
    private void loginZBServer(String sessionId, final String phoneNum) {
        //注册验证
        new ZBRegisterValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBVerificationActivity.this, "注册失败");
            }

            @Override
            public void onSuccess(@NonNull ZBLoginBean result) {
                if (result.getResultCode() == 0) {
//                    UserBiz userBiz = UserBiz.get();
//                    userBiz.setAvatar("");
//                    userBiz.setNickName(phoneNum);
//
//                    int size = AppManager.get().getCount();
//                    if (size > 1) {
//                        ZBVerificationActivity.this.finish();
//                        EventBus.getDefault().postSticky(new CloseZBLoginEvent());
//                    } else {
//                        UIUtils.getActivity()
//                                .startActivity(new Intent(ZBVerificationActivity.this, MainActivity.class));
//                    }
                } else {
                    T.showShortNow(ZBVerificationActivity.this, "注册失败");
                }
            }
        }).setTag(this).exe(sessionId, "ZB", "", dtAccountText.getText(), "");
    }

}
