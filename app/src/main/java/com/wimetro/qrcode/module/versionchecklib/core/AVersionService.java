package com.wimetro.qrcode.module.versionchecklib.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.module.versionchecklib.callback.DownloadListener;
import com.wimetro.qrcode.module.versionchecklib.utils.ALog;

import java.io.File;


public abstract class AVersionService extends Service implements DownloadListener {
    protected VersionParams versionParams;
    public static final String VERSION_PARAMS_KEY = "VERSION_PARAMS_KEY";
    public static final String VERSION_PARAMS_EXTRA_KEY = "VERSION_PARAMS_EXTRA_KEY";
    public static final String PERMISSION_ACTION = "com.allenliu.versionchecklib.filepermisssion.action";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            versionParams = intent.getParcelableExtra(VERSION_PARAMS_KEY);
            verfiyAndDeleteAPK();
            if (versionParams.isOnlyDownload()) {
                showVersionDialog(versionParams.getDownloadUrl(), versionParams.getTitle(), versionParams.getUpdateMsg(), versionParams.getParamBundle());
            } else {
                //requestVersionUrlSync();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 验证安装包是否存在，并且在安装成功情况下删除安装包
     */
    private void verfiyAndDeleteAPK() {
        //判断versioncode与当前版本不一样的apk是否存在，存在删除安装包
        String downloadPath = versionParams.getDownloadAPKPath() + getApplicationContext().getString(R.string.versionchecklib_download_apkname, getApplicationContext().getPackageName());
        if (!DownloadManager.checkAPKIsExists(getApplicationContext(), downloadPath)) {
            try {
                ALog.e("删除本地apk");
                new File(downloadPath).delete();
            } catch (Exception e) {
            }
        }
    }

    public abstract void onResponses(AVersionService service, String response);

    String downloadUrl, title, updateMsg;
    Bundle paramBundle;

    public void showVersionDialog(String downloadUrl, String title, String updateMsg) {
        showVersionDialog(downloadUrl, title, updateMsg, null);
    }

    public void showVersionDialog(String downloadUrl, String title, String updateMsg, Bundle paramBundle) {
        this.downloadUrl = downloadUrl;
        this.title = title;
        this.updateMsg = updateMsg;
        this.paramBundle = paramBundle;
        if (versionParams.isSilentDownload()) {
//            BroadcastReceiver receiver = new VersionBroadCastReceiver();
//            IntentFilter intentFilter = new IntentFilter(PERMISSION_ACTION);
//            registerReceiver(receiver, intentFilter);
//            Intent intent = new Intent(this, PermissionDialogActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            silentDownload();
        } else {
            goToVersionDialog();
        }
    }

    private void silentDownload() {
        DownloadManager.downloadAPK(getApplicationContext(), downloadUrl, versionParams, this);
    }

    @Override
    public void onCheckerDownloading(int progress) {

    }

    @Override
    public void onCheckerStartDownload() {

    }

    @Override
    public void onCheckerDownloadSuccess(File file) {
        goToVersionDialog();
    }

    @Override
    public void onCheckerDownloadFail() {
        stopSelf();
    }

    private void goToVersionDialog() {
        Intent intent = new Intent(getApplicationContext(), versionParams.getCustomDownloadActivityClass());
        if (updateMsg != null)
            intent.putExtra("text", updateMsg);
        if (downloadUrl != null)
            intent.putExtra("downloadUrl", downloadUrl);
        if (title != null)
            intent.putExtra("title", title);
        if(paramBundle!=null)
            versionParams.setParamBundle(paramBundle);
        intent.putExtra(VERSION_PARAMS_KEY, versionParams);
//        if (paramBundle != null)
//            intent.putExtra(VERSION_PARAMS_EXTRA_KEY, paramBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    public void setVersionParams(VersionParams versionParams) {
        this.versionParams = versionParams;
    }

    public class VersionBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PERMISSION_ACTION)) {
                boolean result = intent.getBooleanExtra("result", false);
                if (result)
                    silentDownload();
                unregisterReceiver(this);
            }
        }
    }
}
