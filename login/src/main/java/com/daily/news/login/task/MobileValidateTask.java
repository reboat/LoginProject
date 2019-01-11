package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;

/**
 * 实名制短信验证码校验
 * Created by wanglinjie.
 * create time:2017/7/28  上午11:18
 */
public class MobileValidateTask extends APIPostTask<Void> {

    public MobileValidateTask(LoadingCallBack<Void> callBack) {
        super(callBack);
    }

    /**
     * @param params 参数
     *               手机号
     *               短信验证码
     */
    @Override
    protected void onSetupParams(Object... params) {
        put("mobile", params[0]);
        put("verification_code", params[1]);
        put("session_id", params[2]); // 5.7及之后版本必传,解决个性化账户未登录不能发验证码的问题
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.ZB_MOBILE_VALIDATE;
    }
}
