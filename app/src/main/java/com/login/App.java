package com.login;

import android.app.Application;

import com.bianfeng.passport.Passport;
import com.bianfeng.woa.WoaSdk;
import com.daily.news.login.global.Key;
import com.squareup.leakcanary.LeakCanary;
import com.zjrb.core.common.base.BaseInit;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;
import com.netease.mobsec.rjsb.watchman;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        BaseInit.init(UIUtils.getApp(), "bianfeng");
        ThemeMode.init(this);
        WoaSdk.init(this);
        Passport.init(this);
        watchman.init(this, Key.YiDun.PRODUCT_NUMBER);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        SettingManager.init(this);
    }
}
