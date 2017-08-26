package com.daily.news.login.zbtxz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.OnRegisterBySmsListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.common.base.toolbar.TopBarFactory;
import com.zjrb.coreprojectlibrary.common.permission.IPermissionCallBack;
import com.zjrb.coreprojectlibrary.common.permission.Permission;
import com.zjrb.coreprojectlibrary.common.permission.PermissionManager;
import com.zjrb.coreprojectlibrary.nav.Nav;
import com.zjrb.coreprojectlibrary.ui.widget.DeleteEditText;
import com.zjrb.coreprojectlibrary.utils.AppUtils;
import com.zjrb.coreprojectlibrary.utils.T;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手机验证码登录/重置密码获取验证码
 * Created by wanglinjie.
 * create time:2017/8/11  下午3:49
 */

public class ZBResetPWSmsLogin extends BaseActivity {

    @BindView(R2.id.dt_account_text)
    DeleteEditText dtAccountText;
    @BindView(R2.id.et_sms_text)
    DeleteEditText etSmsText;
    @BindView(R2.id.tv_sms_verification)
    TextView tvTerification;
    @BindView(R2.id.tv_change_login_type)
    TextView tvChangeLoginType;

    /**
     * 登录类型：true:验证码登录/false:重置密码
     */
    private String login_type = "";
    private String uuid = "";


    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            if (intent.hasExtra(Key.LOGIN_TYPE)) {
                login_type = data.getQueryParameter(Key.LOGIN_TYPE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_reset_password);
        ButterKnife.bind(this);
        getIntentData(getIntent());
        if (login_type.equals(Key.Value.LOGIN_RESET_TYPE)) {
            tvTerification.setEnabled(false);
            tvChangeLoginType.setText(getString(R.string.zb_password_login));
        } else {
            tvTerification.setEnabled(true);
        }
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login)).getView();
    }

    @OnClick({R2.id.tv_sms_verification, R2.id.bt_confirm})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;

        //获取验证
        if (view.getId() == R.id.tv_sms_verification) {
            if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                checkAccountExist(this, dtAccountText.getText().toString());
            } else {
                if (dtAccountText.getText().toString().equals("")) {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_inout_error));
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_phone_num_error));
                }
            }
            //进入重置密码页面
        } else if (view.getId() == R.id.bt_confirm) {
            regAndLogin(uuid, tvTerification.getText().toString(), dtAccountText.getText().toString());
            //进入账号密码登录页面
        } else {
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBLoginActivity")
                    .buildUpon()
                    .build(), 0);
        }

    }


    /**
     * 先注册账号再登录
     *
     * @param uid
     * @param smsCode
     * @param phoneNum
     */
    private void regAndLogin(@NonNull String uid, @NonNull final String smsCode, @NonNull final String phoneNum) {
        WoaSdk.registerBySmsCaptcha(this, uid, smsCode, new OnRegisterBySmsListener() {

            @Override
            public void onFailure(int i, String s) {
                T.showShort(ZBResetPWSmsLogin.this, s);
            }

            @Override
            public void onSuccess(String token) {
                //注册验证
                if (Key.LOGIN_TYPE.equals(Key.Value.LOGIN_SMS_TYPE)) {
                    if (null == token || token.isEmpty()) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_reg_error));
                    } else {
                        loginZBServer(WoaSdk.getTokenInfo().getSessionId(), phoneNum);
                    }
                } else {
                    if (null == token || token.isEmpty()) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_smscode_error));
                    } else {
                        Nav.with(ZBResetPWSmsLogin.this).to(Uri.parse("http://www.8531.cn/login/ZBResetNewPassWord")
                                .buildUpon()
                                .appendQueryParameter(Key.UUID, uuid)
                                .appendQueryParameter(Key.ACCOUNTID, dtAccountText.getText().toString())
                                .build(), 0);
                    }

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


    /**
     * @param ctx
     * @param account 手机账号
     *                检测账号是否存在
     */
    private void checkAccountExist(Context ctx, String account) {
        WoaSdk.checkAccountExist(ctx, account, new OnCheckAccountExistListener() {
            @Override
            public void onFailure(int i, String s) {
                T.showShort(ZBResetPWSmsLogin.this, s);
            }

            @Override
            public void onSuccess(boolean b, String s) {
                if (!b) {
                    getverificationPermission();
                } else {
                    T.showShort(ZBResetPWSmsLogin.this, getString(R.string.zb_account_exise));
                }
            }
        });
    }

    /**
     * 获取短信验证权限
     */
    private void getverificationPermission() {
        PermissionManager.get().request(ZBResetPWSmsLogin.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        WoaSdk.getSmsCaptcha(ZBResetPWSmsLogin.this,
                                dtAccountText.getText().toString(),
                                etSmsText.getText().toString(),
                                new OnGetSmsCaptchaListener() {
                                    @Override
                                    public void onFailure(int i, String s) {
                                        T.showShort(ZBResetPWSmsLogin.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        //获取uuid
                                        uuid = s;
                                    }
                                });

                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBResetPWSmsLogin.this, getString(R.string.tip_permission_denied));

                    }

                    @Override
                    public void onElse(List<String> deniedPerms, List<String>
                            neverAskPerms) {
                    }
                }, Permission.PHONE_READ_PHONE_STATE);
    }

}
