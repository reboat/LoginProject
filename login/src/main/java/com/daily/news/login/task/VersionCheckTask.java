package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.load.LoadingCallBack;

import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIGetTask;


/**
 * 浙报通行证升级校验接口,返回52005代表需要升级
 */
public class VersionCheckTask extends APIGetTask<Void> {

    public VersionCheckTask(APIExpandCallBack<Void> callBack) {
        super(callBack);
    }

    /**
     * @param params 参数
     *               手机号
     *               短信验证码
     */
    @Override
    public void onSetupParams(Object... params) {
    }

    @Override
    public String getApi() {
        return APIManager.endpoint.PASSPORT_VERSION_CHECK;
    }
}
