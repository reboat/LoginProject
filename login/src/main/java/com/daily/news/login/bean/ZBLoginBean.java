package com.daily.news.login.bean;


import com.zjrb.core.domain.base.BaseInnerData;

/**
 * 登录验证bean
 * Created by wanglinjie.
 * create time:2017/8/25  下午4:32
 */

public class ZBLoginBean extends BaseInnerData {

    /**
     * id : 59996a9f6f740a2950bf3302
     * nick_name : 秋天来了
     * last_accessed : 1503227489000
     * ref_code : zoz73i
     * ref_user_uid :
     * auth_type : QQ
     * auth_uid : SIN599557835
     * mobile :
     * forbidden : false
     * created_at : 1503226527000
     * invitation_number : 0
     * address :
     * portrait_id : 9
     * image_url : https://www.baidu.com/s?rsv_idx=1&wd=%E8%BD%AF%E4%BB%B6%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1&ie=utf-8&rsv_cq=mybatis+%E6%89%B9%E5%A4%84%E7%90%86&rsv_dl=0_right_recommends_merge_21180&euri=ac7ba1cfb121481483f05744f83a726e
     * continuous_days : 0
     * updated_at : 1503304418000
     * nav_version : 1
     * nav :
     * privilege_ids : 10,11
     * proposal_words :
     * daily_hot_spot_flag : 1
     * local_news_flag : 0
     * activity_messages_flag : 0
     * current_version : 1
     * anonymous : false
     * location_city :
     * total_score : 205
     * union_id : sdfdfsdfsd456dgdfzfz
     */

    private AccountBean account;

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

    public static class AccountBean {
        private String session_id;
        private String id;
        private String nick_name;
        private long last_accessed;
        private String ref_code;
        private String ref_user_uid;
        private String auth_type;
        private String auth_uid;
        private String mobile;
        private boolean forbidden;
        private long created_at;
        private int invitation_number;
        private String address;
        private int portrait_id;
        private String image_url;
        private int continuous_days;
        private long updated_at;
        private int nav_version;
        private String nav;
        private String privilege_ids;
        private String proposal_words;
        private int daily_hot_spot_flag;
        private int local_news_flag;
        private int activity_messages_flag;
        private int current_version;
        private boolean anonymous;
        private String location_city;
        private int total_score;
        private String union_id;

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public long getLast_accessed() {
            return last_accessed;
        }

        public void setLast_accessed(long last_accessed) {
            this.last_accessed = last_accessed;
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

        public String getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(String auth_type) {
            this.auth_type = auth_type;
        }

        public String getAuth_uid() {
            return auth_uid;
        }

        public void setAuth_uid(String auth_uid) {
            this.auth_uid = auth_uid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public boolean isForbidden() {
            return forbidden;
        }

        public void setForbidden(boolean forbidden) {
            this.forbidden = forbidden;
        }

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }

        public int getInvitation_number() {
            return invitation_number;
        }

        public void setInvitation_number(int invitation_number) {
            this.invitation_number = invitation_number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPortrait_id() {
            return portrait_id;
        }

        public void setPortrait_id(int portrait_id) {
            this.portrait_id = portrait_id;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getContinuous_days() {
            return continuous_days;
        }

        public void setContinuous_days(int continuous_days) {
            this.continuous_days = continuous_days;
        }

        public long getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(long updated_at) {
            this.updated_at = updated_at;
        }

        public int getNav_version() {
            return nav_version;
        }

        public void setNav_version(int nav_version) {
            this.nav_version = nav_version;
        }

        public String getNav() {
            return nav;
        }

        public void setNav(String nav) {
            this.nav = nav;
        }

        public String getPrivilege_ids() {
            return privilege_ids;
        }

        public void setPrivilege_ids(String privilege_ids) {
            this.privilege_ids = privilege_ids;
        }

        public String getProposal_words() {
            return proposal_words;
        }

        public void setProposal_words(String proposal_words) {
            this.proposal_words = proposal_words;
        }

        public int getDaily_hot_spot_flag() {
            return daily_hot_spot_flag;
        }

        public void setDaily_hot_spot_flag(int daily_hot_spot_flag) {
            this.daily_hot_spot_flag = daily_hot_spot_flag;
        }

        public int getLocal_news_flag() {
            return local_news_flag;
        }

        public void setLocal_news_flag(int local_news_flag) {
            this.local_news_flag = local_news_flag;
        }

        public int getActivity_messages_flag() {
            return activity_messages_flag;
        }

        public void setActivity_messages_flag(int activity_messages_flag) {
            this.activity_messages_flag = activity_messages_flag;
        }

        public int getCurrent_version() {
            return current_version;
        }

        public void setCurrent_version(int current_version) {
            this.current_version = current_version;
        }

        public boolean isAnonymous() {
            return anonymous;
        }

        public void setAnonymous(boolean anonymous) {
            this.anonymous = anonymous;
        }

        public String getLocation_city() {
            return location_city;
        }

        public void setLocation_city(String location_city) {
            this.location_city = location_city;
        }

        public int getTotal_score() {
            return total_score;
        }

        public void setTotal_score(int total_score) {
            this.total_score = total_score;
        }

        public String getUnion_id() {
            return union_id;
        }

        public void setUnion_id(String union_id) {
            this.union_id = union_id;
        }
    }
}
