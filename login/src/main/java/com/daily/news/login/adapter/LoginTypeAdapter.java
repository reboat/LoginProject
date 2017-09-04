package com.daily.news.login.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daily.news.login.R;
import com.daily.news.login.bean.LoginTypeBean;
import com.zjrb.core.common.base.BaseRecyclerAdapter;
import com.zjrb.core.common.base.BaseRecyclerViewHolder;
import com.zjrb.core.utils.UIUtils;

import java.util.List;


/**
 * 登陆页登录方式适配器
 * Created by wanglinjie.
 * create time:2017/8/10  下午8:39
 */

public final class LoginTypeAdapter extends BaseRecyclerAdapter {


    /**
     * @param datas 传入集合数据
     */
    public LoginTypeAdapter(List datas) {
        super(datas);
    }

    @Override
    public DetailShareViewHolder onAbsCreateViewHolder(ViewGroup parent, int viewType) {
        return new DetailShareViewHolder(UIUtils.inflate(R.layout.module_login_type_item, parent, false));
    }

    public class DetailShareViewHolder extends BaseRecyclerViewHolder<LoginTypeBean> {
        private TextView tv_title;
        private ImageView iv_img;

        public DetailShareViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_login_name);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_login_image);
        }

        @Override
        public void bindView() {
            tv_title.setText(mData.getType());
            iv_img.setImageResource(mData.getResId());
        }
    }
}
