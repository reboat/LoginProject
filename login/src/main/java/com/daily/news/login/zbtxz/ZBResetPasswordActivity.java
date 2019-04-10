package com.daily.news.login.zbtxz;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.util.LoginUtil;
import com.zjrb.core.permission.IPermissionCallBack;
import com.zjrb.core.permission.Permission;
import com.zjrb.core.permission.PermissionManager;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ErrorCode;
import com.zjrb.passport.listener.ZbResultListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.biz.core.DailyActivity;
import cn.daily.news.biz.core.nav.Nav;
import cn.daily.news.biz.core.ui.dialog.ZbGraphicDialog;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;
import cn.daily.news.biz.core.utils.MultiInputHelper;
import cn.daily.news.biz.core.utils.RouteManager;

/**
 * Date: 2018/8/15
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 忘记密码界面
 */
public class ZBResetPasswordActivity extends DailyActivity {

    @BindView(R2.id.et_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText etSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView tvTerification;
    @BindView(R2.id.tv_change_login_type)
    TextView tvChangeLoginType;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;


    /**
     * 验证码定时器
     */
    private CountDownTimer timer;
    private MultiInputHelper mInputHelper;


//    private boolean isCommentActivity = false;

//    /**
//     * @param intent 获取intent数据
//     */
//    private void getIntentData(Intent intent) {
//        if (intent != null) {
//            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
//                isCommentActivity = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_reset_password);
        ButterKnife.bind(this);
//        getIntentData(getIntent());
        initView();
        //创建输入监听辅助类，传入提交按钮view
        mInputHelper = new MultiInputHelper(btConfirm);
        //添加需要监听的textview
        mInputHelper.addViews(dtAccountText, etSmsText);
    }

    /**
     * 文案
     */
    private void initView() {
        AppUtils.setEditTextInhibitInputSpace(dtAccountText, false);
        btConfirm.setText(getString(R.string.zb_confirm));
        tvTerification.setText(getString(R.string.zb_sms_verication));

        tvChangeLoginType.setText(getString(R.string.zb_input_sms_tip));
        btConfirm.setText("确定");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        mInputHelper.removeViews();
    }


    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return BIZTopBarFactory.createDefaultForLogin(view, this).getView();
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
        if (sms.length() != 6) {
            T.showShort(ZBResetPasswordActivity.this, "验证码错误");
            return;
        }
        ZbPassport.checkCaptcha(phoneNum, sms, new ZbResultListener() {
            @Override
            public void onSuccess() {
                if (bundle == null) {
                    bundle = new Bundle();
                }
//                bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isCommentActivity);
                bundle.putString("phoneNum", phoneNum);
                bundle.putString("sms", sms);
                Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager
                        .ZB_RESET_NEW_PASSWORD);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                T.showShort(ZBResetPasswordActivity.this, errorMessage);
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
                                if (errorCode == ErrorCode.ERROR_NEED_GRRPHICS) {
                                    final ZbGraphicDialog zbGraphicDialog = new ZbGraphicDialog(ZBResetPasswordActivity.this);
                                    zbGraphicDialog.setBuilder(new ZbGraphicDialog.Builder()
                                            .setMessage("请先验证图形验证码")
                                            .setOkText("确定")
                                            .setOnClickListener(new ZbGraphicDialog.OnDialogClickListener() {
                                                @Override
                                                public void onLeftClick() {
                                                    if (zbGraphicDialog.isShowing()) {
                                                        zbGraphicDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onRightClick() {
                                                    if (TextUtils.isEmpty(zbGraphicDialog.getEtGraphic().getText().toString())) {
                                                        T.showShort(ZBResetPasswordActivity.this, "请先输入图形验证码");
                                                    } else {
                                                        ZbPassport.sendCaptcha(phoneNum, zbGraphicDialog.getEtGraphic().getText().toString(), new ZbResultListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                T.showShort(ZBResetPasswordActivity.this, "验证通过");
                                                                if (zbGraphicDialog.isShowing()) {
                                                                    zbGraphicDialog.dismiss();
                                                                }
                                                                startTimeCountDown(); // 开始倒计时
                                                            }

                                                            @Override
                                                            public void onFailure(int errorCode, String errorMessage) {
                                                                if (timer != null) {
                                                                    timer.cancel();
                                                                }
                                                                T.showShort(ZBResetPasswordActivity.this, errorMessage);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onRefreshImage() {
                                                    String url = ZbPassport.getGraphicsCode() + "?time="+ SystemClock.elapsedRealtime();
                                                    GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("Cookie", ZbPassport.getZbConfig().getCookie()).build());
                                                    Glide.with(ZBResetPasswordActivity.this).load(glideUrl).into(zbGraphicDialog.getIvGrahpic());
                                                }
                                            }));
                                    zbGraphicDialog.show();
                                } else {
                                    if (timer != null) {
                                        timer.cancel();
                                    }
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
        timer = LoginUtil.startCountDownTimer(this, tvTerification, 60);
    }

}
