package com.xyyh.android.webkit;

import com.baidu.location.BDLocation;

@FunctionalInterface
public interface LocationTaskExecutor {
    public void accept(BDLocation location);
}
