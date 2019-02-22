package com.xyyh.android.webkit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import static com.xyyh.android.webkit.Codes.*;
import static android.Manifest.permission.*;
import static android.content.pm.PackageManager.*;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    INTERNET,
                    CAMERA,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                    VIBRATE,
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_WIFI_STATE,
                    ACCESS_NETWORK_STATE,
                    CHANGE_WIFI_STATE,
                    READ_PHONE_STATE

            }, PERMISSION_REQUEST);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    this.finish();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
