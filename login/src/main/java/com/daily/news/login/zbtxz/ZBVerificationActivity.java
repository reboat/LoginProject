package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.LoginActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.daily.news.login.utils.YiDunUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.LoadingDialogUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.analytics.Analytics;

import static com.zjrb.core.utils.UIUtils.getContext;

/**
 * 注册页面 - 验证码确认页面
 * <p>
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

    /**
     * 边锋uid
     */
    public String mUuid = "";
    /**
     * 账号名称
     */
    public String mAccountID = "";
    /**
     * 密码
     */
    public String mPassWord = "";

    /**
     * 短信验证码定时器
     */
    private TimerManager.TimerTask timerTask;

    private boolean isCommentActivity = false;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(Key.UUID)) {
                mUuid = intent.getStringExtra(Key.UUID);
            }
            if (intent.hasExtra(Key.ACCOUNTID)) {
                mAccountID = intent.getStringExtra(Key.ACCOUNTID);
            }
            if (intent.hasExtra(Key.PASSWORD)) {
                mPassWord = intent.getStringExtra(Key.PASSWORD);
            }
            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
        }

    }

    /**
     * 文案
     */
    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(etSmsCode, false);
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
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_register_toolbar))
                .getView();
    }

    @OnClick({R2.id.bt_register, R2.id.tv_resend})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        //注册账号 获取token
        if (view.getId() == R.id.bt_register) {
            if (!TextUtils.isEmpty(mUuid)) {
                if (TextUtils.isEmpty(etSmsCode.getText().toString())) {
                    T.showShortNow(ZBVerificationActivity.this, "请输入验证码");
                } else {
                    //防止高并发服务端来不及处理，客户端做容错
                    LoadingDialogUtils.newInstance().getLoginingDialog("正在注册");
                    regAndLogin(mUuid, etSmsCode.getText().toString());
                }
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
                tvResend.setText("(" + value + ")" + getString(R.string
                        .zb_login_get_validationcode_again));
                if (!ThemeMode.isNightMode()) {
                    tvResend.setTextColor(getResources().getColor(R.color._999999));
                } else {
                    tvResend.setTextColor(getResources().getColor(R.color._7a7b7d));
                }
                if (value == 0) {
                    TimerManager.cancel(this);
                    tvResend.setEnabled(true);
                    tvResend.setBackground(null);
                    tvResend.setText(getString(R.string
                            .zb_login_resend));
                    if (!ThemeMode.isNightMode()) {
                        tvResend.setTextColor(getResources().getColor(R.color._f44b50));
                    } else {
                        tvResend.setTextColor(getResources().getColor(R.color._8e3636));
                    }
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
     * 先边锋注册账号再登录
     */
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode) {
        WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

            @Override
            public void onFailure(int i, String s) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false,s);
//                T.showShort(ZBVerificationActivity.this, s);
            }

            @Override
            public void onSuccess(String s) {
                //注册验证
                if (null == s || s.isEmpty()) {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_reg_error));
                    T.showShort(ZBVerificationActivity.this, getString(R.string.zb_reg_error));
                } else {
                    loginZBServer(WoaSdk.getTokenInfo().getSessionId());
                }
            }
        });
    }

    private Bundle bundle;

    /**
     * 注册验证接口
     *
     * @param sessionId 返回浙报服务器的token
     */
    private void loginZBServer(String sessionId) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_reg_error));
//                T.showShortNow(getActivity(), getString(R.string.zb_reg_error));
            }

            @Override
            public void onSuccess(ZBLoginBean bean) {
                if (bean != null) {
                    //注册成功
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(true);
                    SensorsDataAPI.sharedInstance().login(bean.getSession().getAccount_id());
                    new Analytics.AnalyticsBuilder(getContext(), "A0000", "A0000", "SignUp",false)
                            .setEvenName("注册成功")
                            .setPageType("注册页")
                            .setIscuccesee(true)
                            .pageType("注册页")
                            .signUpType("手机号")
                            .mobilePhone(bean.getAccount().getMobile())
                            .userID(bean.getSession().getAccount_id())
                            .build()
                            .send();

                    try {
                        JSONObject properties = new JSONObject();
                        properties.put("userID", bean.getSession().getAccount_id());
                        properties.put("mobilePhone", bean.getAccount().getMobile());
                        SensorsDataAPI.sharedInstance().profileSet(properties);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    UserBiz userBiz = UserBiz.get();
                    userBiz.setZBLoginBean(bean);
                    LoginHelper.get().setResult(true); // 设置登录成功
                    if (!userBiz.isCertification() && !LoginHelper.get().filterCommentLogin()) {
//                    if (!userBiz.isCertification()) { // 进入实名制页面
                        if (bundle == null) {
                            bundle = new Bundle();
                        }
                        bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                        Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_MOBILE_VERIFICATION);
                        // 关闭上个注册页面
                        AppManager.get().finishActivity(ZBRegisterActivity.class);
                        // 关闭本页面
                        finish();
                    } else {
                        // 关闭上个注册页面
                        AppManager.get().finishActivity(ZBRegisterActivity.class);
                        // 关闭本页面
                        finish();
                        // 登录入口页
                        AppManager.get().finishActivity(LoginActivity.class);
                    }
                } else {
                    LoadingDialogUtils.newInstance().dismissLoadingDialog(false,getString(R.string.zb_reg_error));
//                    T.showShortNow(getActivity(), getString(R.string.zb_reg_error));
                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", mAccountID, mAccountID,
                mAccountID, 1,YiDunUtils.getToken());
    }
}
