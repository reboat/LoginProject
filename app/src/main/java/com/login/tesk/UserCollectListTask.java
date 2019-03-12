package com.login.tesk;

import com.zjrb.core.load.LoadingCallBack;

import cn.daily.news.biz.core.constant.C;
import cn.daily.news.biz.core.network.compatible.APIGetTask;

/**
 * 用户中心收藏列表
 *
 * @author a_liYa
 * @date 2017/9/28 下午2:52.
 */
public class UserCollectListTask extends APIGetTask<Void> {

    public UserCollectListTask(LoadingCallBack<Void> callBack) {
        super(callBack);
    }

    /**
     * @param params start:long,最后一条的时间戳
     */
    @Override
    public void onSetupParams(Object... params) {
        if (params != null && params.length > 0) {
            put("start", params[0]);
        }
        put("size", C.PAGE_SIZE);
    }

    @Override
    public String getApi() {
        return "/api/favorite/collect_list";
    }
}
