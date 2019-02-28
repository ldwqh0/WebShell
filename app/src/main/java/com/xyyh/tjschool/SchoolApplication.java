package com.xyyh.tjschool;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by LiDong on 2017/11/6.
 */

public class SchoolApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 忽略直接呼起相机闪退的错误
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
}
