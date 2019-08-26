package com.wimetro.qrcode.common.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void ForcefinishAll() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        
     // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    
    public static void NormalFinishAll(Context mContext) {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
