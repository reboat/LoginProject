package com.daily.news.login.task;

import android.content.Context;

import cn.daily.news.biz.core.model.BaseData;
import cn.daily.news.biz.core.network.compatible.APICallBack;
import cn.daily.news.biz.core.network.compatible.APIPostTask;
import cn.daily.news.biz.core.ui.dialog.ZBSingleDialog;
import cn.daily.news.biz.core.ui.toast.ZBToast;

import static com.zjrb.core.utils.UIUtils.getContext;

public class CheckLogOffTask extends APIPostTask<BaseData> {
    public static final int CODE_LOG_OFF = 52004;//已经注销

    public interface OnCheckLogOffCallback {
        void onNormalPhoneNumber();
    }

    public CheckLogOffTask(final Context context, final OnCheckLogOffCallback callback) {
        super(new CheckCallback(callback, context));

    }

    @Override
    public void onSetupParams(Object... params) {
        put("phone_number", params[0]);
    }

    @Override
    public String getApi() {
        return "/api/account/is_logoff";
    }

    private static class CheckCallback extends APICallBack<BaseData> {
        private final OnCheckLogOffCallback mCallback;
        private final Context mContext;

        public CheckCallback(OnCheckLogOffCallback callback, Context context) {
            mCallback = callback;
            mContext = context;
        }

        @Override
        public void onSuccess(BaseData data) {
            if (mCallback != null) {
                mCallback.onNormalPhoneNumber();
            }
        }

        @Override
        public void onError(String errMsg, int errCode) {
            if (errCode == CODE_LOG_OFF) {
                ZBSingleDialog dialog = new ZBSingleDialog(mContext);
                dialog.setBuilder(new ZBSingleDialog.Builder()
                        .setMessage("该帐号已注销，换个帐号试试吧！")
                        .setConfirmText("知道了"));
                dialog.show();
            } else {
                ZBToast.showShort(getContext(), errMsg);
            }
        }
    }
}
