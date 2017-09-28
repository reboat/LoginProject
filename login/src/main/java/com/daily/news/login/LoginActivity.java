package com.daily.news.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.api.LoginHelper;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面
 * Created by wanglinjie.
 * create time:2017/8/19  下午20:14
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

    /**
     * 请求码
     */
    private int REQUEST_CODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_login_login);
        ButterKnife.bind(this);
        initLoginRV();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_login)).getView();
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
            Nav.with(this).toPath("/login/ZBRegisterActivity", REQUEST_CODE);
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data        第三方登录回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            setResult(RESULT_OK);
            finish();
        } else {
            if (mUmengUtils != null) {
                mUmengUtils.getDialog();
                mUmengUtils.onResult(requestCode, resultCode, data);
                finish();
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        LoginHelper.get().finish();
    }

    @OnClick({R2.id.ll_module_login_zbtxz, R2.id.ll_module_login_wx, R2.id.ll_module_login_qq, R2.id.ll_module_login_wb})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.ll_module_login_zbtxz) {
            //进入浙报通行证页面
            Nav.with(this).toPath("/login/ZBLoginActivity");
        } else if (i == R.id.ll_module_login_wx) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.WEIXIN);
        } else if (i == R.id.ll_module_login_qq) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.QQ);
        } else if (i == R.id.ll_module_login_wb) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.SINA);
        }
    }
}

