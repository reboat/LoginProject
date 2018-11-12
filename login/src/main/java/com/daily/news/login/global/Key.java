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
     * 是否来自评论登录
     */
    public static final String IS_COMMENT_LOGIN = "is_comment_login";

    /**
     * 浙报通行证UUID
     */
    public static final String UUID = "uuid";

    /**
     * 浙报通行证手机号码
     */
    public static final String ACCOUNTID = "accountid";

    /**
     * 浙报通行证密码
     */
    public static final String PASSWORD = "password";

    public static class Value {
        /**
         * 重置密码类型
         */
        public static final String LOGIN_RESET_TYPE = "login_reset_type";

        /**
         * 账号密码登录类型
         */
        public static final String LOGIN_SMS_TYPE = "login_sms_type";
    }

    /**
     * 网易盾防作弊sdk相关参数
     */
    public static class YiDun{
        /**
         * business id
         */
        public static final String ANTI_CHEAT_REG_KEY_DEBUG = "9b3f30dd27f445518b542b8ef488d7b2";
        public static final String ANTI_CHEAT_REG_KEY_RELEASE = "68c2023979384a20bacaaa201c83a257";
        /**
         * product number
         */
        public static final String PRODUCT_NUMBER = "YD00158346389550";
    }

}
