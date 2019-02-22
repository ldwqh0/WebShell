package com.xyyh.android.webkit;

import android.webkit.WebView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Queue;

/**
 * 一个定位任务
 */
public class BDLocationTask implements Runnable {
    //    private final Queue<BDLocationTask> taskQueue;
    private final LocationClient locationClient;
    private final WebView webView;
    private final String coorType;
    private final LocationTaskExecutor executor;


    public BDLocationTask(LocationClient client, WebView webView, String coorType, LocationTaskExecutor executor) {
        this.locationClient = client;
        this.webView = webView;
        this.coorType = coorType;
        this.executor = executor;
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                // 当接收到位置信息之后，结束定位
                BDLocationTask.this.locationClient.stop();
                executor.accept(bdLocation);
            }
        });
    }

    @Override
    public void run() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setOpenAutoNotifyMode();
        option.setIgnoreKillProcess(false);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedLocationDescribe(true);
        // 设置坐标类型
        option.setCoorType(coorType);
        locationClient.setLocOption(option);
        locationClient.start();
    }
}
