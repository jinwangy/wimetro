package com.wimetro.qrcode.common.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


import android.view.KeyEvent;
import android.view.View;

import android.view.WindowManager;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.http.bean.Version;
import com.wimetro.qrcode.ui.activity.MainActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by jfwang on 2017-10-26.
 */

public class CheckVersionHelper {


    private ExecutorService FULL_TASK_EXECUTOR;
    private CheckUpdateTask mCheckUpdateTask;
    private static final String FORCE_UPDATE = "1";
    private boolean isNeedUpate = false;
    private Context mContext;

    public interface Callback {

        /**
         * 当检查更新结束时
         *
         * @param newVersion
         *            新版本，没有新版本则为null
         * @return
         */
        boolean onCheckVersionFinish(Version newVersion);
    }


    public static CheckVersionHelper sInstance;

    public static CheckVersionHelper getInstance() {
        if (sInstance == null) {
            sInstance = new CheckVersionHelper();
        }
        return sInstance;
    }

    private CheckVersionHelper() {
    }


    public void checkVersion(final Context context, final Callback callback)	{
        mContext = context;
        TaskUtils.cancelTaskInterrupt(mCheckUpdateTask);
        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();
        mCheckUpdateTask = new CheckUpdateTask(context,callback);
        mCheckUpdateTask.executeOnExecutor(FULL_TASK_EXECUTOR);

    }

    class CheckUpdateTask extends AsyncTask<String, Integer,  ApiResponse<Version> > {

        private Api api;
        private Context context;
        private Callback callback;


        public CheckUpdateTask(Context context,final Callback callback) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected ApiResponse<Version> doInBackground(String... params) {

            try {
                return api.gainHCEVersion(context);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Version> result) {
            super.onPostExecute(result);



            if (result == null)
                return;
            Version object = result.getObject();
            callback.onCheckVersionFinish(object);
            if (ApiRequest.handleResponse(context, result)) {
                if(isNeedUpdate(object))	{
                    showUpdateDialog(object);
                    isNeedUpate = true;
                } else {
                    isNeedUpate = false;
                }

            } else {
                isNeedUpate = false;
                Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showUpdateDialog(final Version arg0)	{
        if (FORCE_UPDATE.equals(arg0.getUpdate_flag())) {
            Dialog alertDialog=new AlertDialog.Builder(mContext)
                    .setTitle("检查到新版本，避免使用异常，请更新!")
                    .setPositiveButton("确定",new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            String path = "https://www.pgyer.com/iafc_hce_android";

                            downFile(mContext,path);
                        }

                    }).create();


            alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK )
                    {
                        Activity mActivity = (Activity) mContext;
                        mActivity.finish();
                    }
                    return false;
                }




            });
            alertDialog.show();
        } else {
            Dialog alertDialog=new AlertDialog.Builder(mContext)
                    .setTitle("检查到新版本，是否要更新？")
                    .setPositiveButton("确定",new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            String path = "https://www.pgyer.com/iafc_hce_android";
                            downFile(mContext,path);
                        }

                    })
                    .setNegativeButton("取消",new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    }).create();

            alertDialog.show();
        }



    }

    private static void downFile(Context context, String path) {
        if (path != null) {
            Uri uri = Uri.parse(path);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));

        }
    }

    private boolean isNeedUpdate(Version arg0)	{
        if (arg0 != null) {

            float  localVersion = Utils.getVersionCode(mContext);

            float serverVersion = Float.parseFloat(arg0.getVersion_no());

            if(serverVersion > localVersion) {
                return true;
            }
        }
        return false;
    }


}
