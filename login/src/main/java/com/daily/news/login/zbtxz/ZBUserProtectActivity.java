package com.daily.news.login.zbtxz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.daily.news.login.R;
import com.daily.news.login.R2;
import com.daily.news.login.global.C;
import com.zjrb.core.common.base.BaseActivity;
import com.zjrb.core.common.base.toolbar.TopBarFactory;
import com.zjrb.core.db.ThemeMode;
import com.zjrb.core.nav.Nav;
import com.zjrb.core.ui.widget.ZBWebView;
import com.zjrb.core.utils.AppUtils;
import com.zjrb.core.utils.UIUtils;
import com.zjrb.core.utils.webjs.WebJsInterface;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户协议页面
 * Created by wanglinjie.
 * create time:2017/10/26  下午7:50
 */

public class ZBUserProtectActivity extends BaseActivity {

    @BindView(R2.id.web_view)
    ZBWebView webView;

    private WebJsInterface mWebJsInterface;
    private String urlData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        setContentView(R.layout.module_login_user_protect);
        ButterKnife.bind(this);
        initWebView();
        setCssJSWebView(urlData);
    }

    @Override
    protected View onCreateTopBar(ViewGroup view) {
        return TopBarFactory.createDefault(view, this,"用户协议").getView();
    }

    /**
     * @param intent 获取传递数据
     */
    private void getIntentData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("url")) {
                urlData = intent.getExtras().getString("url");
            }
        }
    }

    /**
     * 设置CSS和JS
     */
    private void setCssJSWebView(String urlData) {
        String htmlCode = AppUtils.getAssetsText(C.HTML_RULE_PATH);
        String uiModeCssUri = ThemeMode.isNightMode()
                ? C.NIGHT_CSS_URI : C.DAY_CSS_URI;
        String htmlBody = WebBiz.parseHandleHtml(TextUtils.isEmpty(urlData) ? "" : urlData,
                new WebBiz.ImgSrcsCallBack() {
                    @Override
                    public void callBack(String[] imgSrcs) {
                    }
                }, new WebBiz.TextCallBack() {

                    @Override
                    public void callBack(String text) {
                        mWebJsInterface.setHtmlText(text);
                    }
                });
        String htmlResult = String.format(htmlCode, uiModeCssUri, htmlBody);
        webView.loadDataWithBaseURL(null, htmlResult, "text/html", "utf-8", null);
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        webView.setFocusable(false);

        // 隐藏到滚动条
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollContainer(false);
        //注入支持的本地方法
        mWebJsInterface = new WebJsInterface(this);
        webView.addJavascriptInterface(mWebJsInterface, WebJsInterface.JS_NAME);
        mWebJsInterface.setWebJsCallBack(webView);
        // 夜间模式
        if (ThemeMode.isNightMode()) {
            webView.setBackgroundColor(UIUtils.getActivity().getResources().getColor(R.color.bc_202124_night));
        } else {
            webView.setBackgroundColor(UIUtils.getActivity().getResources().getColor(R.color.bc_ffffff));
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url)) {
                    Nav.with(ZBUserProtectActivity.this).to(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 因为加载的是html文本，所以onPageStart时机比较合适
            }

        });
    }

}
