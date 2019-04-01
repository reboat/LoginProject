package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;

import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.network.compatible.APIPostTask;

/**
 * 账号合并接口
 * Created by sishuqun
 * create time:2018/09/10
 */
public class UserAccountMergeTask extends APIPostTask<ZBLoginBean> {

    public UserAccountMergeTask(APIExpandCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    public void onSetupParams(Object... params) {
        put("unselected_account_id", params[0]); // 未选中的账户浙报通行证id
        put("selected_account_id", params[1]); // 选中的账户浙报通行证id
        put("session_id", params[2]); // 用户登录后对应的sessionId
    }

    @Override
    public String getApi() {
        return APIManager.endpoint.ACCOUNT_MERGE;
    }
}
