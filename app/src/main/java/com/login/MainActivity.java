package com.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daily.news.login.LoginMainActivity;
import com.login.tesk.TestAuthActivity;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.nav.Nav;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv_text);
        tv.setOnClickListener(this);
        findViewById(R.id.test_login).setOnClickListener(this);
        findViewById(R.id.test_auth).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_text:
                Nav.with(this).toPath("/login/ZBMobileValidateActivity");
                break;
            case R.id.test_login:
//                new TipDialog(MainActivity.this).setTitle(getResources().getString(com.daily.news.login.R.string.zb_mobile_login_title_reset)).setOkText(getResources().getString(com.daily.news.login.R.string.zb_mobile_reset_password)).setOnConfirmListener(new TipDialog.OnConfirmListener() {
//                    @Override
//                    public void onCancel() {
//                    }
//
//                    @Override
//                    public void onOK() {
//                        // 跳转到设置密码页面
//                        Nav.with(getActivity()).toPath(RouteManager.ZB_RESET_PASSWORD);
//                    }
//                }).show();
                startActivity(new Intent(this, LoginMainActivity.class));
                break;
            case R.id.test_auth:
                startActivity(new Intent(this, TestAuthActivity.class));
                break;
        }
    }
}
