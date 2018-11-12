package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.domain.ZBLoginBean;

/**
 * 登录验证接口
 * Created by wanglinjie.
 * create time:2017/7/28  上午11:18
 */
public class LoginValidateTask extends APIPostTask<ZBLoginBean> {

    public LoginValidateTask(LoadingCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {
        put("auth_token", params[0]);
        put("auth_type", params[1]);
        put("auth_uid", params[2]);
        put("nick_name", params[3]);
        put("union_id", params[4]);
        put("is_new",params[5]);
        put("check_token",params[6]);
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.LOGIN_VERIFICATION;
    }
}
