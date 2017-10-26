package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 获取用户协议代码
 * Created by wanglinjie.
 * create time:2017/7/28  上午11:18
 */
public class UserProtectTask extends APIGetTask<UserProtectBean> {

    public UserProtectTask(LoadingCallBack<UserProtectBean> callBack) {
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
        return APIManager.endpoint.USER_PROTECT;
    }
}
