package com.daily.news.login.bean;


import com.zjrb.core.domain.base.BaseInnerData;

/**
 * 登录验证bean
 * Created by wanglinjie.
 * create time:2017/8/25  下午4:32
 */

public class ZBLoginBean extends BaseInnerData {

    /**
     * account : {"nick_name":"读友row3j7","ref_code":"row3j7","ref_user_uid":"","ref_user_code":"","mobile":"13965146707","invitation_number":0,"image_url":"http://pic44.nipic.com/20140717/12432466_121957328000_2.jpg","total_score":105}
     * session : {"id":"59b0fb9ae5fe2c6e6069cf22","device_id":"508426669311b3c7772cb6f487ef0937","anonymous":true,"expired":false}
     * score_notify : {"obtained":2,"balance":105,"popup":true,"task":"登录新版本"}
     */

    private AccountBean account;
    private SessionBean session;
    private ScoreNotifyBean score_notify;

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

    public ScoreNotifyBean getScore_notify() {
        return score_notify;
    }

    public void setScore_notify(ScoreNotifyBean score_notify) {
        this.score_notify = score_notify;
    }

    public static class AccountBean {
        /**
         * nick_name : 读友row3j7
         * ref_code : row3j7
         * ref_user_uid :
         * ref_user_code :
         * mobile : 13965146707
         * invitation_number : 0
         * image_url : http://pic44.nipic.com/20140717/12432466_121957328000_2.jpg
         * total_score : 105
         */

        private String nick_name;
        private String ref_code;
        private String ref_user_uid;
        private String ref_user_code;
        private String mobile;
        private int invitation_number;
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

        public String getRef_user_code() {
            return ref_user_code;
        }

        public void setRef_user_code(String ref_user_code) {
            this.ref_user_code = ref_user_code;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getInvitation_number() {
            return invitation_number;
        }

        public void setInvitation_number(int invitation_number) {
            this.invitation_number = invitation_number;
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
        /**
         * id : 59b0fb9ae5fe2c6e6069cf22
         * device_id : 508426669311b3c7772cb6f487ef0937
         * anonymous : true
         * expired : false
         */

        private String id;
        private String device_id;
        private boolean anonymous;
        private boolean expired;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public boolean isAnonymous() {
            return anonymous;
        }

        public void setAnonymous(boolean anonymous) {
            this.anonymous = anonymous;
        }

        public boolean isExpired() {
            return expired;
        }

        public void setExpired(boolean expired) {
            this.expired = expired;
        }
    }

    public static class ScoreNotifyBean {
        /**
         * obtained : 2
         * balance : 105
         * popup : true
         * task : 登录新版本
         */

        private int obtained;
        private int balance;
        private boolean popup;
        private String task;

        public int getObtained() {
            return obtained;
        }

        public void setObtained(int obtained) {
            this.obtained = obtained;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public boolean isPopup() {
            return popup;
        }

        public void setPopup(boolean popup) {
            this.popup = popup;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }
    }
}
