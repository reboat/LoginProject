package com.daily.news.login.global;

/**
 * key值
 * Created by wanglinjie.
 * create time:2017/7/24  下午4:12
 */

public final class Key {
    /**
     * 登录类型
     */
    public static final String LOGIN_TYPE = "login_type";



    /**
     * 浙报通行证UUID
     */
    public static final String UUID = "uuid";
    /**
     * 浙报通行证短信验证码
     */
    public static final String SMSCODE = "smscode";
    /**
     * 浙报通行证手机号码
     */
    public static final String ACCOUNTID = "accountid";

    /**
     * 浙报通行证密码
     */
    public static final String PASSWORD = "password";

    public static class  Value{
        /**
         * 重置密码类型
         */
        public static final String LOGIN_RESET_TYPE = "login_reset_type";

        /**
         * 账号密码登录类型
         */
        public static final String LOGIN_SMS_TYPE = "login_sms_type";
    }

}
