package com.daily.news.login.utils;

import com.daily.news.login.global.Key;
import com.netease.mobsec.rjsb.watchman;
import com.zjrb.core.utils.UIUtils;

/**
 * 接入易盾sdk相关工具类
 */
public class YiDunUtils {
    /**
     * 获取反作弊注册的token
     * @return token
     */
    public static String getToken(){
        return watchman.getToken(UIUtils.isDebuggable() ? Key.YiDun.ANTI_CHEAT_REG_KEY_DEBUG : Key.YiDun.ANTI_CHEAT_REG_KEY_RELEASE);
    }
}
