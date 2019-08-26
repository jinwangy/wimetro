package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.AsyncTask;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.http.bean.Version;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;
import com.wimetro.qrcode.module.versionchecklib.core.AllenChecker;
import com.wimetro.qrcode.module.versionchecklib.core.VersionParams;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-11-2 17:26.
 */

public class CheckVersionMode implements IMode {
    private String TAG = CheckVersionMode.class.getSimpleName();
    private IResult result;
    private BaseActivity mBaseActivity;

    private ExecutorService mFull_task_excutor;
    private CheckUpdateTask checkUpdateTask;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        TaskUtils.cancelTaskInterrupt(checkUpdateTask);
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(checkUpdateTask);
    }

    public void excute(final IResult result, final IData data) {
        this.result = result;
        this.mBaseActivity = (BaseActivity)result;
        if(!Utils.isNetworkAvailable(this.mBaseActivity)) {
            WLog.e(TAG,"CheckUpdate,no network");
            this.result.onFailed("","check_update");
            return;
        }
        checkUpdateTask = new CheckUpdateTask(this.mBaseActivity);
        checkUpdateTask.executeOnExecutor(mFull_task_excutor);
    }

    class CheckUpdateTask extends AsyncTask<String, Integer,  ApiResponse<Version> > {
        private Api api;
        private Context context;

        public CheckUpdateTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<Version> doInBackground(String... params) {
            try {
                WLog.e(TAG,"checkUpdate task");
                return api.gainHCEVersion(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Version> result) {
            super.onPostExecute(result);
            WLog.e(TAG,"CheckUpdate,result = " + result);

            if (ApiRequest.handleResponse(context, result,false)) {
                Version version = result.getObject();
                if(version != null) {
                    WLog.e(TAG,"apk version url = " + AppConfig.API_URL_PHONE_HCE_VERSION_APK + version.getVersion_url());
                    if(isNeedUpdate(version))	{
                        VersionParams.Builder builder = new VersionParams.Builder();

                        builder.setShowDownloadingDialog(true)
                                .setShowNotification(false)
                                .setOnlyDownload(true)
                                .setDownloadUrl(AppConfig.API_URL_PHONE_HCE_VERSION_APK + version.getVersion_url())
                                .setTitle("检测到新版本" + version.getVersion_name() + ",请更新!")
                                .setUpdateMsg("");

                        AllenChecker.startVersionCheck(context, builder.build());
                        CheckVersionMode.this.result.onSuccess("","check_update");
                        ((BaseActivity)context).finish();
                    } else {
                        CheckVersionMode.this.result.onFailed("","check_update");
                        WLog.e(TAG,"no need update!");
                    }
                } else {
                    CheckVersionMode.this.result.onFailed("","check_update");
                    WLog.e(TAG,"version is null");
                }
            } else {
                CheckVersionMode.this.result.onFailed("","check_update");
                WLog.e(TAG,"get version error");
            }
        }

    }


    private boolean isNeedUpdate(Version version)	{
        if (version != null) {
            try{
                int localVersion = Utils.getVersionCode(this.mBaseActivity);
                int serverVersion = Integer.valueOf(version.getVersion_no()).intValue();

                WLog.e(TAG,"localVersion = " + localVersion + ",serverVersion = " + serverVersion);

                if(serverVersion > localVersion) {
                    return true;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
