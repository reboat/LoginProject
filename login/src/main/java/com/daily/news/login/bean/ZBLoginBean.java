package com.daily.news.login.bean;


import com.zjrb.core.domain.base.BaseInnerData;

/**
 * 登录验证bean
 * Created by wanglinjie.
 * create time:2017/8/25  下午4:32
 */

public class ZBLoginBean extends BaseInnerData {


    /**
     * nick_name : 读者_3omlsl22_1
     * ref_code : 4ozcpr
     * ref_user_uid :
     * image_url : http://10.100.60.98:81/g1/M000000CmQ8Ylmb_cOAJ3wKAAVEJLF8cIk522.png?w=719&h=719
     * total_score : 12
     */

    private AccountBean account;
    /**
     * id : 59a01976f7bf514470e34072
     * anonymous : false
     */

    private SessionBean session;

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public static class AccountBean {
        private String nick_name;
        private String ref_code;
        private String ref_user_uid;
        private String image_url;
        private int total_score;

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getRef_code() {
            return ref_code;
        }

        public void setRef_code(String ref_code) {
            this.ref_code = ref_code;
        }

        public String getRef_user_uid() {
            return ref_user_uid;
        }

        public void setRef_user_uid(String ref_user_uid) {
            this.ref_user_uid = ref_user_uid;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getTotal_score() {
            return total_score;
        }

        public void setTotal_score(int total_score) {
            this.total_score = total_score;
        }
    }

    public static class SessionBean {
        private String id;
        private boolean anonymous;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isAnonymous() {
            return anonymous;
        }

        public void setAnonymous(boolean anonymous) {
            this.anonymous = anonymous;
        }
    }
}
