package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.domain.base.BaseInnerData;

/**
 * 实名制获取验证码
 * Created by wanglinjie.
 * create time:2017/7/28  上午11:18
 */
public class GetSmsCodeTask extends APIGetTask<BaseInnerData> {


    public GetSmsCodeTask(LoadingCallBack<BaseInnerData> callBack) {
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
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.GET_SMS_CODE;
    }
}
