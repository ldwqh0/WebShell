package com.xyyh.android.webkit;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import static com.xyyh.android.webkit.Codes.QR_REQUEST;

/**
 * Created by LiDong on 2017/11/16.
 */
public abstract class WebViewActivity extends AppCompatActivity {

    private int scanIndex;

    public abstract WebView getWebView();

    public final void setScanIndex(int i) {
        this.scanIndex = i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("result")) {
                    String result = data.getStringExtra("result");
                    final WebView webView = getWebView();
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:__responseQRCode(" + scanIndex + ",'" + result + "')");
                        }
                    });
                }
            }
        }
    }
}
