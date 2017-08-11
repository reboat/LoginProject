package com.daily.news.login;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.daily.news.login.adapter.LoginTypeAdapter;
import com.daily.news.login.bean.LoginTypeBean;
import com.zjrb.coreprojectlibrary.common.base.BaseActivity;
import com.zjrb.coreprojectlibrary.common.base.toolbar.ToolBarFactory;
import com.zjrb.coreprojectlibrary.common.listener.IOnItemClickListener;
import com.zjrb.coreprojectlibrary.ui.widget.divider.GridSpaceDivider;
import com.zjrb.coreprojectlibrary.utils.UIUtils;
import com.zjrb.coreprojectlibrary.utils.click.ClickTracker;

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

@Route(path = "/module/login/LoginActivity")
public class LoginActivity extends BaseActivity implements IOnItemClickListener<LoginTypeBean> {

    @BindView(R2.id.rv_list)
    RecyclerView mRecyleView;
    @BindView(R2.id.tv_register)
    TextView mTvLogin;

    private List<LoginTypeBean> mBean;
    private LoginTypeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.module_login_login);
        ButterKnife.bind(this);
        initLoginRV();
    }

    @Override
    protected void onSetUpToolBar(Toolbar toolbar, ActionBar actionBar) {
        ToolBarFactory.createStyle1(this, toolbar, getString(R.string.module_login_toolbar));
    }

    /**
     * 初始化滚动列表数据
     */
    private void initLoginRV() {

        mRecyleView.addItemDecoration(new GridSpaceDivider(0));
        GridLayoutManager managerFollow = new GridLayoutManager(UIUtils.getContext(), 4);
        mRecyleView.setLayoutManager(managerFollow);

        if (mBean == null) {
            mBean = new ArrayList<>();
            mBean.add(new LoginTypeBean(R.mipmap.ic_launcher, "浙报通行证"));
            mBean.add(new LoginTypeBean(R.mipmap.me_wechat_btn, "微信"));
            mBean.add(new LoginTypeBean(R.mipmap.me_qq_btn, "QQ"));
            mBean.add(new LoginTypeBean(R.mipmap.me_sina_btn, "微博"));
        }

        mAdapter = new LoginTypeAdapter(mBean);
        mRecyleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    /**
     * @param v
     * 点击注册
     */
    @OnClick({R2.id.tv_register})
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        switch (v.getId()) {
            case R2.id.tv_register:
                break;
        }
    }

    /**
     * 第三方跳转登录
     *
     * @param itemView
     * @param position
     * @param data
     */
    @Override
    public void onItemClick(View itemView, int position, LoginTypeBean data) {

    }
}

