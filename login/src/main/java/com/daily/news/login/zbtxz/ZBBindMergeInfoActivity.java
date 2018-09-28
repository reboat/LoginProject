package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.bean.MultiAccountBean;
import com.daily.news.login.task.UserAccountMergeTask;
import com.zjrb.core.api.callback.APIExpandCallBack;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.common.biz.UserBiz;
import com.zjrb.core.domain.ZBLoginBean;
import com.zjrb.core.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date: 2018/9/03
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 绑定账号信息合并界面
 */
public class ZBBindMergeInfoActivity extends BaseActivity {

    @BindView(R2.id.tv_bind_desc)
    TextView mTvBindDesc;
    @BindView(R2.id.iv_logo)
    ImageView mIvLogo;
    @BindView(R2.id.user_info_top)
    LinearLayout mInfoTop;
    @BindView(R2.id.user_info_bottom)
    LinearLayout mInfoBottom;
    @BindView(R2.id.tv_quit)
    TextView mTvQuit;
    @BindView(R2.id.tv_bind)
    TextView mTvBind;

    ImageView mUserLogoTop;
    ImageView mUserLogoBottom;

    ImageView mIvMobile;
    ImageView mIvQQ;
    ImageView mIvWeixin;
    ImageView mIvWeibo;

    ImageView mIvMobileBottom;
    ImageView mIvQQBottom;
    ImageView mIvWeixinBottom;
    ImageView mIvWeiboBottom;

    TextView mUserNameTop; // 用户名
    TextView mUserNameBottom;

    TextView mUserScoreTop; // 积分
    TextView mUserScoreBottom;
    TextView mUserScoreNumTop;
    TextView mUserScoreNumBottom;

    TextView mUserFavTop; // 收藏
    TextView mUserFavBottom;
    TextView mUserFavNumTop;
    TextView mUserFavNumBottom;

    TextView mUserCommentTop; // 评论
    TextView mUserCommentBottom;
    TextView mUserCommentNumTop;
    TextView mUserCommentNumBottom;

    CheckBox mCbTop;
    CheckBox mCbBottom;

    private MultiAccountBean multiAccountBean;
    private MultiAccountBean.AccountBean mSelectBean;
    private String mSelectId;
    private String mUnSelectedId;
    private int authType = 1;
    private String authUid;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            multiAccountBean = (MultiAccountBean) intent.getSerializableExtra("merge_data");
//            if (intent.hasExtra("auth_type")) {
//                authType = intent.getIntExtra("auth_type", 0);
//            }
//            if (intent.hasExtra("auth_uid")) {
//                authUid = intent.getStringExtra("auth_uid");
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        setContentView(R.layout.module_login_merge);
        getIntentData(getIntent());
        ButterKnife.bind(this);
        initView();
        refreshView(multiAccountBean);
//        initData();
    }

//    private void initData() {
//        new GetMuitiAccountTask(new APIExpandCallBack<MultiAccountBean>() {
//
//            @Override
//            public void onSuccess(MultiAccountBean data) {
//                if (data != null) {
//                    multiAccountBean = data;
//                    refreshView(multiAccountBean);
//                }
//            }
//
//            @Override
//            public void onError(String errMsg, int errCode) {
//                super.onError(errMsg, errCode);
//            }
//        }).setTag(this).exe(authType, authUid);
//
//    }

    private void refreshView(MultiAccountBean bean) {
        if (bean == null) {
            return;
        }
        final MultiAccountBean.AccountBean firstAccount = bean.getFirst_account();
        final MultiAccountBean.AccountBean secondAccount = bean.getSecond_account();
        if (firstAccount != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.default_user_icon);
            options.centerCrop();
            options.circleCrop();
            Glide.with(this).load(firstAccount.getImage_url()).apply(options).into(mUserLogoTop);
            mUserNameTop.setText(firstAccount.getNick_name());
            mUserScoreNumTop.setText(firstAccount.getTotal_score());
            mUserFavNumTop.setText(firstAccount.getNews_favorite_size());
            mUserCommentNumTop.setText(firstAccount.getComment_size());

        }

        if (secondAccount != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.default_user_icon);
            options.centerCrop();
            options.circleCrop();
            Glide.with(this).load(secondAccount.getImage_url()).apply(options).into(mUserLogoTop);
            mUserNameTop.setText(firstAccount.getNick_name());
            mUserScoreNumTop.setText(firstAccount.getTotal_score());
            mUserFavNumTop.setText(firstAccount.getNews_favorite_size());
            mUserCommentNumTop.setText(firstAccount.getComment_size());
        }
        mCbTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbBottom.setChecked(false);
                    mSelectBean = firstAccount;
                    mSelectId = firstAccount.getId();
                    mUnSelectedId = secondAccount.getId();
                }
            }
        });

        mCbBottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbTop.setChecked(false);
                    mSelectBean = secondAccount;
                    mSelectId = secondAccount.getId();
                    mUnSelectedId = firstAccount.getId();
                }
            }
        });

    }

    /**
     * 初始化标题
     */
    private void initView() {
        mUserLogoTop = (ImageView) mInfoTop.findViewById(R.id.iv_logo);
        mUserNameTop = (TextView) mInfoTop.findViewById(R.id.tv_name);
        mUserScoreTop = (TextView) mInfoTop.findViewById(R.id.tv_score);
        mUserFavTop = (TextView) mInfoTop.findViewById(R.id.tv_fav);
        mUserCommentTop = (TextView) mInfoTop.findViewById(R.id.tv_comment);
        mUserScoreNumTop = (TextView) mInfoTop.findViewById(R.id.tv_score_num);
        mUserFavNumTop = (TextView) mInfoTop.findViewById(R.id.tv_fav_num);
        mUserCommentNumTop = (TextView) mInfoTop.findViewById(R.id.tv_comment_num);
        mIvMobile = (ImageView) mInfoTop.findViewById(R.id.iv_mobile);
        mIvQQ = (ImageView) mInfoTop.findViewById(R.id.iv_qq);
        mIvWeixin = (ImageView) mInfoTop.findViewById(R.id.iv_weixin);
        mIvWeibo = (ImageView) mInfoTop.findViewById(R.id.iv_weibo);
        mCbTop = (CheckBox) mInfoTop.findViewById(R.id.cb_userinfo);


        mUserLogoBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_logo);
        mUserNameBottom = (TextView) mInfoBottom.findViewById(R.id.tv_name);
        mUserScoreBottom = (TextView) mInfoBottom.findViewById(R.id.tv_score);
        mUserFavBottom = (TextView) mInfoBottom.findViewById(R.id.tv_fav);
        mUserCommentBottom = (TextView) mInfoBottom.findViewById(R.id.tv_comment);
        mUserScoreNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_score_num);
        mUserFavNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_fav_num);
        mUserCommentNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_comment_num);
        mIvMobileBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_mobile);
        mIvQQBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_qq);
        mIvWeixinBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_weixin);
        mIvWeiboBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_weibo);
        mCbBottom = (CheckBox) mInfoBottom.findViewById(R.id.cb_userinfo);

        // TODO: 2018/9/19 设置 确认绑定按钮的选中态  设置背景
        if (mCbTop.isChecked() || mCbBottom.isChecked()) {
            mTvBind.setClickable(true);

        } else {
            mTvBind.setClickable(false);
            // TODO: 2018/9/19 设置背景
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this, getString(R.string.zb_mobile_bind_title))
                .getView();
    }


    @Override
    public void finish() {
        super.finish();
    }

    @OnClick({R2.id.tv_quit, R2.id.tv_bind})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R2.id.tv_quit:
                finish();
                break;
            case R2.id.tv_bind:
                bindAccount();
                break;
        }
    }

    /**
     * 账号合并页面确认绑定功能
     */
    private void bindAccount() {
        new UserAccountMergeTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onSuccess(ZBLoginBean data) {
                UserBiz userBiz = UserBiz.get();
                userBiz.setZBLoginBean(data);
                T.showShort(ZBBindMergeInfoActivity.this, getResources().getString(R.string.zb_mobile_bind_success_tip));
            }

            @Override
            public void onError(String errMsg, int errCode) {
                super.onError(errMsg, errCode);
                T.showShort(ZBBindMergeInfoActivity.this, errMsg);
            }
        }).setTag(this).exe(mSelectId, mUnSelectedId);
    }
}
