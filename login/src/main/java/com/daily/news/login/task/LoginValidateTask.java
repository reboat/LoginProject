package com.daily.news.login.task;


import com.daily.news.login.bean.ZBLoginBean;
import com.zjrb.coreprojectlibrary.api.base.APIPostTask;
import com.zjrb.coreprojectlibrary.api.callback.LoadingCallBack;

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
        put("third_party_session_id", params[0]);
        put("auth_type", params[1]);
        put("auth_uid", params[2]);
        put("nick_name", params[3]);
        put("union_id", params[4]);
    }

    @Override
    protected String getApi() {
        return "";
//        return APIManager.endpoint.ARTICLE_COMMENT_LIST;
    }
}
