package com.wimetro.qrcode.testUnit;

import android.os.Bundle;
import android.util.Log;

import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.service.HceHostApduService;

import java.util.Arrays;

/**
 * jwyuan on 2017-10-31 18:03.
 */

public class TestAdpu {

    private String TAG = HceHostApduService.class.getSimpleName();

    //获取本地方法库的单例
    private static class SingletonHolder {
        private static final TestAdpu INSTANCE = new TestAdpu();
    }
    private TestAdpu (){}
    public static final TestAdpu getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void processCommandApdu() {
        byte[] apdu = HEX.hexToBytes("805401000F0000000A2017103117332901D6F585");
        processCommandApdu(apdu,null);
    }

    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        Log.e(TAG,"***************************************************************");
        Log.e(TAG,"processCommandApdu");


//        if (DeviceUtil.queryTokenExpires() /**|| DeviceUtil.queryTokenOfflineTimes()**/) {
//            Utils.showTokenDialog(this);
//            return null;
//        }

        if(apdu == null || apdu.length == 0) return null;

        String adpu_str = HEX.bytesToHex(apdu);
        Log.e(TAG, "processCommandApdu,APDU = " + adpu_str);
        short len_in = (short) apdu.length;
        Log.e(TAG, "processCommandApdu,len_in = " + len_in);

        byte[] p_apdu_out = new byte[512];
        short [] len_out = new short[2];
        byte[] trade_type = new byte[2];
        byte[] p_trade_data = new byte[512];
        short[] p_token_status = new short[2];

        short result = NativeLib.getInstance().processApdu(apdu,len_in,p_apdu_out,len_out,trade_type,p_trade_data,p_token_status);

        Log.e(TAG, "processCommandApdu,c++,return = " + result);
        Log.e(TAG, "processCommandApdu,p_apdu_out = " + HEX.bytesToHex(p_apdu_out).substring(0,2*len_out[0]));
        Log.e(TAG, "processCommandApdu,len_out = " + len_out[0]);
        Log.e(TAG, "processCommandApdu,trade_type = " + HEX.bytesToHex(trade_type));
        Log.e(TAG, "processCommandApdu,p_trade_data = " + HEX.bytesToHex(p_trade_data));

        byte[] p_apdu_out_back  = Arrays.copyOf(p_apdu_out,len_out[0]);
        Log.e(TAG, "processCommandApdu,java,back = " + HEX.bytesToHex(p_apdu_out_back));
        Log.e(TAG,"***************************************************************");

        return p_apdu_out_back;
    }

}
