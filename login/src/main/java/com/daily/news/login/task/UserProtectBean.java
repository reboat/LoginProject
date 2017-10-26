package com.daily.news.login.task;

import com.zjrb.core.domain.base.BaseData;

/**
 * Created by wanglinjie.
 * create time:2017/10/26  下午7:30
 */

public class UserProtectBean extends BaseData {
    private String user_agreement;
    public String getUser_agreement() {
        return user_agreement;
    }

    public void setUser_agreement(String user_agreement) {
        this.user_agreement = user_agreement;
    }

}
