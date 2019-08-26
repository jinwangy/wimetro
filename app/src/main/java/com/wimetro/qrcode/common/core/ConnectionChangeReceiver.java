package com.wimetro.qrcode.common.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.mode.Presenter;
import com.wimetro.qrcode.mode.ReportOfflineStationDataMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

/**
 * jwyuan on 2017-11-18 16:11.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver implements IResult {
    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();
    private Presenter presenter_reportofflinestationdata;

    @Override
    public void onReceive(Context context, Intent intent) {
        WLog.e(TAG, "网络状态改变");
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //**判断当前的网络连接状态是否可用*/
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if ( info != null && info.isAvailable()){
                //当前网络状态可用
                int netType = info.getType();
                if (netType == ConnectivityManager.TYPE_WIFI){
                    WLog.e(TAG, "当前网络状态为-wifi");
                }else if (netType == ConnectivityManager.TYPE_MOBILE ){
                    WLog.e(TAG, "当前网络状态为-mobile");
                }

                if(presenter_reportofflinestationdata == null) {
                    presenter_reportofflinestationdata = new Presenter(this, "reportofflinestationdata");
                }
                presenter_reportofflinestationdata.excute(new ReportOfflineStationDataMode.ReportOffData(context));

                TokenManager.getInstance().setTokenContext(context);
                TokenManager.getInstance().startTokenService();
            }else {
                //当前网络不可用
                WLog.e(TAG, "无网络连接");
            }
        }
    }

    @Override
    public void onSuccess(String message, String type) {
        if(presenter_reportofflinestationdata != null) {
            //presenter_reportofflinestationdata.onStop();
        }
    }

    @Override
    public void onFailed(String error, String type) {

    }
}
