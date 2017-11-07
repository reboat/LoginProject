package com.daily.news.login.zbtxz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.UserProtectBean;
import com.daily.news.login.task.UserProtectTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.common.permission.IPermissionCallBack;
import com.zjrb.core.common.permission.Permission;
import com.zjrb.core.common.permission.PermissionManager;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.widget.dialog.ConfirmDialog;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙报通行证 - 注册页面
 * Created by wanglinjie.
 * create time:2017/8/11  上午11:04
 */
public class ZBRegisterActivity extends BaseActivity implements ConfirmDialog.OnConfirmListener {

    @BindView(R2.id.dt_account_text)
    EditText dtAccountText;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.verification_code_see_btn)
    ImageView verificationCodeSeeBtn;
    @BindView(R2.id.bt_register)
    TextView btRegister;
    @BindView(R2.id.tv_link)
    TextView tvLink;
    @BindView(R2.id.tv_link_tip)
    TextView tvLinkTip;
    @BindView(R2.id.rb_reg)
    CheckBox mRbReg;

    private boolean isClick = false;
    private int passwordLength = 0;
    private boolean isFromComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_register);
        getIntentData(getIntent());
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        verificationCodeSeeBtn.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
        btRegister.setText(getString(R.string.zb_register));
        tvLinkTip.setText(getText(R.string.zb_reg_tip));
        tvLink.setText(getText(R.string.zb_reg_link));
    }

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isFromComment = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
        }
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_register_toolbar)).getView();
    }

    @OnClick({R2.id.verification_code_see_btn, R2.id.bt_register, R2.id.tv_link})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        if (view.getId() == R.id.verification_code_see_btn) {
            clickSeePassword();
        } else if (view.getId() == R.id.tv_link) {
            getUserProject();
        } else {
            if (mRbReg.isChecked()) {
                clickRegBtn();
            } else {
                T.showShortNow(this, getString(R.string.please_agree_protocol));
            }
        }
    }


    /**
     * 获取用户协议地址
     */
    private String urlData = "";

    private void getUserProject() {

        new UserProtectTask(new APIExpandCallBack<UserProtectBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBRegisterActivity.this, errMsg);
            }

            @Override
            public void onSuccess(UserProtectBean bean) {
                if (bean != null) {
                    urlData = bean.getUser_agreement();
                }
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putString("url", urlData);
                Nav.with(ZBRegisterActivity.this).setExtras(bundle).toPath("/login/ZBUserProtectActivity");
            }
        }).setTag(this).exe();
    }

    /**
     * 点击密码可视
     */
    private void clickSeePassword() {
        passwordLength = etPasswordText.getText().toString().length();
        if (!isClick) {
            //开启
            verificationCodeSeeBtn.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_see));
            etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPasswordText.setSelection(passwordLength);
            isClick = true;
        } else {
            //隐藏
            verificationCodeSeeBtn.getDrawable().setLevel(getResources().getInteger(R.integer.level_password_unsee));
            etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPasswordText.setSelection(passwordLength);
            isClick = false;
        }
    }

    /**
     * 点击注册
     */
    private void clickRegBtn() {
        if (dtAccountText.getText().toString().isEmpty()) {
            T.showShort(this, getString(R.string.zb_phone_num_empty));
        } else if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
            //注册
            passwordLength = etPasswordText.getText().toString().length();
            if (passwordLength == 0) {
                T.showShort(this, getString(R.string.zb_password_empty));
            } else if (passwordLength < 6 || passwordLength > 30) {
                T.showShort(this, getString(R.string.zb_password_error));
            } else {
                checkAccountExist(this, dtAccountText.getText().toString());
            }
        } else {
            T.showShort(this, getString(R.string.zb_phone_num_error));
        }
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
                T.showShort(ZBRegisterActivity.this, s);
            }

            @Override
            public void onSuccess(boolean b, String s) {
                if (!b) {
                    getverificationPermission();
                } else {
                    ConfirmDialog dialog = new ConfirmDialog(ZBRegisterActivity.this);
                    dialog.setTitle("该手机号码已经注册，是否立即登录?");
                    dialog.setOnConfirmListener(ZBRegisterActivity.this);
                    dialog.show();
//                    T.showShort(ZBRegisterActivity.this, getString(R.string.zb_account_exise));
                }
            }
        });
    }


    /**
     * 获取短信验证权限
     */
    private void getverificationPermission() {
        PermissionManager.get().request(ZBRegisterActivity.this, new
                IPermissionCallBack() {
                    @Override
                    public void onGranted(boolean isAlreadyDef) {
                        WoaSdk.getSmsCaptcha(ZBRegisterActivity.this,
                                dtAccountText.getText().toString(),
                                etPasswordText.getText().toString(),
                                new OnGetSmsCaptchaListener() {
                                    @Override
                                    public void onFailure(int i, String s) {
                                        T.showShort(ZBRegisterActivity.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        //获取uuid，并进入短信验证页面
                                        if (bundle == null) {
                                            bundle = new Bundle();
                                        }
                                        bundle.putString(Key.UUID, s);
                                        bundle.putString(Key.ACCOUNTID, dtAccountText.getText().toString());
                                        bundle.putString(Key.PASSWORD, etPasswordText.getText().toString());
                                        bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
                                        Nav.with(ZBRegisterActivity.this).setExtras(bundle).toPath(RouteManager.ZB_VERIFICAITION);
                                    }
                                });

                    }

                    @Override
                    public void onDenied(List<String> neverAskPerms) {
                        T.showShort(ZBRegisterActivity.this, getString(R.string.tip_permission_denied));

                    }

                    @Override
                    public void onElse(List<String> deniedPerms, List<String>
                            neverAskPerms) {
                    }
                }, Permission.PHONE_READ_PHONE_STATE);
    }

    @Override
    public void onCancel() {

    }

    private Bundle bundle;

    @Override
    public void onOK() {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("mobile", dtAccountText.getText().toString());
        Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_LOGIN);
        finish();
    }
}
