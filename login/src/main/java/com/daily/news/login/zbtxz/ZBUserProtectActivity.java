package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @BindView(R2.id.tv_text)
    TextView mTvText;

    private String urlData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        setContentView(R.layout.module_login_user_protect);
        ButterKnife.bind(this);
        mTvText.setText(Html.fromHtml(urlData));
//        mTvText.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, "用户协议").getView();
    }

    /**
     * @param intent 获取传递数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("url")) {
                urlData = intent.getExtras().getString("url");
            }
        }
    }

}
