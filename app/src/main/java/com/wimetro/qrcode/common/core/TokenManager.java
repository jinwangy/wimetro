package com.wimetro.qrcode.common.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.jni.NativeLib;

import static android.content.Context.ALARM_SERVICE;

/**
 * jwyuan on 2017-11-17 09:51.
 */

public class TokenManager {

    private String TAG = TokenManager.class.getSimpleName();

    private Context mContext;
    private int expiresInt = 10;//每多少分钟申请一次令牌

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            WLog.e(TAG,"runCheckToken...");
            if(isNeedApplyTokenByTime()) {
                startTokenService();
            }
            mHandler.postDelayed(this, expiresInt * 60 * 1000);
        }
    };

    private boolean isNeedApplyTokenByTime() {
        WLog.e(TAG,"isNeedAppplyTokenByTime");
        boolean isNeed = false;
        try{
            short checkTokenResult = NativeLib.getInstance().checkToken(Utils.getCurrent());
            WLog.e(TAG,"checkTokenResult = " + checkTokenResult);
            if(NativeLib.getInstance().checkToken(Utils.getCurrent()) != 0) {
                return true;
            }

            short[] oddmins = new short[1];
            short[] oddamount = new short[1];
            short[] minamount = new short[1];
            byte[] oddcnt = new byte[1];

            NativeLib.getInstance().getTokenOddInfo(Utils.getCurrent(),oddmins,oddamount,minamount,oddcnt);
            WLog.e(TAG,"oddmins = " + oddmins[0] + ",oddamount = "  + oddamount[0] + ",minamount = " + minamount[0] +",oddcnt = " + HEX.bytesToHex(oddcnt));

            if(oddmins[0] <=  (short)10) {
                isNeed = true;
            }
        }catch (Exception e) {
            e.printStackTrace();
            isNeed = false;
        }
        return isNeed;
    }

    private static class SingletonHolder {
        private static final TokenManager INSTANCE = new TokenManager();
    }
    private TokenManager (){}
    public static final TokenManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Context getTokenContext() {
        return mContext;
    }

    public void setTokenContext(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    public void startTokenService() {
        WLog.e(TAG, "startTokenService");
        Intent intent = makeIntent();
        if(intent == null) return;

        mContext.startService(intent);
    }

    public void stopTokenService() {
        WLog.e(TAG, "stopTokenService");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.TokenService");
        intent.setPackage(mContext.getPackageName());
        mContext.stopService(intent);
    }

    private Intent makeIntent() {
        Intent intent = null;

        String hce_id = DeviceUtil.getHceId(mContext);
        String voucher_id = DeviceUtil.getAlipayUserId(mContext);
        String device_imei = DeviceUtil.getImei(mContext);

        if(TextUtils.isEmpty(hce_id) || TextUtils.isEmpty(voucher_id) || TextUtils.isEmpty(device_imei)) {
            WLog.e(TAG,"startAlarmColock:parameter is not ok!");
            sendTokenIntent();
            return null;
        }

        if(!Utils.isNetworkAvailable(mContext)) {
            WLog.e(TAG,"network is not ok!");
            sendTokenIntent();
            return null;
        }

        intent = new Intent("android.intent.action.TokenService");
        intent.putExtra("hce_id",hce_id);
        intent.putExtra("voucher_id",voucher_id);
        intent.putExtra("device_imei",device_imei);

        intent.setPackage(mContext.getPackageName());

        return  intent;
    }

    private void sendTokenIntent() {
        WLog.e(TAG,"sendTokenIntent");
        Intent intent = new  Intent();
        intent.setAction("com.token.intent");
        mContext.sendBroadcast(intent);
    }

    public void startCheckToken(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        WLog.e(TAG,"startCheckToken");
        startTokenService();
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, expiresInt * 60 * 1000);//每多长时间执行一次runnable.
    }

    public void stopCheckToken(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        WLog.e(TAG,"stopCheckToken");
        mHandler.removeCallbacks(runnable);
    }

//    public void startAlarmColock() {
//        WLog.e(TAG, "startAlarmColock");
//        AlarmManager am = (AlarmManager)((this.mContext).getSystemService(ALARM_SERVICE));
//
//        Intent intent = makeIntent();
//        if(intent == null) return;
//
//        PendingIntent pi = PendingIntent.getService(this.mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),expiresInt * 60 * 1000,pi);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + expiresInt * 60 * 1000, pi);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + expiresInt * 60 * 1000, pi);
//        } else {
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), expiresInt * 60 * 1000, pi);
//        }
//    }
//
//    public void cancelAlarmColock() {
//        WLog.e(TAG, "cancelAlarmColock");
//        AlarmManager am = (AlarmManager)(mContext.getSystemService(ALARM_SERVICE));
//
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.TokenService");
//        intent.setPackage(mContext.getPackageName());
//
//        PendingIntent pi = PendingIntent.getService(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        am.cancel(pi);
//        pi.cancel();
//    }
}
