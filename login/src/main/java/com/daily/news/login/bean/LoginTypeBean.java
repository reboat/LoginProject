package com.daily.news.login.bean;

import android.support.annotation.NonNull;

/**
 * 登录方式Bean
 * Created by wanglinjie.
 * create time:2017/8/10  下午8:21
 */

public class LoginTypeBean {
    //图片ID
    public int resId;
    //文字描述
    public String type;

    public LoginTypeBean(int resId, @NonNull String type) {
        this.resId = resId;
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public String getType() {
        return type;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setType(String type) {
        this.type = type;
    }

}
