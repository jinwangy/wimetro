package com.wimetro.qrcode.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.wimetro.qrcode.service.HceHostApduService;

/**
 * jwyuan on 2017-12-14 10:16.
 */

public class InfoUtil {
    public static String info = "";
    public static void init(Context context) {
        info = getMetrics(context) + "," +isSDCardAvailable() + "," +
                getVersionCode(context) + "," +isSupportHce(context) + "," + isDebug() + ","+
                getPhoneBrand() + "," + getPhoneModel() + "," + getProduct() + "," + getBuildVersion() + "," +
                getBuildLevel() + ","+getManufacturer() +","+getDevice()+","+getHardware();

        WLog.e("InfoUtil","info : " + info);
    }

    public static String getInfo() {
        return info;
    }

    /**
     * 获取设备宽度（px）
     * @param context
     * @return
     */
    public static int deviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     * @param context
     * @return
     */
    public static int deviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    public static String getMetrics(Context context) {
        try {
            WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
            Display display = wm.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int[] metrics = {width, height};
            return "metrics=(width = "+ width + ",height = " + height + ")";
        }catch (Exception e) {
            return "metrics=null";
        }
    }

    /**
     * SD卡判断
     * @return
     */
    public static String isSDCardAvailable() {
        try {
            return "sdcard=" + Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }catch (Exception e) {
            return "sdcard=null";
        }
    }

    /**
     * 返回版本号
     * 对应build.gradle中的versionCode
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "versionCode=" + versionCode;
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return "brand="+android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return "model=" + android.os.Build.MODEL;
    }

    /**
     * 获取手机Android API等级（22、23 ...）
     *
     * @return
     */
    public static String getBuildLevel() {
        return "sdk="+android.os.Build.VERSION.SDK_INT;
    }
    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return "release="+android.os.Build.VERSION.RELEASE;
    }

    //硬件制造商
    public static String getManufacturer() {
        return "manufacturer="+ Build.BOARD + "-" +Build.MANUFACTURER;
    }

    //硬件名称
    public static String getHardware() {
        return "hardware=" + Build.HARDWARE;
    }

    //设备名
    public static String getProduct() {
        return "product="+Build.PRODUCT;
    }

    public static String getDevice() {
        return "device="+Build.DEVICE;
    }

    public static String isDebug() {
        return "isDebug=" + WLog.isDebug;
    }

    public static String isSupportHce(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            boolean supportNfcHce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
            return "isSupportHce=" + supportNfcHce;
        }catch (Exception e) {
            return "isSupportHce=null";
        }
    }

    public static boolean ifSupportHce(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            boolean supportNfcHce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
            return supportNfcHce;
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean checkIsDefaultApp(Context mContext) {
        try{
            CardEmulation cardEmulationManager = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(mContext));
            ComponentName paymentServiceComponent = new ComponentName(mContext,  HceHostApduService.class.getCanonicalName());
            if (!cardEmulationManager.isDefaultServiceForCategory(paymentServiceComponent, CardEmulation.CATEGORY_PAYMENT)) {
                Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
                intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
                intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, paymentServiceComponent);
                ((Activity)mContext).startActivityForResult(intent, 0);
                WLog.e("InfoUtil","当前应用不是默认支付,请先手动设置");
                //Toast.makeText(mContext, "当前应用不是默认支付,请先手动设置", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                WLog.e("InfoUtil","当前应用是系统默认支付程序");
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNfcOpen(Context mContext) {
        boolean isOpen = false;
        NfcManager manager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter存在，能启用
            isOpen = true;
        } else {
            isOpen = false;
        }

        return isOpen;
    }
}
