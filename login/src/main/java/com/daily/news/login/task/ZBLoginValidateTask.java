package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.domain.ZBLoginBean;

/**
 * 新版登录验证接口
 * http://10.100.62.28:8090/pages/viewpage.action?pageId=71631004
 */
public class ZBLoginValidateTask extends APIPostTask<ZBLoginBean> {

    public ZBLoginValidateTask(LoadingCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {
        put("union_id", params[0]);
        put("auth_uid", params[1]);
        put("auth_type", params[2]);
        put("auth_token", params[4]);
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.LOGIN_VERIFICATION;
    }
}
