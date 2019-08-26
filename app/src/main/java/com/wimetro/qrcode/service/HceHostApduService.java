package com.wimetro.qrcode.service;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.wimetro.qrcode.common.core.TokenManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.FileUtil;
import com.wimetro.qrcode.common.utils.HEX;
//import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.mode.Presenter;
import com.wimetro.qrcode.mode.ReportStationDataMode;

import java.util.Arrays;

public class HceHostApduService extends HostApduService {
    private int messageCounter = 0;
    private String TAG = HceHostApduService.class.getSimpleName();

    private Presenter presenter;

    private boolean isLogin;
    private boolean isLoadCard;

    @Override
    public void onCreate() {
        WLog.e(TAG,"onCreate");
        presenter = new Presenter(new ReportStationDataMode.ReportStationResult(),"reportstationdata");

        getLoadCardStatus();

        short needToken =  NativeLib.getInstance().checkToken(Utils.getCurrent());
        WLog.e(TAG,"needToken = " + needToken);
        if(needToken != 0) {
            TokenManager.getInstance().setTokenContext(this);
            TokenManager.getInstance().startTokenService();
        }
    }

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        WLog.e(TAG,"***************************************************************");
        WLog.e(TAG,"processCommandApdu");

        if(apdu == null || apdu.length == 0) return null;

        isLogin = DeviceUtil.isLogin(this);
        if(!isLogin) {
            WLog.e(TAG,"isLogin = false");
            Toast.makeText(this,"未登录无法交易,请先登录",Toast.LENGTH_SHORT).show();
            return null;
        }

        short loadCard = NativeLib.getInstance().cardExsit();
        if(loadCard != 0) {
            WLog.e(TAG,"isLoadCard = false");
            Toast.makeText(this,"数据同步异常,请重新登录后再交易",Toast.LENGTH_SHORT).show();
            return null;
        }

//        if (DeviceUtil.queryIsTokenExpires() /**|| DeviceUtil.queryTokenOfflineTimes()**/) {
//            Utils.showTokenDialog(this);
//            return null;
//        }


        String adpu_str = HEX.bytesToHex(apdu);
        WLog.e(TAG, "processCommandApdu,APDU = " + adpu_str);
        short len_in = (short) apdu.length;
        WLog.e(TAG, "processCommandApdu,len_in = " + len_in);

        byte[] p_apdu_out = new byte[512];
        short [] len_out = new short[2];
        byte[] trade_type = new byte[2];
        byte[] p_trade_data = new byte[512];
        short[] p_token_status = new short[2];

        short result = NativeLib.getInstance().processApdu(apdu,len_in,p_apdu_out,len_out,trade_type,p_trade_data,p_token_status);

        WLog.e(TAG, "processCommandApdu,c++,return = " + result);
        WLog.e(TAG, "processCommandApdu,p_apdu_out = " + HEX.bytesToHex(p_apdu_out).substring(0,2*len_out[0]));
        WLog.e(TAG, "processCommandApdu,len_out = " + len_out[0]);
        WLog.e(TAG, "processCommandApdu,trade_type = " + HEX.bytesToHex(trade_type));
        //Log.e(TAG, "processCommandApdu,p_trade_data = " + HEX.bytesToAscii(p_trade_data));
        WLog.e(TAG, "processCommandApdu,p_token_status = " + p_token_status[0]);

        byte[] p_apdu_out_back  = Arrays.copyOf(p_apdu_out,len_out[0]);
        WLog.e(TAG, "processCommandApdu,java,back = " + HEX.bytesToHex(p_apdu_out_back));
        WLog.e(TAG,"***************************************************************");

        if(p_token_status[0] != 0) {
            int nopay_amount = DeviceUtil.getNoPayNum(this);
            int size = DeviceUtil.getOffDataCount();
            WLog.e(TAG,"nopay_num = " + nopay_amount + ",offData,size = " + size);

            if(Utils.isNetworkAvailable(this) && (size < nopay_amount)) {
                TokenManager.getInstance().setTokenContext(this);
                TokenManager.getInstance().startTokenService();
            } else {
                WLog.e(TAG, "sendTokenIntent");
                Intent intent = new Intent();
                intent.setAction("com.token.intent");
                this.sendBroadcast(intent);
            }
        }

        presenter.excute(new ReportStationDataMode.ReportStationData(trade_type,p_trade_data,this));

        return p_apdu_out_back;
    }

    private byte[] getMessage(String message) {
        byte[] data = message.getBytes();
        return addSW1SW2(data, (byte)(0x90), (byte)(0x00));
    }

    private byte[] getWelcomeMessage() {
        byte[] data = "Hello Desktop!".getBytes();
        return addSW1SW2(data, (byte)(0x90), (byte)(0x00));
    }

    private byte[] getNextMessage() {
        byte[] data = ("Message from android: " + messageCounter++).getBytes();
        return addSW1SW2(data, (byte)(0x90), (byte)(0x00));
    }

    private byte[] addSW1SW2(byte[] data, byte sw1, byte sw2) {
        byte[] ret = new byte[data.length + 2];
        System.arraycopy(data, 0, ret, 0, data.length);
        ret[data.length] = sw1;
        ret[data.length + 1] = sw2;

        return ret;
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {
        WLog.e(TAG, "onDeactivated");
        if(presenter != null) {
            //presenter.onStop();
        }
    }

    private void getLoadCardStatus() {
        String cardPath = FileUtil.getCardDirectoryPath(this);
        String cachePath = FileUtil.getCacheDirectoryPath(this);
        WLog.e(TAG,"CardPath = " + cardPath);
        WLog.e(TAG,"CachePath = " + cachePath);

        Short cardNoVersion = AppConfig.card_no_version;
        WLog.e(TAG,"cardNoVersion = " + cardNoVersion);

        if(!FileUtil.mkdir(cardPath,true)){
            isLoadCard = false;
            return;
        }

        if(!FileUtil.mkdir(cachePath,false)){
            isLoadCard = false;
            return;
        }

        String hceId = DeviceUtil.getHceId(this);
        if(TextUtils.isEmpty(hceId)) {
            isLoadCard = false;
            return;
        }

        short setLocalParam_result = NativeLib.getInstance().setLocalParam(cardPath,cachePath,DeviceUtil.getImei(this),cardNoVersion,hceId,new byte[1]);
        WLog.e(TAG,"setLocalParam_result = " + setLocalParam_result);
        if(setLocalParam_result == 0) {
            isLoadCard = true;
        } else {
            isLoadCard = false;
        }
    }
}
