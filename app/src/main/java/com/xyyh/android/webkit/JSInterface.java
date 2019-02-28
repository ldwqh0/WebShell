package com.xyyh.android.webkit;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.google.zxing.client.android.CaptureActivity;
import com.xyyh.tjschool.MainActivity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.xyyh.android.webkit.Codes.*;

public class JSInterface {
    private WebViewActivity activity;
    private LocationClient locationClient;
    private final Queue<BDLocationTask> taskQueue = new ConcurrentLinkedQueue<>();

    private volatile boolean running = false;

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void setRunning(boolean status) {
        this.running = status;
    }

    public JSInterface(MainActivity context) {
        this.activity = context;
        this.locationClient = new LocationClient(activity.getApplicationContext());
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
    public void getLocation(String coorType) {
        addGeoTask(coorType);
    }


    /**
     * 添加一个定位任务到任务队列
     */
    private synchronized void addGeoTask(String coorType) {
        BDLocationTask task = new BDLocationTask(this.locationClient, this.activity.getWebView(), coorType, this::postLocation);
        taskQueue.add(task);
        if (!this.isRunning()) {
            startQueue();
        }
    }

    private void postLocation(final BDLocation location) {
        final WebView webView = this.activity.getWebView();
        webView.post(new Runnable() {
            @Override
            public void run() {
                String script = "coords: {" +
                        "latitude: " + location.getLatitude() + "," +
                        "longitude: " + location.getLongitude() +
                        "}," +
                        "coorType: '" + location.getCoorType() + "'," +
                        "address: {" +
                        "addrStr: '" + location.getAddrStr() + "'," +
                        "country: '" + location.getCountry() + "'," +
                        "province: '" + location.getProvince() + "'," +
                        "city: '" + location.getCity() + "'," +
                        "district: '" + location.getDistrict() + "'," +
                        "street: '" + location.getStreet() + "'," +
                        "streetNumber: '" + location.getStreetNumber() + "'," +
                        "locationDescribe: '" + location.getLocationDescribe() + "'" +
                        "}";
                webView.loadUrl("javascript:__responseLocation({" + script + "})");
            }
        });
        startQueue();
    }

    private synchronized void startQueue() {
        BDLocationTask task = taskQueue.poll();
        if (task != null) {
            setRunning(true);
            this.activity.getWebView().post(task);
        } else {
            setRunning(false);
        }
    }
}