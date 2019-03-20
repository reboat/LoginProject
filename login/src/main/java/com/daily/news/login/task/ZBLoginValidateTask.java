package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;

import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIPostTask;


/**
 * 新版登录验证接口
 * http://10.100.62.28:8090/pages/viewpage.action?pageId=71631004
 */
public class ZBLoginValidateTask extends APIPostTask<ZBLoginBean> {

    public ZBLoginValidateTask(APIExpandCallBack<ZBLoginBean> callBack) {
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
