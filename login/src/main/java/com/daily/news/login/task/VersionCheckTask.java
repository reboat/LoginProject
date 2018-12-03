package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 浙报通行证升级校验接口,返回52005代表需要升级
 */
public class VersionCheckTask extends APIGetTask<Void> {

    public VersionCheckTask(LoadingCallBack<Void> callBack) {
        super(callBack);
    }

    /**
     * @param params 参数
     *               手机号
     *               短信验证码
     */
    @Override
    protected void onSetupParams(Object... params) {
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.PASSPORT_VERSION_CHECK;
    }
}
