package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;

import cn.daily.news.biz.core.model.MultiAccountBean;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIGetTask;

/**
 * 多账号详情接口
 */
public class GetMultiAccountTask extends APIGetTask<MultiAccountBean> {

    public GetMultiAccountTask(APIExpandCallBack<MultiAccountBean> callBack) {
        super(callBack);
    }

    /**
     * @param params 参数
     */
    @Override
    public void onSetupParams(Object... params) {
        put("auth_type", params[0]); //  认证类型 phone_number qq wei_xin wei_bo
        put("auth_uid", params[1]); // (绑定账号的信息）社交账号的union_id或者手机号码
        put("auth_token", params[2]); // 第三方平台的access_token或者手机短信验证码
        put("session_id", params[3]); // 用户登录后对应的sessionId
    }

    @Override
    public String getApi() {
        return APIManager.endpoint.MULTI_ACCOUNT_DETAIL;
    }
}
