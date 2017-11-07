package com.login;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.bianfeng.passport.Passport;
import com.bianfeng.woa.WoaSdk;
import com.squareup.leakcanary.LeakCanary;
import com.zjrb.core.common.base.BaseInit;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.utils.UIUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        BaseInit.getInstance().init(UIUtils.getApp(), "bianfeng");
        ThemeMode.initTheme(R.style.AppTheme, R.style.NightAppTheme);
        UiModeManager.init(this, R.styleable.SupportUiMode);
        WoaSdk.init(this);
        Passport.init(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
