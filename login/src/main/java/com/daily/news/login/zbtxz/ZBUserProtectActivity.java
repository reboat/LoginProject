package com.daily.news.login.zbtxz;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户协议页面
 * Created by wanglinjie.
 * create time:2017/10/26  下午7:50
 */

public class ZBUserProtectActivity extends BaseActivity {

    @BindView(R2.id.v_web)
    WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_user_protect);
        ButterKnife.bind(this);
        mWeb.loadUrl("https://zj.8531.cn/page/agreement.html");
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, "用户协议").getView();
    }


}
