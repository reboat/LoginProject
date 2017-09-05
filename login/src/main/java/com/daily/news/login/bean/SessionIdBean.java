package com.daily.news.login.bean;

import com.zjrb.core.domain.base.BaseInnerData;

/**
 * Created by wanglinjie.
 * create time:2017/9/5  下午3:39
 */

public class SessionIdBean  extends BaseInnerData {

    /**
     * id : 5994f537aab7bc32347410b7
     * anonymous : true
     */

    private SessionBean session;

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
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
