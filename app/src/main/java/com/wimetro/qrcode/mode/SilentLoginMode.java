package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.AsyncTask;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-12-19 16:14.
 */

public class SilentLoginMode implements IMode {

    private String TAG = SilentLoginMode.class.getSimpleName();
    private Context mContext;
    private IResult result;

    private ExecutorService mFull_task_excutor;
    private SilentLoginTask silentLoginTask;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        TaskUtils.cancelTaskInterrupt(silentLoginTask);
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(silentLoginTask);
    }

    public void excute(final IResult result, final IData data) {
        if (result == null) return;
        this.result = result;
        this.mContext = (Context) result;

        if(!Utils.isNetworkAvailable(this.mContext)) {
            this.result.onFailed("silentLogin,no network","silentLogin");
            return;
        }
        silentLoginTask = new SilentLoginTask(this.mContext);
        silentLoginTask.executeOnExecutor(mFull_task_excutor, DeviceUtil.getHceId(this.mContext),DeviceUtil.getAlipayUserId(this.mContext),
                DeviceUtil.getImei(this.mContext));
    }

    class SilentLoginTask extends AsyncTask<String, Integer,  ApiResponse<User> > {
        private Api api;
        private Context context;

        public SilentLoginTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                WLog.e(TAG,"silentLogin task");
                return api.silentLogIn(context,params[0], params[1], params[2]);
            } catch (IOException e) {
                SilentLoginMode.this.result.onFailed("","silentLogin");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            WLog.e(TAG,"SilentLogin,result = " + result);

            if (ApiRequest.handleResponse(context, result,false)) {
                User mUser = result.getObject();
                if(mUser != null) {
                    DeviceUtil.saveLocal(context,mUser);
                    SilentLoginMode.this.result.onSuccess("silent login success!","silentLogin");
                } else {
                    SilentLoginMode.this.result.onFailed("silent login failed,user = null","silentLogin");
                }
            } else {
                SilentLoginMode.this.result.onFailed("silent login failed,response failed","silentLogin");
            }
        }
    }
}
