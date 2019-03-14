package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daily.news.login.LoginMainActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.task.GetMuitiAccountTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.api.task.GetAccessTokenTask;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.base.toolbar.holder.DefaultTopBarHolder2;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.manager.AppManager;
import com.zjrb.core.common.manager.TimerManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.domain.AccountBean;
import com.zjrb.core.domain.MultiAccountBean;
import com.zjrb.core.domain.base.AccessTokenBean;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.widget.dialog.ZBBindDialog;
import com.zjrb.core.ui.widget.dialog.ZbGraphicDialog;
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

/**
 * Date: 2018/8/30
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 绑定手机号界面
 */
public class ZBBindMobileActivity extends BaseActivity {
    @BindView(R2.id.dt_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_sms_text)
    EditText etSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView tvVerification;
    @BindView(R2.id.bt_confirm)
    TextView btConfirm;
    @BindView(R2.id.iv_top_jump)
    TextView mTvJump;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;

    private boolean isAuthSuccess;

    private TimerManager.TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_mobile_bind);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化标题
     */
    private void initView() {
        mTvJump.setVisibility(View.GONE);
        //不允许输入空格
        AppUtils.setEditTextInhibitInputSpace(dtAccountText, false);
        mTvTitle.setText(getString(R.string.zb_mobile_bind_tip));
        btConfirm.setText(getString(R.string.zb_mobile_ok));
        tvVerification.setText(getString(R.string.zb_sms_verication));
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
        topBarHolder.setTopBarText(getString(R.string.zb_mobile_bind_title));
        topBarHolder.setViewVisible(topBarHolder.getRightText(), View.GONE);
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
                    T.showShort(ZBBindMobileActivity.this, getString(R.string
                            .zb_phone_num_empty));
                } else {
                    T.showShort(ZBBindMobileActivity.this, getString(R.string
                            .zb_phone_num_error));
                }
            }
            //提交
        } else if (view.getId() == R.id.bt_confirm) {
            //验证码
            if (etSmsText.getText() != null && !TextUtils.isEmpty(etSmsText.getText().toString())) {
                if (dtAccountText.getText() != null && !TextUtils.isEmpty(dtAccountText.getText()
                        .toString())) {
                    bindMobile(dtAccountText.getText().toString(), etSmsText.getText()
                            .toString());
                } else {
                    T.showShort(ZBBindMobileActivity.this, getString(R.string
                            .zb_phone_num_empty));
                }
            } else {
                T.showShortNow(this, getString(R.string.zb_input_sms_verication));
            }
        } else {
            finish();
        }

    }


    /**
     * 绑定手机号 后续分情况跳账号合并页面,或者弹出解绑或者绑定成功的对话框
     *
     * @param mobile
     * @param smsCode
     */
    private void bindMobile(final String mobile, final String smsCode) {
        new GetAccessTokenTask(new APIExpandCallBack<AccessTokenBean>() {
            @Override
            public void onSuccess(AccessTokenBean data) {
                if (data != null) {
                    String token = data.getAccess_token();
                    ZbPassport.changePhoneNum(mobile, smsCode, token, new ZbResultListener() {
                        @Override
                        public void onSuccess() {
                            // TODO: 去掉积分处理
//                if (passData != null) {
//                    ZbBindEntity zbBindEntity = JsonUtils.parseObject(passData, ZbBindEntity.class);
//                    if (zbBindEntity != null) {
//                        ZBUtils.showPointDialog(zbBindEntity.data);
//                    }
//                }
                            final ZBBindDialog zbBindDialog = new ZBBindDialog(ZBBindMobileActivity.this);
                            zbBindDialog.setBuilder(new ZBBindDialog.Builder()
                                    .setTitle("绑定成功")
                                    .setMessage("现在可以发表评论啦! 如手机号有变动,可在个人中心-账号设置页面进行更改")
                                    .setOkText("知道了")
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == com.zjrb.core.R.id.btn_ok) {
                                                if (zbBindDialog.isShowing()) {
                                                    zbBindDialog.dismiss();
                                                }
                                                finish();
                                                AppManager.get().finishActivity(LoginMainActivity.class);
                                            }
                                        }
                                    }));
                            zbBindDialog.show();
                            // 更新手机号信息
                            AccountBean account = UserBiz.get().getAccount();
                            account.setPhone_number(mobile); // 实名认证账号
                            UserBiz.get().setAccount(account);
                            Intent intent = new Intent("bind_mobile_successful");
                            LocalBroadcastManager.getInstance(ZBBindMobileActivity.this).sendBroadcast(intent);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage) {
                            if (errorCode == ErrorCode.ERROR_PHONENUM_ALREADY_BIND) { // 该手机号已经被其他账号占用
                                final ZBBindDialog zbBindDialog = new ZBBindDialog(ZBBindMobileActivity.this);
                                zbBindDialog.setBuilder(new ZBBindDialog.Builder()
                                        .setTitle("绑定失败")
                                        .setMessage("该手机号已被注册,且绑定有同种类型的第三方账号")
                                        .setOkText("知道了")
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (v.getId() == com.zjrb.core.R.id.btn_ok) {
                                                    if (zbBindDialog.isShowing()) {
                                                        zbBindDialog.dismiss();
                                                    }
                                                }
                                            }
                                        }));
                                zbBindDialog.show();
                            } else if (errorCode == ErrorCode.ERROR_PHONENUM_CAN_MERGE || errorCode == ErrorCode.ERROR_THIRD_PARTY_CAN_MERGE) { // 进行账号合并的情况
                                new GetMuitiAccountTask(new APIExpandCallBack<MultiAccountBean>() {

                                    @Override
                                    public void onSuccess(MultiAccountBean data) {
                                        if (data != null) {
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("merge_data", data);
                                            Nav.with(getActivity()).setExtras(bundle).toPath(RouteManager.ZB_ACCOUNT_MERGE);
                                        } else {
                                            T.showShortNow(ZBBindMobileActivity.this, "绑定失败");
                                        }
                                    }

                                    @Override
                                    public void onError(String errMsg, int errCode) {
                                        super.onError(errMsg, errCode);
                                        T.showShortNow(ZBBindMobileActivity.this, errMsg);
                                    }
                                }).setTag(this).exe("phone_number", mobile, smsCode);
                            } else {
                                T.showShortNow(ZBBindMobileActivity.this, errorMessage);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errMsg, int errCode) {
                super.onError(errMsg, errCode);
            }
        }).setTag(this).exe();

    }


    /**
     * 获取绑定手机号短信验证码
     *
     * @param mobile 手机号
     */
    private void getValidateCode(final String mobile) {
        PermissionManager.get().request(ZBBindMobileActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        ZbPassport.sendCaptcha(mobile, "", new ZbResultListener() {
                            @Override
                            public void onSuccess() {
                                startTimeCountDown();
                                T.showShortNow(getActivity(), getString(R.string
                                        .zb_sms_send));
                            }

                            @Override
                            public void onFailure(int errorCode, String errorMessage) {
                                // 需要图形验证码的情况
                                if (errorCode == ErrorCode.ERROR_NEED_GRRPHICS) {
                                    final ZbGraphicDialog zbGraphicDialog = new ZbGraphicDialog(ZBBindMobileActivity.this);
                                    zbGraphicDialog.setBuilder(new ZbGraphicDialog.Builder()
                                            .setMessage("请先验证图形验证码")
                                            .setOkText("确定")
                                            .setUrl(ZbPassport.getGraphicsCode())
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
                                                        T.showShort(ZBBindMobileActivity.this, "请先输入图形验证码");
                                                    } else {
                                                        ZbPassport.sendCaptcha(mobile, zbGraphicDialog.getEtGraphic().getText().toString(), new ZbResultListener() {
                                                            @Override
                                                            public void onSuccess() {
                                                                T.showShort(ZBBindMobileActivity.this, "验证通过");
                                                                if (zbGraphicDialog.isShowing()) {
                                                                    zbGraphicDialog.dismiss();
                                                                }
                                                                startTimeCountDown(); // 开始倒计时
                                                            }

                                                            @Override
                                                            public void onFailure(int errorCode, String errorMessage) {
                                                                T.showShort(ZBBindMobileActivity.this, errorMessage);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onRefreshImage() {
                                                    String url = ZbPassport.getGraphicsCode();
                                                    Glide.with(ZBBindMobileActivity.this).load(url).into(zbGraphicDialog.getIvGrahpic());
                                                }
                                            }));
                                    zbGraphicDialog.show();
                                } else {
                                    T.showShortNow(ZBBindMobileActivity.this, errorMessage);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBBindMobileActivity.this, getString(R.string
                                .tip_permission_denied));

                    }

                    @Override
                    public void onElse(List<String> deniedPerms, List<String>
                            neverAskPerms) {
                    }
                }, Permission.PHONE_READ_PHONE_STATE);
    }

    /**
     * 实名制设置为120s一次
     * 开始倒计时
     * 重复访问获取验证码的时间是多少  60s  3次  一天最多5次
     */
    private void startTimeCountDown() {
        tvVerification.setEnabled(false);
        //倒计时
        timerTask = new TimerManager.TimerTask(1000, 1000) {
            @Override
            public void run(long count) {
                long value = (120 - count);
                tvVerification.setBackgroundResource(R.drawable.border_timer_text_bg);
                tvVerification.setTextColor(getResources().getColor(R.color.tc_999999));
                tvVerification.setText("(" + value + ")" + getString(R.string
                        .zb_login_get_validationcode_again));
                if (value == 0) {
                    TimerManager.cancel(this);
                    tvVerification.setEnabled(true);
                    tvVerification.setBackgroundResource(R.drawable.module_login_bg_sms_verification);
                    tvVerification.setTextColor(getResources().getColor(R.color.tc_f44b50));
                    tvVerification.setText(getString(R.string
                            .zb_login_resend));
                }
            }
        };
        TimerManager.schedule(timerTask);
    }

    @Override
    public void finish() {
        // 绑定页面点击返回按钮,手动清空session
        UserBiz.get().logout();
        super.finish();
//        RealNameAuthHelper.get().finishAuth(isAuthSuccess);
//        AppManager.get().finishActivity(LoginMainActivity.class);
    }

}
