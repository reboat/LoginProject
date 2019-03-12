package com.login.tesk;

import com.zjrb.core.load.LoadingCallBack;

import cn.daily.news.biz.core.constant.C;
import cn.daily.news.biz.core.network.compatible.APIGetTask;

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
    public void onSetupParams(Object... params) {
        put("size", C.PAGE_SIZE);
        if (params != null && params.length > 0) {
            put("start", params[0]);
        }
    }

    @Override
    public String getApi() {
        return "/api/account_dynamic/dynamic_list";
    }
}
