package com.wimetro.qrcode.common.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.core.PreferencesManager;
import com.wimetro.qrcode.greendao.StationDao;
import com.wimetro.qrcode.greendao.UserDao;
import com.wimetro.qrcode.greendao.entity.Station;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.bean.Token;
import com.wimetro.qrcode.jni.NativeLib;

import java.util.List;

/**
 * jwyuan on 2017-10-25 10:17.
 */

public class DeviceUtil {

    public static void saveLocal(Context mContext,User mUser) {
        PreferencesManager.put(mContext,"app_user",mUser.getApp_user());
        PreferencesManager.put(mContext,"tele_no",mUser.getTele_no());
        PreferencesManager.put(mContext,"hce_id",mUser.getHce_id());
        PreferencesManager.put(mContext,"alipay_user_id",mUser.getAlipay_user_id());
        PreferencesManager.put(mContext,"user_id",mUser.getUser_id());
        WLog.e("DeviceUtil","saveLocal,user_id = " + mUser.getUser_id());
        PreferencesManager.put(mContext,"login_flag",mUser.getLogin_flag());
        PreferencesManager.put(mContext,"card_flag",mUser.getCard_flag());
        PreferencesManager.put(mContext,"voucher_id",mUser.getVoucher_id());

        PreferencesManager.put(mContext,"nopay_amount",mUser.nopay_amount);
        PreferencesManager.put(mContext,"nopay_num",mUser.nopay_num);
    }

    public static void clearLocal(Context mContext) {
        WLog.e("DeviceUtil","clearLocal");
        PreferencesManager.clear(mContext);
    }

    public static String get(Context mContext, String key, String defaultObject){
        return (String)PreferencesManager.get(mContext,key,defaultObject);
    }

    public static String getHceId(Context mContext) {
        String hceId = get(mContext,"hce_id","");
        return hceId;
    }

    public static String getHceIdFromDb(Context mContext) {
        UserDao mUseDao = DaoManager.getInstance().getSession().getUserDao();
        if(mUseDao == null) return "";

        List<User> users = mUseDao.loadAll();
        if(users == null || users.size() == 0) return "";

        String hceId = users.get(users.size() - 1).getHce_id();
        return hceId;
    }

    public static String getUserId(Context mContext) {
        String userId = get(mContext,"user_id","");
        return userId;
    }

    public static String getAppUser(Context mContext) {
        String app_user = get(mContext,"app_user","");
        return app_user;
    }

    public static String getTeleNo(Context mContext) {
        String tele_no = get(mContext,"tele_no","");
        return tele_no;
    }

    public static void setCardNo(Context mContext) {
        String cardStr = get(mContext,"card_no","");
        if(TextUtils.isEmpty(cardStr)) {
            byte[] p_logicID = new byte[16];
            byte[] p_metro = new byte[80];
            int[] p_balance = new int[2];
            short[] p_offline_cnt = new short[2];
            short[] p_online_cnt = new short[2];

            short getUploadInfo_result = NativeLib.getInstance().getUploadInfo(p_logicID,p_balance,p_offline_cnt,p_online_cnt,p_metro,(short)61,new byte[1]);
            Log.e("setCardNo","result = " + getUploadInfo_result);

            if(getUploadInfo_result != 0) {
                return;
            }

            cardStr = HEX.bytesToAscii(p_logicID).substring(0,16);
            PreferencesManager.put(mContext,"card_no",cardStr);
        }
    }

    public static String getCardNo(Context mContext) {
        String card_no = get(mContext,"card_no","");
        return card_no;
    }

    public static String getLoginFlag(Context mContext) {
        String login_flag = get(mContext,"login_flag","");
        return login_flag;
    }

    public static String getCardFlag(Context mContext) {
        String card_flag = get(mContext,"card_flag","0");
        return card_flag;
    }

    public static String getAlipayUserId(Context mContext) {
        String alipayUserId = get(mContext,"voucher_id","");
        return alipayUserId;
    }

    public static boolean isLogin(Context mContext) {
        String user_id = get(mContext,"user_id","");
        if(TextUtils.isEmpty(user_id)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getImei(Context mContext) {
        TelephonyManager telephonemanage = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonemanage.getDeviceId();
        return  imei;
    }

    public static String getCN(Context mContext) {
        String cn = get(mContext,"voucher_id","");
        return cn;
    }

    public static String getNoPayAmount(Context mContext) {
        String amount = get(mContext,"nopay_amount","");
        return amount;
    }

    public static int getNoPayNum(Context mContext) {
        String num = get(mContext,"nopay_num","");
        int n = -1;
        try {
            n = Integer.valueOf(num).intValue();
        }catch (Exception e) {
        }
        return n;
    }

    public static String getRideWay(Context mContext) {
        String ride_way = get(mContext,"ride_way","qrcode");
        return ride_way;
    }

    public static void setRideWay(Context mContext,String way) {
        PreferencesManager.put(mContext,"ride_way",way);
    }

    public static void saveTokenLocal(Context mContext,Token mToken) {
        PreferencesManager.put(mContext,"expires",mToken.getExpires());
        PreferencesManager.put(mContext,"balLimit",mToken.getBalLimit());
        PreferencesManager.put(mContext,"onlineTimes",mToken.getOnlineTimes());
        PreferencesManager.put(mContext,"offlineTimes",mToken.getOfflineTimes());
    }

    public static boolean queryIsTokenExpiresFromLocal(Context mContext) {
        boolean isExpires = (Boolean) PreferencesManager.get(mContext,"isExpires",false);
        return isExpires;
    }

    public static void setIsTokenExpiresFromLocal(Context mContext,boolean isTokenExpires) {
        PreferencesManager.put(mContext,"isExpires",isTokenExpires);
    }

    public static int queryTokenOfflineTimesFromLocal(Context mContext) {
        String offlineTimes = get(mContext,"offlineTimes","-1");
        int offline = Integer.parseInt(offlineTimes);
        return offline;
    }

    public static int queryTokenOnlineTimesFromLocal(Context mContext) {
        String onlineTimes = get(mContext,"onlineTimes","-1");
        int online = Integer.parseInt(onlineTimes);
        return online;
    }

    public static int queryTokenBalLimitTimesFromLocal(Context mContext) {
        String balLimit = get(mContext,"balLimit","-1");
        int bal = Integer.parseInt(balLimit);
        return bal;
    }

    public static int queryTokenExpiresTimeFromLocal(Context mContext) {
        String tokenExpiresTime = get(mContext,"expires","-1");
        int tokenExpires = Integer.parseInt(tokenExpiresTime);
        return tokenExpires;
    }

    public static int getOffDataCount() {
        int count = 0;
        StationDao mStationDao = DaoManager.getInstance().getSession().getStationDao();
        List<Station> mStationList = mStationDao.queryBuilder().where(StationDao.Properties.Is_report.eq("0")).build().list();
        if(mStationList != null) {
            count = mStationList.size();
        }
        return count;
    }
}
