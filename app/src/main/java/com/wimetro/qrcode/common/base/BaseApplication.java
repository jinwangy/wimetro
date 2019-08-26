package com.wimetro.qrcode.common.base;

import android.app.Application;

import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.utils.CrashHandler;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;

import cn.com.infosec.mobile.android.IMSSdk;

/**
 * jwyuan on 2017/8/30 10:09.
 */

public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        DaoManager.getInstance().init(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext());
        AppConfig.init();

        //初始化IMSSDK
        Boolean status = IMSSdk.initialization(getApplicationContext(), AppConfig.HOST,
                "https".equals(AppConfig.PROTOCOL) ? AppConfig.HTTPS_CERT : null);
        WLog.e(TAG,"sdk初始化状态:" + status + ",IP = " + AppConfig.HOST);
    }
}
