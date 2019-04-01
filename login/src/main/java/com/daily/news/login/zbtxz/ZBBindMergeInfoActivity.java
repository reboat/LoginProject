package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daily.news.login.LoginMainActivity;
import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.task.UserAccountMergeTask;
import com.zjrb.core.utils.AppManager;
import com.zjrb.core.utils.T;
import com.zjrb.core.utils.click.ClickTracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.daily.news.biz.core.DailyActivity;
import cn.daily.news.biz.core.UserBiz;
import cn.daily.news.biz.core.model.MultiAccountBean;
import cn.daily.news.biz.core.model.ZBLoginBean;
import cn.daily.news.biz.core.network.compatible.APIExpandCallBack;
import cn.daily.news.biz.core.ui.toolsbar.BIZTopBarFactory;

/**
 * Date: 2018/9/03
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 绑定账号信息合并界面
 */
public class ZBBindMergeInfoActivity extends DailyActivity {

    @BindView(R2.id.tv_bind_second_desc)
    TextView mTvBindDescSecond;
    @BindView(R2.id.user_info_top)
    LinearLayout mInfoTop;
    @BindView(R2.id.user_info_bottom)
    LinearLayout mInfoBottom;
    @BindView(R2.id.tv_quit)
    Button mBtnQuit;
    @BindView(R2.id.tv_bind)
    Button mBtnBind;

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
    private String mSelectId;
    private String mUnSelectedId;
    private String mobile;
    private String sessionId;

    /**
     * @param intent 获取intent数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            multiAccountBean = (MultiAccountBean) intent.getSerializableExtra("merge_data");
            mobile = intent.getStringExtra("merge_phone");
            sessionId = intent.getStringExtra("merge_sessionId");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        setContentView(R.layout.module_login_merge);
        ButterKnife.bind(this);
        initView();
        refreshView(multiAccountBean);
    }

    private void refreshView(MultiAccountBean bean) {
        if (bean == null) {
            return;
        }
        setBindBtnState();
        final MultiAccountBean.AccountBean currentAccount = bean.getCurrent_account();
        final MultiAccountBean.AccountBean candidateAccount = bean.getCandidate_account();
        // 上面为候选账号
        if (candidateAccount != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.default_user_icon);
            options.centerCrop();
            options.circleCrop();
            Glide.with(this).load(candidateAccount.getImage_url()).apply(options).into(mUserLogoTop);
            mUserNameTop.setText(candidateAccount.getNick_name());
            mUserScoreNumTop.setText(String.valueOf(candidateAccount.getTotal_score()));
            mUserFavNumTop.setText(String.valueOf(candidateAccount.getFavorite_size()));
            mUserCommentNumTop.setText(String.valueOf(candidateAccount.getComment_size()));
            MultiAccountBean.BindingLogMapBean topMap = candidateAccount.getBinding_logo_map();
            if (topMap != null) {
                mIvMobile.setVisibility(topMap.phone_number ? View.VISIBLE : View.GONE);
                mIvQQ.setVisibility(topMap.qq ? View.VISIBLE : View.GONE);
                mIvWeibo.setVisibility(topMap.wei_bo ? View.VISIBLE : View.GONE);
                mIvWeixin.setVisibility(topMap.wei_xin ? View.VISIBLE : View.GONE);
            }
        }
        // 下面为当前登录账号
        if (currentAccount != null) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.mipmap.default_user_icon);
            options.centerCrop();
            options.circleCrop();
            Glide.with(this).load(currentAccount.getImage_url()).apply(options).into(mUserLogoBottom);
            mUserNameBottom.setText(currentAccount.getNick_name());
            mUserScoreNumBottom.setText(String.valueOf(currentAccount.getTotal_score()));
            mUserFavNumBottom.setText(String.valueOf(currentAccount.getFavorite_size()));
            mUserCommentNumBottom.setText(String.valueOf(currentAccount.getComment_size()));
            MultiAccountBean.BindingLogMapBean bottomMap = currentAccount.getBinding_logo_map();
            if (bottomMap != null) {
                mIvMobileBottom.setVisibility(bottomMap.phone_number ? View.VISIBLE : View.GONE);
                mIvQQBottom.setVisibility(bottomMap.qq ? View.VISIBLE : View.GONE);
                mIvWeiboBottom.setVisibility(bottomMap.wei_bo ? View.VISIBLE : View.GONE);
                mIvWeixinBottom.setVisibility(bottomMap.wei_xin ? View.VISIBLE : View.GONE);
            }
        }
        mCbTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbBottom.setChecked(false);
//                    mSelectBean = candidateAccount;
                    mSelectId = candidateAccount.getPassport_id();
                    mUnSelectedId = currentAccount.getPassport_id();
                }
                setBindBtnState();
            }
        });

        mCbBottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbTop.setChecked(false);
//                    mSelectBean = currentAccount;
                    mSelectId = currentAccount.getPassport_id();
                    mUnSelectedId = candidateAccount.getPassport_id();
                }
                setBindBtnState();
            }
        });
    }

    /**
     * 设置绑定按钮的选中态
     */
    private void setBindBtnState() {
        if (mCbTop.isChecked() || mCbBottom.isChecked()) {
            mBtnBind.setEnabled(true);
        } else {
            mBtnBind.setEnabled(false);
        }
    }

    /**
     * 初始化标题
     */
    private void initView() {
        mTvBindDescSecond.setText(String.format(getResources().getString(R.string.zb_mobile_bind_desc_second), mobile));
        mUserLogoTop = (ImageView) mInfoTop.findViewById(R.id.iv_logo);
        mUserNameTop = (TextView) mInfoTop.findViewById(R.id.tv_name);
        mUserScoreTop = (TextView) mInfoTop.findViewById(R.id.tv_score);
        mUserFavTop = (TextView) mInfoTop.findViewById(R.id.tv_fav);
//        mCurrentAccountTop = (TextView) mInfoTop.findViewById(R.id.tv_current_account);
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
//        mCurrentAccountBottom = (TextView) mInfoBottom.findViewById(R.id.tv_current_account);
        mUserCommentBottom = (TextView) mInfoBottom.findViewById(R.id.tv_comment);
        mUserScoreNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_score_num);
        mUserFavNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_fav_num);
        mUserCommentNumBottom = (TextView) mInfoBottom.findViewById(R.id.tv_comment_num);
        mIvMobileBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_mobile);
        mIvQQBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_qq);
        mIvWeixinBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_weixin);
        mIvWeiboBottom = (ImageView) mInfoBottom.findViewById(R.id.iv_weibo);
        mCbBottom = (CheckBox) mInfoBottom.findViewById(R.id.cb_userinfo);

//        mCurrentAccountTop.setVisibility(View.GONE);
//        mCurrentAccountBottom.setVisibility(View.VISIBLE);
        mBtnQuit.setText(getResources().getString(R.string.zb_mobile_bind_quit));
        mBtnBind.setText(getResources().getString(R.string.zb_mobile_bind_confirm));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return BIZTopBarFactory.createDefaultForLogin(view, this).getView();
    }


    @Override
    public void finish() {
        super.finish();
    }

    @OnClick({R2.id.tv_quit, R2.id.tv_bind})
    public void onClick(View v) {
        if (ClickTracker.isDoubleClick()) return;
        if (v.getId() == R.id.tv_quit) {
            finish();
        } else if (v.getId() == R.id.tv_bind) {
            bindAccount();
        }
    }

    /**
     * 账号合并页面确认绑定功能
     */
    private void bindAccount() {
        new UserAccountMergeTask(new APIExpandCallBack<ZBLoginBean>() {
            @Override
            public void onSuccess(ZBLoginBean data) {
                T.showShort(ZBBindMergeInfoActivity.this, getResources().getString(R.string.zb_mobile_bind_success_tip));
                Intent intent = new Intent("bind_mobile_successful");
                LocalBroadcastManager.getInstance(ZBBindMergeInfoActivity.this).sendBroadcast(intent);
                finish();
                AppManager.get().finishActivity(ZBBindMobileActivity.class); // 注意: 这里因为在bind界面的返回做了logout的操作,所以重设数据要在这个步骤之后
                UserBiz userBiz = UserBiz.get();
                userBiz.setZBLoginBean(data);
                AppManager.get().finishActivity(LoginMainActivity.class);

            }

            @Override
            public void onError(String errMsg, int errCode) {
                super.onError(errMsg, errCode);
                T.showShort(ZBBindMergeInfoActivity.this, errMsg);
            }
        }).setTag(this).exe(mUnSelectedId + "", mSelectId + "", sessionId);

    }
}
