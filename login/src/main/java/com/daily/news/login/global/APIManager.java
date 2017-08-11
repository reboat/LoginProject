package com.daily.news.login.global;


/**
 * API管理
 * Created by wanglinjie.
 * create time:2017/7/24  下午4:12
 */
public class APIManager {

    /**
     * 正式包，一定不要忘记改为 false
     */
    public static boolean isDebug = true;

    /**
     * Url白名单， 不需要强制进入登录页的
     */
    private static String[] whiteList = new String[]{
            endpoint.GET_CID
    };


    public static final String getBaseUri() {
        return isDebug ? value.DEBUG_BASE_URL : value.BASE_URL;
    }

    /**
     * 拼接Url
     *
     * @param endpoint 端点路径
     * @return 返回完整URl
     */
    public static String jointUrl(String endpoint) {
        return getBaseUri() + endpoint;
    }

    /**
     * 电子报
     */
    public static String getEPager() {
        return "http://act.qblife.com.cn/epaper/page";
    }

    /**
     * 微软小冰
     */
    public static String getXiaoIce() {
        return "https://act.qblife.com.cn/msbing";
    }

    /**
     * 是否在白名单内
     *
     * @param url Url或者endpoint
     * @return
     */
    public static boolean isAtWhiteList(String url) {
        if (url == null) return false;

        for (String whiteUrl : whiteList) {
            if (url.equals(whiteUrl) || url.equals(jointUrl(whiteUrl)))
                return true;
        }

        return false;
    }


    /*--------------------------------------url value-------------------------------------------*/

    /**
     * url 地址
     */
    private static final class value {

        /**
         * 正式域名
         */
        private static final String BASE_URL = "http://app.thehour.cn/h24";

        /**
         * 预发布域名
         */
        private static final String DEBUG_BASE_URL = "http://10.200.76.17/h24";

        /**
         * 正式分享URL
         */
        private static final String SHARE_URL = "http://app.thehour.cn";

        /**
         * 预发布环境分享URL
         */
        private static final String DEBUG_SHARE_URL = "http://10.200.76.17";
    }

    /**
     * url 端点 路径
     */
    public static final class endpoint {
        /**
         * 绑定ClientID
         */
        public static final String GET_CID = "/app/v0/user/updateClientId";


    }

}
