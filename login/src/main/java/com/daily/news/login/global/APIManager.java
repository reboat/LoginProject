package com.daily.news.login.global;


/**
 * API管理
 * Created by wanglinjie.
 * create time:2017/7/24  下午4:12
 */
public class APIManager {

    /**
     * url 端点 路径
     */
    public static final class endpoint {

        /**
         * 浙报通行证登录验证
         */
        public static final String LOGIN_VERIFICATION = "/api/account/auth_login";

        /**
         * 获取短信验证码
         */
        public static final String GET_SMS_CODE = "/api/account/send_validate_code";

        /**
         * 多账号详情接口  GET
         */
        public static final String MULTI_ACCOUNT_DETAIL = "/api/account/multi_account_detail";

        /**
         * 账号合并接口  POST
         */
        public static final String ACCOUNT_MERGE = "/api/account/account_merge";

        /**
         * 浙报通行证升级校验接口
         */
        public static final String PASSPORT_VERSION_CHECK = "/api/account/passport_version_check";

    }

}
