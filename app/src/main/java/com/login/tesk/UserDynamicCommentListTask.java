package com.login.tesk;


import com.zjrb.core.api.base.APIGetTask;
import com.zjrb.core.api.callback.LoadingCallBack;
import com.zjrb.core.common.global.C;

/**
 * 用户中心 - 我的动态 - 评论列表 - task
 *
 * @author a_liYa
 * @date 2017/9/25 下午2:32.
 */
public class UserDynamicCommentListTask extends APIGetTask<Void> {

    public UserDynamicCommentListTask(LoadingCallBack<Void> callBack) {
        super(callBack);
    }

    @Override
    protected void onSetupParams(Object... params) {
        put("size", C.PAGE_SIZE);
        if (params != null && params.length > 0) {
            put("start", params[0]);
        }
    }

    @Override
    protected String getApi() {
        return "/api/account_dynamic/dynamic_list";
    }
}
