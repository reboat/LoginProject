package com.daily.news.login.zbtxz;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bianfeng.woa.OnCheckAccountExistListener;
import com.bianfeng.woa.OnGetSmsCaptchaListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.common.base.toolbar.ToolBarFactory;
import com.zjrb.coreprojectlibrary.common.listener.IPermissionCallBack;
import com.zjrb.coreprojectlibrary.common.manager.PermissionManager;
import com.zjrb.coreprojectlibrary.domain.enums.Permission;
import com.zjrb.coreprojectlibrary.ui.widget.DeleteEditText;
import com.zjrb.coreprojectlibrary.utils.AppUtils;
import com.zjrb.coreprojectlibrary.utils.T;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 浙报通行证注册页面
 * Created by wanglinjie.
 * create time:2017/8/11  上午11:04
 */

@Route(path = "/module/login/ZBRegisterActivity")
public class ZBRegisterActivity extends BaseActivity implements TextWatcher {

    @BindView(R2.id.dt_account_text)
    DeleteEditText dtAccountText;
    @BindView(R2.id.ly_login)
    LinearLayout lyLogin;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.verification_code_see_btn)
    ImageView verificationCodeSeeBtn;
    @BindView(R2.id.fy_password)
    FrameLayout fyPassword;
    @BindView(R2.id.bt_register)
    Button btRegister;
    @BindView(R2.id.cb_btn)
    CheckBox cbbtn;

    private boolean isClick = false;
    private int passwordLength = 0;
    private String uuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.module_zbtxz_register);
        ButterKnife.bind(this);
    }

    @Override
    protected void onSetUpToolBar(Toolbar toolbar, ActionBar actionBar) {
        ToolBarFactory.createStyle1(this, toolbar, getString(R.string.module_register_toolbar));
    }

    @OnClick({R2.id.verification_code_see_btn, R2.id.bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            //密码可视
            case R2.id.verification_code_see_btn:
                passwordLength = etPasswordText.getText().toString().length();
                if (passwordLength > 0) {
                    if (!isClick) {
                        //开启
                        etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPasswordText.setSelection(passwordLength);
                        isClick = true;
                    } else {
                        //隐藏
                        etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPasswordText.setSelection(passwordLength);
                        isClick = false;
                    }
                } else {
                    T.showShort(this, "输入密码后显示明文");
                }
                break;
            //注册 获取验证码
            case R2.id.bt_register:
                //注册账号 获取token
                if (!ClickTracker.isDoubleClick()) {
                    if (cbbtn.isChecked()) {
                        if (AppUtils.isMobileNum(dtAccountText.getText().toString())) {
                            //注册
                            passwordLength = etPasswordText.getText().toString().length();
                            if (passwordLength < 6) {
                                T.showShort(this, getString(R.string.zb_verification_error));
                            } else {
                                checkAccountExist(this, dtAccountText.getText().toString());
                            }
                        } else {
                            T.showShort(this, getString(R.string.zb_phone_num_error));
                        }
                    } else {
                        T.showShort(this, getString(R.string.zb_register_check_true));
                    }

                }
                break;
        }
    }

    /**
     * @param ctx
     * @param account 手机账号
     *                检测账号是否存在
     */
    private void checkAccountExist(Context ctx, String account) {
        WoaSdk.checkAccountExist(this, dtAccountText.getText().toString(), new OnCheckAccountExistListener() {
            @Override
            public void onFailure(int i, String s) {
                T.showShort(ZBRegisterActivity.this, s);
            }

            @Override
            public void onSuccess(boolean b, String s) {
                if (!b) {
                    getverificationPermission();
                } else {
                    T.showShort(ZBRegisterActivity.this, getString(R.string.zb_account_exise));
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
                                        //获取uuid
                                        uuid = s;
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //删除密码后默认为关闭密码可视
        if (s.length() == 0) {
            btRegister.setBackgroundResource(R.drawable.border_login_text_bg);
            btRegister.setEnabled(true);
            if (etPasswordText.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isClick = false;
            }

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
