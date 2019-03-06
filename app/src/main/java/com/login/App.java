package com.login;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.bianfeng.passport.Passport;
import com.bianfeng.woa.WoaSdk;
import com.squareup.leakcanary.LeakCanary;
import com.zjrb.core.common.base.BaseInit;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.SettingManager;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.passport.ZbConfig;
import com.zjrb.passport.ZbPassport;
import com.zjrb.passport.constant.ZbConstants;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        BaseInit.init(UIUtils.getApp(), "bianfeng");
        ThemeMode.initTheme(R.style.AppTheme, R.style.NightAppTheme);
        UiModeManager.init(this, R.styleable.SupportUiMode);
        WoaSdk.init(this);
        Passport.init(this);
        initPassport();
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
                new ZbConfig.Builder().setEnvType(ZbConstants.Env.DEV)
                        .setDebug(true)
                        .setAppVersion("1.0")
                        .setClientId(1)
//                        .setToken("J8BWUjBaYStIHqBu1g9pFjWv")
                        .setAppUuid("uuid"));
    }
}
