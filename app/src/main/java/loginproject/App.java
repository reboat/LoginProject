package loginproject;

import android.app.Application;

import com.aliya.uimode.UiModeManager;
import com.zjrb.coreprojectlibrary.db.ThemeMode;
import com.zjrb.coreprojectlibrary.utils.UIUtils;
import com.zjrb.loginproject.R;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this);
        ThemeMode.initTheme(R.style.AppTheme, R.style.NightAppTheme);
        UiModeManager.init(this, R.styleable.SupportUiMode);

    }
}
