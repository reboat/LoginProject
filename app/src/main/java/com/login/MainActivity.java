package com.login;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.nav.Nav;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv_text);
        tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_text:
                Nav.with(this).to(Uri.parse("http://www.8531.cn/login/LoginActivity")
                        .buildUpon()
                        .build(), 0);
//                ARouter.getInstance().build("/module/com.login/ZBLoginActivity")
//                        .navigation();

                break;
        }
    }
}
