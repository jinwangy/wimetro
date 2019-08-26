package com.wimetro.qrcode.service;

import android.app.IntentService;
import android.content.Intent;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.http.bean.Token;
import com.wimetro.qrcode.jni.NativeLib;

import java.net.URLEncoder;

public class TokenService extends IntentService {

    private String TAG = TokenService.class.getSimpleName();

    private Api api;
    private ApiResponse<Token> result;

    private String msg = "";

    public TokenService() {
        super("tokenService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WLog.e(TAG, "onCreate");
        this.api = ApiFactory.getApi(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //WLog.e(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //WLog.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WLog.e(TAG, "onHandleIntent");
        if(intent != null && intent.getAction().equals("android.intent.action.TokenService")) {
            String hce_id = intent.getStringExtra("hce_id");
            String voucher_id = intent.getStringExtra("voucher_id");
            String device_imei = intent.getStringExtra("device_imei");

            try{
                result = this.api.gainToken(this,hce_id, voucher_id,device_imei, Utils.getCurrent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            msg = "";

            if (ApiRequest.handleResponse(this, result,false)) {
                Token token = result.getObject();
                if(token != null) {
                    WLog.e(TAG, "token = " + token);
                    String token_id = token.getToken_id().replace("\r\n","").replace("\n","");
                    if(!TextUtils.isEmpty(token_id)) {
                        Short len_key = (short)(token_id.length());
                        WLog.e(TAG,"token_id = " + token_id + ",len_key = " + len_key);

                        short result = NativeLib.getInstance().updateLocalToken(token_id,len_key);
                        WLog.e(TAG, "updateLocalToken,result = " + result);
                    }
                } else {
                    WLog.e(TAG, "token is null");
                }
            } else {
                WLog.e(TAG, "get token error");
                try{
                    Integer mCode = result.getCode();
                    if(mCode != null && (mCode.intValue() == 12 || mCode.intValue() == 13)) {
                        msg = result.getMsg();
                    }
                }catch (Exception e) {
                }
            }
            sendTokenIntent();
        }
    }

    private void sendTokenIntent() {
        WLog.e(TAG,"sendTokenIntent");
        Intent intent = new  Intent();
        intent.setAction("com.token.intent");
        intent.putExtra("message",msg);
        this.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WLog.e(TAG, "onDestroy");
        WLog.e(TAG,"************************************");
    }
}
