package com.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.daily.news.login.LoginActivity;
import com.daily.news.login.zbtxz.ZBLoginActivity;
import com.login.tesk.TestAuthActivity;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.db.ThemeMode;
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
        findViewById(R.id.test_night).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_text:
                Nav.with(this).toPath("/login/ZBMobileValidateActivity");
                break;
            case R.id.test_login:
                startActivity(new Intent(this, ZBLoginActivity.class));
                break;
            case R.id.test_auth:
                startActivity(new Intent(this, TestAuthActivity.class));
                break;
            case R.id.test_night:
                ThemeMode.setUiMode(!ThemeMode.isNightMode());
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
