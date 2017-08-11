package com.daily.news.login.zbtxz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
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

@Route(path = "/module/login/ZBVerificationActivity")
public class ZBVerificationActivity extends BaseActivity {

    @BindView(R2.id.dt_account_text)
    TextView dtAccountText;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.bt_register)
    Button btRegister;
    @BindView(R2.id.tv_notify)
    TextView tvNotify;
    @BindView(R2.id.tv_resend)
    TextView tvResend;


    @Autowired(name = Key.UUID)
    public String mUuid;
    @Autowired(name = Key.SMSCODE)
    public String mSmsCode;
    @Autowired(name = Key.ACCOUNTID)
    public String mAccountID;
    @Autowired(name = Key.PASSWORD)
    public String mPassWord;


    private TimerManager.TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.module_zbtxz_register_verification);
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.bt_register:
                //注册账号 获取token
                if (!ClickTracker.isDoubleClick()) {
                    if (!mUuid.equals("") && !mSmsCode.equals("") && !mAccountID.isEmpty()) {
                        regAndLogin(mUuid, mSmsCode, mAccountID);
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
        startTimeCountDown();
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
//        new WoaValidateTask(new APIExpandCallBack<ZBLoginBean>() {
//            @Override
//            public void onError(String errMsg, int errCode) {
//                showShortToast("注册失败");
//            }
//
//            @Override
//            public void onSuccess(@NonNull ZBLoginBean result) {
//                if (result.getResultCode() == 0) {
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
//                } else {
//                    showShortToast("注册失败");
//                }
//            }
//        }).setTag(this).exe(sessionId);
    }

}
