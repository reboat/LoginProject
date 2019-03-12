package com.login.tesk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.login.R;
import com.zjrb.core.base.BaseActivity;
import com.zjrb.core.base.toolbar.TopBarFactory;

import cn.daily.news.biz.core.model.BaseData;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.task.CommentSubmitTask;

/**
 * 测试评论实名认证
 *
 * @author a_liYa
 * @date 2017/11/7 下午1:04.
 */
public class TestAuthActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_auth);
        findById(R.id.comment).setOnClickListener(this);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, "测试评论实名").getView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment:
                comment();
                break;
        }
    }

    private void comment() {
        int articleId = 1;
        int parentId = 1;
        String content = "我是评论内容";
        new CommentSubmitTask(new APIExpandCallBack<BaseData>() {
            @Override
            public void onSuccess(BaseData data) {
                Log.e("TAG", "data " + data);
            }

            @Override
            public void onError(String errMsg, int errCode) {
                Log.e("TAG", "errMsg " + errMsg);
            }
        }).exe(articleId, content, parentId);

    }

}
