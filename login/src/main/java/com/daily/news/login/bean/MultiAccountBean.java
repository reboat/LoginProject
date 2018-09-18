package com.daily.news.login.bean;

import com.zjrb.core.domain.base.BaseData;

/**
 * Date: 2018/9/6 上午10:28
 * Email: sisq@8531.cn
 * Author: sishuqun
 * Description: 多账号实体类
 */
public class MultiAccountBean extends BaseData{


    private AccountBean first_account;
    private AccountBean second_account;

    public AccountBean getFirst_account() {
        return first_account;
    }

    public void setFirst_account(AccountBean first_account) {
        this.first_account = first_account;
    }

    public AccountBean getSecond_account() {
        return second_account;
    }

    public void setSecond_account(AccountBean second_account) {
        this.second_account = second_account;
    }

    public static class AccountBean {
        /**
         * current_account : true
         * news_favorite_size : 10
         * image_url : https://stcbeta.8531.cn/assets/20180716/1531723722263_5b4c3fca9c880e7cb0b0dbf1.jpeg
         * binging_log_list : ["https://wx.qq.com/logo.png"]
         * nick_name : 读友_3883500
         * total_score : 0
         * comment_size : 20
         * id : 542954b5498eb93760a423f2
         */

        private boolean current_account;
        private int news_favorite_size;
        private String image_url;
        private String nick_name;
        private int total_score;
        private int comment_size;
        private String id;
        private BindingLogMapBean binding_log_map;

        public boolean isCurrent_account() {
            return current_account;
        }

        public void setCurrent_account(boolean current_account) {
            this.current_account = current_account;
        }

        public int getNews_favorite_size() {
            return news_favorite_size;
        }

        public void setNews_favorite_size(int news_favorite_size) {
            this.news_favorite_size = news_favorite_size;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public int getTotal_score() {
            return total_score;
        }

        public void setTotal_score(int total_score) {
            this.total_score = total_score;
        }

        public int getComment_size() {
            return comment_size;
        }

        public void setComment_size(int comment_size) {
            this.comment_size = comment_size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public BindingLogMapBean getBinding_log_map() {
            return binding_log_map;
        }

        public void setBinding_log_map(BindingLogMapBean binding_log_map) {
            this.binding_log_map = binding_log_map;
        }
    }

    public static class BindingLogMapBean {
        public boolean wei_bo;
        public boolean qq;
        public boolean wei_xin;
        public boolean phone_number;
    }

}
