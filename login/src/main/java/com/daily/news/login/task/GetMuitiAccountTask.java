package com.daily.news.login.task;


import com.daily.news.login.bean.MultiAccountBean;
import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 多账号详情接口
 */
public class GetMuitiAccountTask extends APIGetTask<MultiAccountBean> {

    public GetMuitiAccountTask(LoadingCallBack<MultiAccountBean> callBack) {
        super(callBack);
    }

    /**
     * @param params 参数
     */
    @Override
    protected void onSetupParams(Object... params) {
        put("auth_type", params[0]); // int 认证类型 1:手机，2:微信，3:QQ，4:微博
        put("auth_uid", params[1]); // 认证Uid
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.MULTI_ACCOUNT_DETAIL;
    }
}
