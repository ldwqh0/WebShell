package com.xyyh.android.webkit;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.zxing.client.android.CaptureActivity;
import com.xyyh.tjschool.MainActivity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.xyyh.android.webkit.Codes.*;

public class JSInterface {
    private WebViewActivity activity;

    private LocationClient locationClient;

    private BDLocation location;

    private Queue<Object> taskQueue = new ConcurrentLinkedQueue<>();

    public JSInterface(MainActivity context) {
        this.activity = context;
        this.locationClient = new LocationClient(activity.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setOpenAutoNotifyMode();
        option.setIgnoreKillProcess(false);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                // 当接收到位置信息之后，结束定位
                JSInterface.this.locationClient.stop();
                JSInterface.this.location = bdLocation;
                while (taskQueue.poll() != null) {
                    JSInterface.this.postLocation();
                }
            }
        });
        locationClient.start();
    }


    /**
     * 退出壳子页面
     */
    @JavascriptInterface
    public void quit() {
        activity.finish();
    }

    /**
     * 扫描二维码
     *
     * @param i 扫描标号，
     */
    @JavascriptInterface
    public void scanPicture(int i) {
        activity.setScanIndex(i);
        activity.startActivityForResult(new Intent(activity, CaptureActivity.class), QR_REQUEST);
    }

    /**
     * 定位服务
     */
    @JavascriptInterface
    public void getLocation() {
        taskQueue.add(new Object());
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
    }

    private void postLocation() {
        WebView webView = activity.getWebView();
        webView.post(new Runnable() {
            @Override
            public void run() {
                String script = "coords: {" +
                        "latitude: " + location.getLatitude() + "," +
                        "longitude: " + location.getLongitude() +
                        "}," +
                        "address: {" +
                        "addrStr: '" + location.getAddrStr() + "'," +
                        "country: '" + location.getCountry() + "'," +
                        "province: '" + location.getProvince() + "'," +
                        "city: '" + location.getCity() + "'," +
                        "district: '" + location.getDistrict() + "'," +
                        "street: '" + location.getStreet() + "'," +
                        "streetNumber: '" + location.getStreetNumber() + "'," +
                        "coorType: '" + location.getCoorType() + "'," +
                        "locationDescribe: '" + location.getLocationDescribe() + "'" +
                        "}";
                webView.loadUrl("javascript:__responseLocation({" + script + "})");
            }
        });
    }
}