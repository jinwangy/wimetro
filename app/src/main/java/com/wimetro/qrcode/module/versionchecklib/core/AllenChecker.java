package com.wimetro.qrcode.module.versionchecklib.core;

import android.content.Context;
import android.content.Intent;

public class AllenChecker {
    private static boolean isDebug=true;
    public static void startVersionCheck(Context context, VersionParams versionParams) {
        Intent intent = new Intent(context, versionParams.getService());
        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
        context.startService(intent);
    }
    public static void init(boolean debug){
        isDebug=debug;
    }
    public static boolean isDebug(){
        return isDebug;
    }




}
