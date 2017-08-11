package com.daily.news.login.zbtxz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bianfeng.woa.OnLoginListener;
import com.bianfeng.woa.OnResetPasswordListener;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.Key;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.utils.T;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 输入新密码页面
 * Created by wanglinjie.
 * create time:2017/8/11  下午4:56
 */

@Route(path = "/module/login/ZBResetNewPassWord")
public class ZBResetNewPassWord extends BaseActivity {

    @BindView(R2.id.dt_account_text)
    TextView dtAccountText;
    @BindView(R2.id.et_password_text)
    EditText etPasswordText;
    @BindView(R2.id.bt_confirm)
    Button btConfirm;

    @Autowired(name = Key.UUID)
    public String mUuid;
    @Autowired(name = Key.ACCOUNTID)
    public String mAccountID;
    @Autowired(name = Key.PASSWORD)
    public String mPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.module_zbtxz_reset_password_confirm);
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.bt_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            //进入重置密码页面
            case R2.id.bt_confirm:
                if (!ClickTracker.isDoubleClick()) {
                    findPassword();
                }
                break;
        }
    }

    /**
     * 重置密码
     */
    private void findPassword() {
        WoaSdk.resetPassword(this,
                mAccountID,
                mUuid,
                mPassWord,
                new OnResetPasswordListener() {

                    @Override
                    public void onFailure(int i, String s) {
                        T.showShort(ZBResetNewPassWord.this, s);
                    }

                    @Override
                    public void onSuccess() {
                        WoaSdk.login(ZBResetNewPassWord.this,
                                mAccountID,
                                mPassWord,
                                new OnLoginListener() {

                                    @Override
                                    public void onFailure(int i, String s) {
                                        T.showShort(ZBResetNewPassWord.this, s);
                                    }

                                    @Override
                                    public void onSuccess(String s, String s1, String s2) {
//                                        regGetToken(s);
                                    }
                                });
                    }
                });
    }
}
