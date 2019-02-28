package com.xyyh.tjschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(LOAD_DEFAULT);
        webView.addJavascriptInterface(new JSInterface(this), "Android");
        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
            }
            return false;
        });
        webView.loadUrl("http://192.168.1.21:1024/cap/");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        webView.reload();
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
}
