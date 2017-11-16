package com.daily.news.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.common.global.IKey;
import com.zjrb.core.common.global.RouteManager;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录入口页面，所有登录必须通过该入口
 *
 * @author a_liYa
 * @date 2017/9/29 上午11:00.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R2.id.tv_register)
    TextView tvRegister;
    @BindView(R2.id.tv_module_login_zbtxz)
    TextView tvModuleLoginZbtxz;
    @BindView(R2.id.tv_module_login_wx)
    TextView tvModuleLoginWx;
    @BindView(R2.id.tv_module_login_qq)
    TextView tvModuleLoginQq;
    @BindView(R2.id.tv_module_login_wb)
    TextView tvModuleLoginWb;

    private UmengAuthUtils mUmengUtils;

    private boolean isFromComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_login);
        getIntentData(getIntent());
        ButterKnife.bind(this);
        initLoginRV();
        LoginHelper.get().setLogin(true); // 标记开启
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_login)).getView();
    }

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(IKey.IS_COMMENT_ACTIVITY)) {
                isFromComment = intent.getBooleanExtra(IKey.IS_COMMENT_ACTIVITY, false);
            }
        }
    }

    /**
     * 初始化滚动列表数据
     */
    private void initLoginRV() {
        tvRegister.setText(getString(R.string.zb_register_toolbar));
        tvModuleLoginZbtxz.setText(getString(R.string.zb_login_type_zb));
        tvModuleLoginWx.setText(getString(R.string.zb_login_type_wx));
        tvModuleLoginQq.setText(getString(R.string.zb_login_type_qq));
        tvModuleLoginWb.setText(getString(R.string.zb_login_type_wb));
    }


    /**
     * @param v 点击注册
     */
    @OnClick({R2.id.tv_register})
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        if (v.getId() == R.id.tv_register) {
            Nav.with(this).toPath(RouteManager.ZB_REGISTER);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUmengUtils != null) {
            mUmengUtils.onResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        //TODO WLJ 4.4
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("login_successful"));
        if (UserBiz.get().isLoginUser()) {
            setResult(Activity.RESULT_OK);
        }
        super.finish();
        LoginHelper.get().finish(); // 登录结束

    }

    private Bundle bundle;

    @OnClick({R2.id.ll_module_login_zbtxz, R2.id.ll_module_login_wx, R2.id.ll_module_login_qq,
            R2.id.ll_module_login_wb})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (i == R.id.ll_module_login_zbtxz) {
            //进入浙报通行证页面
            bundle.putBoolean(IKey.IS_COMMENT_ACTIVITY, isFromComment);
            Nav.with(this).setExtras(bundle).toPath(RouteManager.ZB_LOGIN);
        } else if (i == R.id.ll_module_login_wx) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.WEIXIN, isFromComment);
        } else if (i == R.id.ll_module_login_qq) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.QQ, isFromComment);
        } else if (i == R.id.ll_module_login_wb) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.SINA, isFromComment);
        }
    }
}

