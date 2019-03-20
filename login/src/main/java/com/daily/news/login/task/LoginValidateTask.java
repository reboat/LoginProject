package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;

import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIPostTask;

/**
 * 登录验证接口
 * Created by wanglinjie.
 * create time:2017/7/28  上午11:18
 */
public class LoginValidateTask extends APIPostTask<ZBLoginBean> {

    public LoginValidateTask(APIExpandCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    public void onSetupParams(Object... params) {
        put("union_id", params[0]);
        put("auth_uid", params[1]);
        put("auth_type", params[2]);
        put("code",params[3]);
        put("check_token",params[4]);
    }

    @Override
    public String getApi() {
        return APIManager.endpoint.LOGIN_VERIFICATION;
    }
}
