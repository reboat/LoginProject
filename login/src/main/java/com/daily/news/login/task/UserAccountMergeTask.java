package com.daily.news.login.task;


import com.daily.news.login.global.APIManager;
import com.zjrb.core.api.base.APIPostTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.domain.ZBLoginBean;

/**
 * 账号合并接口
 * Created by sishuqun
 * create time:2018/09/10
 */
public class UserAccountMergeTask extends APIPostTask<ZBLoginBean> {

    public UserAccountMergeTask(LoadingCallBack<ZBLoginBean> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {
        put("unselected_account_id", params[0]); // 未选中的账户浙报通行证id
        put("selected_account_id", params[1]); // 选中的账户浙报通行证id
    }

    @Override
    protected String getApi() {
        return APIManager.endpoint.ACCOUNT_MERGE;
    }
}
