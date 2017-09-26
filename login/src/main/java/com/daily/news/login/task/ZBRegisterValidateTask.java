package com.daily.news.login.task;


import com.daily.news.login.bean.ZBLoginBean;
import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 注册验证bean
 * Created by wanglinjie.
 * create time:2017/8/25  下午4:32
 */

public class ZBRegisterValidateTask extends APIPostTask<ZBLoginBean> {
    public ZBRegisterValidateTask(LoadingCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {
        put("session_id", params[0]);
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.ZB_REG_SERVER;
    }
}

