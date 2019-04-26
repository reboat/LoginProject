package com.login;

import android.app.Application;

import com.netease.mobsec.rjsb.watchman;
import com.squareup.leakcanary.LeakCanary;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.passport.ZbConfig;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ZbConstants;

import cn.daily.news.biz.core.constant.Key;
import cn.daily.news.biz.core.db.SettingManager;
import cn.daily.news.biz.core.db.ThemeMode;
import cn.daily.news.biz.core.utils.BaseInit;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        BaseInit.init(UIUtils.getApp(), "bianfeng");
        ThemeMode.init(this);
        initPassport();
        watchman.init(this, Key.YiDun.PRODUCT_NUMBER);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        SettingManager.init(this);
    }

    private void initPassport() {
//        ZbPassport.init(this,
//                new ZbConfig.Builder().setEnvType(ZbConstants.Env.DEV)
//                        .setDebug(true)
//                        .setAppVersion("1.0")
//                        .setAppUuid("uuid"));
        ZbPassport.init(this,
                new ZbConfig.Builder().setEnvType(ZbConstants.Env.TEST)
                        .setDebug(true)
                        .setAppVersion("1.0")
                        .setClientId(1)
                        .setToken("J8BWUjBaYStIHqBu1g9pFjWv")
                        .setAppUuid("uuid"));
    }
}
