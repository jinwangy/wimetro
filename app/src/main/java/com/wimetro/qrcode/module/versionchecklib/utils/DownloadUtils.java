package com.wimetro.qrcode.module.versionchecklib.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.wimetro.qrcode.common.utils.WLog;

import java.io.File;

/**
 * jwyuan on 2017-12-10 16:44.
 */

public class DownloadUtils {
    private static String TAG = DownloadUtils.class.getSimpleName();
    //下载器
    private DownloadManager downloadManager;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    //上下文
    private Context mContext;
    private String path;
    private String name;
    private Handler handler;
    //下载的ID
    private long downloadId;
    private int progress;

    private DownloadChangeObserver downloadObserver;
    private DownloadCall downloadCall;

    public static interface DownloadCall {
        public void onSuccess(File file);
        public void onDownloading(int progress);
        public void onDownloadFailed();
    }

    public void setDownloadCall(DownloadCall downloadCall) {
        this.downloadCall = downloadCall;
    }

    public  DownloadUtils(Context context,String path, String name){
        this.mContext = context;
        this.path = path;
        this.name = name;
        handler = new Handler(Looper.getMainLooper());
    }

    //下载apk
    public void downloadAPK(String url) {
        // 储存下载文件的目录
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("新版本Apk下载");
        request.setDescription("Apk Downloading");
        request.setVisibleInDownloadsUi(true);

        //设置下载的路径
        //request.setDestinationInExternalPublicDir(this.path, name);
       request.setDestinationUri(Uri.fromFile(new File(this.path + name)));

        //获取DownloadManager
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        downloadId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //采用内容观察者模式实现进度
        downloadObserver = new DownloadChangeObserver(null);
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction() ;
            if( action.equals( DownloadManager.ACTION_DOWNLOAD_COMPLETE  )){
                checkStatus();
            }
        }
    };


    //检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    WLog.e(TAG,"pause");
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    WLog.e(TAG,"pending");
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    WLog.e(TAG,"running");
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    WLog.e(TAG,"下载成功");
                    final File file = new File(path, name);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DownloadUtils.this.downloadCall.onSuccess(file);
                        }
                    });
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    WLog.e(TAG,"failed");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DownloadUtils.this.downloadCall.onDownloadFailed();
                        }
                    });
                    break;
            }
        }
        c.close();
    }

    class DownloadChangeObserver extends ContentObserver {

        private DownloadManager dManager;
        private DownloadManager.Query query;

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            dManager = (DownloadManager) (mContext.getSystemService(Context.DOWNLOAD_SERVICE));

            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                progress = Math.round(percent * 100);

                WLog.e(TAG,"progress = " + progress);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadUtils.this.downloadCall.onDownloading(progress);
                    }
                });
                cursor.close();
            }
        }
    }
}
