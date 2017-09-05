package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.OnResetPasswordListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.ZBLoginBean;
import com.daily.news.login.global.Key;
import com.daily.news.login.task.LoginValidateTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置密码页面
 * Created by wanglinjie.
 * create time:2017/8/11  下午4:56
 */

public class ZBResetNewPassWord extends BaseActivity implements TextWatcher {

    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.tv_tip)
    TextView tvTip;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.iv_see)
    ImageView ivSee;
    @BindView(R2.id.fy_password)
    FrameLayout fyPassword;
    @BindView(R2.id.bt_confirm)
    Button btConfirm;

    public String mUuid = "";
    public String mAccountID = "";
    private boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_zbtxz_reset_password_confirm);
        ButterKnife.bind(this);
        initView();
        getIntentData(getIntent());
    }

    /**
     * @param intent 获取传递到重置密码页面的数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            mUuid = data.getQueryParameter(Key.UUID);
            mAccountID = data.getQueryParameter(Key.ACCOUNTID);
        }
    }

    private void initView() {
        ivLogo.setBackgroundResource(R.mipmap.module_login_day_zbtxz);
//        if (!ThemeMode.isNightMode()) {
//            ivSee.setBackgroundResource(R.mipmap.module_login_day_password_unsee);
//        } else {
//            ivSee.setBackgroundResource(R.mipmap.module_login_night_password_unsee);
//        }
        etPasswordText.addTextChangedListener(this);
        btConfirm.setText(getString(R.string.zb_confirm));
        btConfirm.setBackgroundResource(R.drawable.border_zblogin_btn_bg);
        tvTip.setText(getString(R.string.zb_set_password_tip));
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_toolbar_login)).getView();
    }

    @OnClick({R2.id.bt_confirm,R2.id.iv_see})
    public void onClick(View view) {
        if (ClickTracker.isDoubleClick()) return;
        if (view.getId() == R.id.bt_confirm) {
            findPassword();
        }else{
            int length = etPasswordText.getText().toString().length();
            if (length > 0) {
                if (!isClick) {
                    //开启
//                    if (!ThemeMode.isNightMode()) {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_day_password_see);
//                    } else {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_night_password_see);
//                    }
                    etPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPasswordText.setSelection(length);
                    isClick = true;
                } else {
//                    if (!ThemeMode.isNightMode()) {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_day_password_unsee);
//                    } else {
//                        ivSee.setBackgroundResource(R.mipmap.module_login_night_password_unsee);
//                    }
                    //隐藏
                    etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPasswordText.setSelection(length);
                    isClick = false;
                }
            } else {
                T.showShort(this, getString(R.string.zb_passwprd_readable));
            }
        }
    }

    /**
     * 重置密码
     */
    private void findPassword() {
        WoaSdk.resetPassword(this,
                mAccountID,
                mUuid,
                etPasswordText.getText().toString(),
                new OnResetPasswordListener() {

                    @Override
                    public void onFailure(int i, String s) {
                        T.showShort(ZBResetNewPassWord.this, s);
                    }

                    @Override
                    public void onSuccess() {
                        WoaSdk.login(ZBResetNewPassWord.this,
                                mAccountID,
                                etPasswordText.getText().toString(),
                                new OnLoginListener() {

                                    @Override
                                    public void onFailure(int i, String s) {
                                        T.showShort(ZBResetNewPassWord.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s, String s1, String s2) {
                                        regGetToken(s);
                                    }
                                });
                    }
                });
    }

    /**
     * 重置密码后需要进行登录
     */
    private void regGetToken(String sessionId) {
        new LoginValidateTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onError(String errMsg, int errCode) {
                T.showShortNow(ZBResetNewPassWord.this, errMsg);
            }

            @Override
            public void onSuccess(@NonNull ZBLoginBean result) {
                //重置完登录边锋
//                ZBToast.createToastConfig().ToastShow();
//                if (result.getResultCode() == 0) {
//                    //登录成功
//                    UserBiz userBiz = UserBiz.get();
//                    userBiz.setAvatar("");
//                    userBiz.setNickName(PHONE_NUM);
//
//                    int size = AppManager.get().getCount();
//                    if (size > 1) {
//                        ZBChangePasswordActivity.this.finish();
//                        //还需要关闭前面2个页面
//                        EventBus.getDefault().postSticky(new ZBCloseActEvent());
//                    } else {
//                        UIUtils.getActivity()
//                                .startActivity(new Intent(ZBChangePasswordActivity.this, MainActivity.class));
//                    }
//                    //WLJ 网脉记录首次登陆
//                    WmUtil.onLogin();
//                } else {
//                    showShortToast(result.getResultMsg());
//                }
            }
        }).setTag(this).exe(sessionId, "BIANFENG", "", mAccountID, "");

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (etPasswordText != null && etPasswordText.getText().toString().length() < 6) {
            btConfirm.setEnabled(false);
            btConfirm.setBackgroundResource(R.drawable.border_zblogin_btn_bg);
        } else {
            btConfirm.setEnabled(true);
            btConfirm.setBackgroundResource(R.drawable.border_login_zb_password_bg);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            if (etPasswordText.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                etPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isClick = false;
            }

        }
    }
}
