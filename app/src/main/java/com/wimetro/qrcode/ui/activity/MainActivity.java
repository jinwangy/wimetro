package com.wimetro.qrcode.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import rx.functions.Action1;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.core.PreferencesManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.FileUtil;
import com.wimetro.qrcode.common.utils.InfoUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.mode.Presenter;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements IResult {
    private static String TAG = MainActivity.class.getSimpleName();
    private NoLeakHandler hander;
    private String voucher_id;

    private Presenter presenter_checkupdate;
    private Presenter presenter_silentlogin;
    protected Dialog hceDialog;


    @Override
    public Object initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void excuteOnCreate() {
        super.excuteOnCreate();
        WLog.e(TAG,"onCreate");


        PackageManager pm = getPackageManager();
        boolean supportNfcHce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        WLog.e(TAG, "supportNfcHce = " + supportNfcHce);

        if(!FileUtil.hasSdcard()) {
            WLog.e(TAG,"has no card !!!");
        } else {
            WLog.e(TAG,"card is ok !!!");
        }

        voucher_id = DeviceUtil.getAlipayUserId(this);
        hander = new NoLeakHandler(this);

        presenter_checkupdate = new Presenter(this,"checkversion");
        presenter_silentlogin = new Presenter(this,"silent_login");

        checkPermissions();


    }

    private static class NoLeakHandler extends Handler{
        private WeakReference<MainActivity> mActivity;

        public NoLeakHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }
    }

    //android6.0以上系统需要动态申请危险及特殊权限
    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            if(Utils.isNetworkAvailable(MainActivity.this)) {
                                presenter_checkupdate.excute(null);
                            } else {
                                checkNfc();
                            }
                        } else {
                            // 未获取权限
                            Toast.makeText(MainActivity.this, "您没有授权该权限，应用退出", Toast.LENGTH_SHORT).show();
                            MainActivity.this.finish();
                        }
                    }
                });
    }

    @Override
    public void onSuccess(String message, String type) {
        if(type.equals("check_update")) {

        } else if(type.equals("silentLogin")) {
            WLog.e(TAG,message);
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        }
    }

    @Override
    public void onFailed(String error, String type) {
        if(type.equals("check_update")) {
            checkNfc();
        } else if(type.equals("silentLogin")) {
            WLog.e(TAG,error);
            DeviceUtil.clearLocal(MainActivity.this);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter_checkupdate.onStop();
        presenter_silentlogin.onStop();
    }

    @Override
    protected void excuteOnDestroy() {
        super.excuteOnDestroy();
        WLog.e(TAG,"onDestroy");
        hander.removeCallbacksAndMessages(null);
        if (hceDialog != null && hceDialog.isShowing())
            hceDialog.dismiss();
    }

    private void checkNfc() {

        if(!InfoUtil.ifSupportHce(this)) {
            showAlertDialog("提示","手机不支持卡模式,无法交易,APP将退出");
        } else {
//            if(!InfoUtil.isNfcOpen(this)) {
//                showAlertDialog("提示","请打开NFC功能再重新进入APP");
//                return;
//            }
            if(InfoUtil.checkIsDefaultApp(this)){
                goLoginPage();
            }
        }
    }

    private void goLoginPage() {
        hander.postDelayed(new Runnable(){
            public void run() {
                PreferencesManager.put(MainActivity.this,"login_flag","");
                WLog.e(TAG,"voucher_id = " + voucher_id);
                if (TextUtils.isEmpty(voucher_id)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                } else {
                    //startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                    presenter_silentlogin.excute(null);
                }
            }
        }, AppConfig.MAIN_ANIMATION_TIME);
    }

    private void showAlertDialog(String title,String updateMsg) {
        hceDialog = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog_Alert).setTitle(title).setMessage(updateMsg).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create();

        hceDialog.setCanceledOnTouchOutside(false);
        hceDialog.setCancelable(false);
        if(!hceDialog.isShowing()) {
            hceDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WLog.e(TAG,"requestCode = " + requestCode + ",resultCode=" + resultCode);
        if(requestCode == 0) {
            if(resultCode == 0) {
                //finish();//不同意将本应用设置为默认支付应用
                showAlertDialog("提示","你拒绝将本应用设置为默认支付应用,无法交易,APP将退出");
            } else{
                //本应用被设置为默认支付应用
                goLoginPage();
            }
        }
    }
}
