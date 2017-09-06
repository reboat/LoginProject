package com.daily.news.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.news.login.adapter.LoginTypeAdapter;
import com.daily.news.login.bean.LoginTypeBean;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.adapter.OnItemClickListener;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.UmengUtils.UmengAuthUtils;
import com.zjrb.core.ui.widget.divider.GridSpaceDivider;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.core.utils.click.ClickTracker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面
 * Created by wanglinjie.
 * create time:2017/8/19  下午20:14
 */

public class LoginActivity extends BaseActivity implements OnItemClickListener {

    @BindView(R2.id.rv_list)
    RecyclerView mRecyleView;
    @BindView(R2.id.tv_register)
    TextView tvRegister;

    private List<LoginTypeBean> mBean;
    private LoginTypeAdapter mAdapter;
    private UmengAuthUtils mUmengUtils;

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
        mRecyleView.addItemDecoration(new GridSpaceDivider(0));
        GridLayoutManager managerFollow = new GridLayoutManager(UIUtils.getContext(), 4);
        mRecyleView.setLayoutManager(managerFollow);

        if (mBean == null) {
            mBean = new ArrayList<>();
            if (!ThemeMode.isNightMode()) {
                mBean.add(new LoginTypeBean(R.mipmap.module_login_day_zbtxz, getString(R.string.zb_login_type_zb)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_day_wx, getString(R.string.zb_login_type_wx)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_day_qq, getString(R.string.zb_login_type_qq)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_day_wb, getString(R.string.zb_login_type_wb)));
            } else {
                mBean.add(new LoginTypeBean(R.mipmap.module_login_night_zbtxz, getString(R.string.zb_login_type_zb)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_night_wx, getString(R.string.zb_login_type_wx)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_night_qq, getString(R.string.zb_login_type_qq)));
                mBean.add(new LoginTypeBean(R.mipmap.module_login_night_wb, getString(R.string.zb_login_type_wb)));
            }
        }

        mAdapter = new LoginTypeAdapter(mBean);
        mRecyleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    /**
     * @param v 点击注册
     */
    @OnClick({R2.id.tv_register})
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        if (v.getId() == R.id.tv_register) {
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBRegisterActivity")
                    .buildUpon()
                    .build(), 0);
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
        //微信不走这里
        if (mUmengUtils != null) {
            mUmengUtils.getDialog();
            mUmengUtils.onResult(requestCode, resultCode, data);
        }
    }

    /**
     * 第三方跳转登录
     *
     * @param itemView
     * @param position
     */
    @Override
    public void onItemClick(View itemView, int position) {
        if (position == 0) {
            //进入浙报通行证页面
            Nav.with(this).to(Uri.parse("http://www.8531.cn/login/ZBLoginActivity")
                    .buildUpon()
                    .build(), 0);
        } else if (position == 1) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.WEIXIN);
        } else if (position == 2) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.QQ);
        } else if (position == 3) {
            mUmengUtils = new UmengAuthUtils(this, SHARE_MEDIA.SINA);
        }
    }
}

