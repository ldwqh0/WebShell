package com.xyyh.tjschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xyyh.android.webkit.EhWebChromeClient;
import com.xyyh.android.webkit.JSInterface;
import com.xyyh.android.webkit.WebViewActivity;

import static com.xyyh.android.webkit.Codes.*;
import static android.webkit.WebSettings.*;

public class MainActivity extends WebViewActivity {

    private WebView webView;
    private EhWebChromeClient webChromeClient;


    public MainActivity() {
        webChromeClient = new EhWebChromeClient(this);
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);

        setContentView(R.layout.activity_main);
        this.webView = findViewById(R.id.webView);
        this.webView.setWebChromeClient(webChromeClient);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebContentsDebuggingEnabled(true);
        // 启用js引擎
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置标准的缓存模式
        webView.getSettings().setCacheMode(LOAD_DEFAULT);
        // 增加JS接口程序
        webView.addJavascriptInterface(new JSInterface(this), "Android");
        // 增加按键处理程序，当用户按返回键时，可以返回到上一页，而不是退出当前activity
        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
            return false;
        });
        webView.loadUrl("http://101.200.59.182/cap/");
    }

    // 页面关闭时，清除会话信息
    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCookies();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                webChromeClient.callbackValue(data);
            } else {
                webChromeClient.callbackValue(null);
            }
        }

        if (requestCode == IMAGE_CAPTURE) {
            webChromeClient.callBackCamera();
        }

        if (requestCode == VIDEO_CAPTURE) {
            webChromeClient.callBackVideo();
        }
    }

    private void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        CookieSyncManager.getInstance().sync();
        CookieSyncManager.getInstance().startSync();
    }

}
