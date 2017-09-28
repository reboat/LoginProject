package com.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.login.tesk.UserCollectListTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.utils.UIUtils;

/**
 * 测试登陆结合请求
 *
 * @author a_liYa
 * @date 2017/9/28 下午2:41.
 */
public class TestLoginActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        findViewById(R.id.test_login).setOnClickListener(this);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, "测试登陆").getView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_login:
                sendTask();
                break;
        }
    }

    private void sendTask() {
        Nav.with(UIUtils.getContext()).toPath("/login/LoginActivity");
        Nav.with(UIUtils.getContext()).toPath("/login/LoginActivity");
        Nav.with(UIUtils.getContext()).toPath("/login/LoginActivity");
        new UserCollectListTask(new LoadingCallBack<Void>() {
            @Override
            public void onCancel() {
                Log.e("TAG", "onCancel Collect list");
            }

            @Override
            public void onError(String errMsg, int errCode) {
                Log.e("TAG", "onError " + errMsg + " - " + errCode);
            }

            @Override
            public void onSuccess(Void data) {
                Log.e("TAG", "onSuccess " + data);
            }
        }).exe();
    }
}
