package com.daily.news.login.task;

import com.daily.news.login.bean.SessionIdBean;
import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 获取sessionId  bean
 * Created by wanglinjie.
 * create time:2017/9/5  下午3:39
 */

public class InitTask extends APIPostTask<SessionIdBean> {

    public InitTask(LoadingCallBack<SessionIdBean> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {

    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.GET_SESSIONID;
    }
}
