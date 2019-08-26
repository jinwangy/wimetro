package com.wimetro.qrcode.jni;

/**
 * jwyuan on 2017-9-25 10:18.
 */

public class NativeLib {

    // 加载本地库
    static {
        System.loadLibrary("native-lib");
    }

    //获取本地方法库的单例
    private static class SingletonHolder {
        private static final NativeLib INSTANCE = new NativeLib();
    }
    private NativeLib (){}
    public static final NativeLib getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // 本地方法声明
    public native String stringFromJNI();

    public native short processApdu(byte[] p_apdu_in,int len_in,byte[] p_apdu_out,short [] len_out,byte[] trade_type,byte[] p_trade_data,short [] p_token_status);
    public native short setLocalParam(String pdir, String p_sys_cache,String pime,short verLowest,String pAccount,byte[] p_data_CRC);
    public native short createDefaultCard();

    public native short updateCardMF05(byte[] p_file_data, short len_data);
    public native short updateCardADF01key(byte type, byte index, byte ver, byte algo, byte err_cnt, byte[] p_key, byte len_key);
    public native short updateCardADF01wallet(int balance, short off_cnt, short on_cnt,byte forceUpdate);
    public native short updateCardADF0115(byte[] p_file_data, short len_data);
    public native short updateCardADF0117(byte id, byte[] p_file_data, short len_data);
    public native void updateCardEnd(short verCurrent);

    public native short getWalletInfo(short adf_sfi, int[] p_balance, short[] p_offline_cnt, short[] p_online_cnt);
    public native short getRecordMetro(short adf_sfi, byte[] p_metro, short len_metro);

    public native short getUploadInfo(byte[] p_logicID, int[] p_balance, short[] p_offline_cnt, short[] p_online_cnt, byte[] p_metro, short len_metro,byte[] p_data_CRC);

    public native short setCardStatus(byte newStatus);
    public native short cardExsit();
    public native short updateLocalToken(String p_NewToken, short lenToken);
    public native short checkToken(String p_time_now);
    public native void getTokenOddInfo(String p_time_now,short[] oddmins, short[] oddamount,short[] minamount, byte[] oddcnt);

    public native short getTokenForServerVerify(byte[] pToken,short[] len_token);
    public native void tmpTokenApply(byte[] pToken,short[] len_token);
    public native short getInternelErrorCode();
}
