package com.wimetro.qrcode.module.versionchecklib.core;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.module.versionchecklib.callback.DownloadListener;
import com.wimetro.qrcode.module.versionchecklib.core.http.AllenHttp;
import com.wimetro.qrcode.module.versionchecklib.core.http.FileCallBack;
import com.wimetro.qrcode.module.versionchecklib.utils.ALog;
import com.wimetro.qrcode.module.versionchecklib.utils.AppUtils;
import com.wimetro.qrcode.module.versionchecklib.utils.DownloadUtils;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class DownloadManager {
    private static int lastProgress = 0;

    public static void downloadAPK(final Context context, final String url, final VersionParams versionParams, final DownloadListener listener) {
        if (url == null || url.isEmpty()) {
            return;
        }
        if (versionParams.isSilentDownload()) {
            silentDownloadAPK(context, url, versionParams, listener);
            return;
        }
        lastProgress = 0;
//        ApkBroadCastReceiver.downloadApkPath = versionParams.getDownloadAPKPath();
        if (!versionParams.isForceRedownload()) {
            //判断本地文件是否存在
            String downloadPath = versionParams.getDownloadAPKPath() + context.getString(R.string.versionchecklib_download_apkname, context.getPackageName());
            if (checkAPKIsExists(context, downloadPath)) {
                if (listener != null)
                    listener.onCheckerDownloadSuccess(new File(downloadPath));
                AppUtils.installApk(context, new File(downloadPath));
                if (context instanceof Activity)
                    ((Activity) context).finish();
                return;
            }
        }
        if (listener != null)
            listener.onCheckerStartDownload();
        NotificationCompat.Builder builder = null;
        NotificationManager manager = null;
        if (versionParams.isShowNotification()) {
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(context);
            Intent intent = new Intent(context, versionParams.getCustomDownloadActivityClass());
            intent.putExtra("isRetry", false);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setTicker(context.getString(R.string.versionchecklib_downloading));
            builder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), 0));
            Notification notification = builder.build();
            notification.vibrate = new long[]{500, 500};
            notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
            manager.notify(0, notification);
        }

        final NotificationCompat.Builder finalBuilder = builder;
        final NotificationManager finalManager = manager;
        ALog.e("download:request,url = " + url);
        if(Utils.isWifiActive(context)) {
            Request request = new Request.Builder().url(url).build();
            AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {
                @Override
                public void onSuccess(File file, Call call, Response response) {
                    listener.onCheckerDownloadSuccess(file);
                    if (versionParams.isShowNotification()) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
                            ALog.e(context.getPackageName() + "");
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            uri = Uri.fromFile(file);
                        }
                        ALog.e("APK download Success");
                        //设置intent的类型
                        i.setDataAndType(uri,
                                "application/vnd.android.package-archive");
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
                        finalBuilder.setContentIntent(pendingIntent);
                        finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_finish));
                        finalBuilder.setProgress(100, 100, false);
                        finalManager.notify(0, finalBuilder.build());
                    }

                    AppUtils.installApk(context, file);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }

                @Override
                public void onDownloading(int progress) {
                    //ALog.e("downloadProgress:" + progress + "");
                    int currentProgress = progress;
//                showLoadingDialog(currentProgress);
                    listener.onCheckerDownloading(currentProgress);
                    if (currentProgress - lastProgress >= 5) {
                        lastProgress = currentProgress;
                        ALog.e("downloadProgress:" + lastProgress + "");
                        if (versionParams.isShowNotification()) {
                            finalBuilder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), lastProgress));
                            finalBuilder.setProgress(100, lastProgress, false);
                            finalManager.notify(0, finalBuilder.build());
                        }
                    }
                }

                @Override
                public void onDownloadFailed() {
                    if (versionParams.isShowNotification()) {
                        Intent intent = new Intent(context, versionParams.getCustomDownloadActivityClass());
                        intent.putExtra("isRetry", true);
//                intent.putExtra(AVersionService.VERSION_PARAMS_KEY, paramBundle);
                        intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
                        intent.putExtra("downloadUrl", url);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
                        finalBuilder.setContentIntent(pendingIntent);
                        finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_fail));
                        finalBuilder.setProgress(100, 0, false);
                        finalManager.notify(0, finalBuilder.build());
                    }
                    ALog.e("file download failed");
//                showFailDialog();
                    listener.onCheckerDownloadFail();

                }
            });
        } else {
            DownloadUtils downloadUtils =   new DownloadUtils(context,versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName()));
            downloadUtils.setDownloadCall(new DownloadUtils.DownloadCall() {
                @Override
                public void onSuccess(File file) {
                    listener.onCheckerDownloadSuccess(file);
                    AppUtils.installApk(context, file);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
                @Override
                public void onDownloading(int progress) {
                    //ALog.e("downloadProgress:" + progress + "");
                    int currentProgress = progress;
//                showLoadingDialog(currentProgress);
                    listener.onCheckerDownloading(currentProgress);
                    if (currentProgress - lastProgress >= 5) {
                        lastProgress = currentProgress;
                        ALog.e("downloadProgress:" + lastProgress + "");
                    }
                }
                @Override
                public void onDownloadFailed() {
                    ALog.e("file download failed");
//                showFailDialog();
                    listener.onCheckerDownloadFail();
                }
            });
            downloadUtils.downloadAPK(url);
        }
    }

    private static void silentDownloadAPK(final Context context, String url, final VersionParams versionParams, final DownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        if (listener != null)
            listener.onCheckerStartDownload();
        AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {


            @Override
            public void onSuccess(File file, Call call, Response response) {
                listener.onCheckerDownloadSuccess(file);

            }

            @Override
            public void onDownloading(int progress) {
                ALog.e("silent downloadProgress:" + progress + "");
                int currentProgress = progress;
//                showLoadingDialog(currentProgress);
                if (currentProgress - lastProgress >= 5) {
                    lastProgress = currentProgress;
                }
                listener.onCheckerDownloading(currentProgress);
            }

            @Override
            public void onDownloadFailed() {
                ALog.e("file silent download failed");
//                showFailDialog();
                listener.onCheckerDownloadFail();
            }
        });
    }

    public static boolean checkAPKIsExists(Context context, String downloadPath) {
        File file = new File(downloadPath);
        boolean result = false;
        if (file.exists()) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(downloadPath,
                        PackageManager.GET_ACTIVITIES);
                //判断安装包存在并且包名一样并且版本号不一样
                ALog.e("本地安装包版本号：" + info.versionCode + "\n 当前app版本号：" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                if (info != null && context.getPackageName().equalsIgnoreCase(info.packageName) && context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode != info.versionCode) {
                    result = true;
                }
            } catch (Exception e) {
                result = false;
            }
        }
        return result;

    }
}
