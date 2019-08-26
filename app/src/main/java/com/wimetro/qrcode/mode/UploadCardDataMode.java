package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-10-31 10:28.
 */

public class UploadCardDataMode implements IMode {

    private static String TAG = UploadCardDataMode.class.getSimpleName();
    private Context mContext;

    private ExecutorService mFull_task_excutor;
    private UploadCardDataTask upCardDataTask;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        TaskUtils.cancelTaskInterrupt(upCardDataTask);
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(upCardDataTask);
    }

    public void excute(final IResult result, final IData data) {
        this.mContext = ((UploadCardData)data).getContext();

        byte[] p_logicID = new byte[16];
        byte[] p_metro = new byte[80];
        int[] p_balance = new int[2];
        short[] p_offline_cnt = new short[2];
        short[] p_online_cnt = new short[2];

        short getUploadInfo_result = NativeLib.getInstance().getUploadInfo(p_logicID,p_balance,p_offline_cnt,p_online_cnt,p_metro,(short)61,new byte[1]);
        WLog.e(TAG,"getUploadInfo_result = " + getUploadInfo_result  +
                ",p_balance = " + p_balance[0] + ",p_offline_cnt = " + p_offline_cnt[0] + ",p_online_cnt = " + p_online_cnt[0]);

        if(getUploadInfo_result != 0) {
            return;
        }

        String cardStr = HEX.bytesToAscii(p_logicID).substring(0,16);
        String metroStr = HEX.bytesToHex(p_metro).substring(0,122);

        WLog.e(TAG,"cardStr = " + cardStr + ",metroStr = " + metroStr);

        if(!Utils.isNetworkAvailable(this.mContext)) {
            //Toast.makeText(this.mContext, "网络连接不可用,无法更新卡信息!", Toast.LENGTH_SHORT).show();
            return;
        }

        upCardDataTask = new UploadCardDataTask(this.mContext);
        upCardDataTask.executeOnExecutor(mFull_task_excutor,DeviceUtil.getHceId(this.mContext),cardStr,metroStr,p_balance[0]+"",p_offline_cnt[0]+"",p_online_cnt[0]+"");
    }

    public static class UploadCardResult implements IResult {
        @Override
        public void onSuccess(String message, String type) {

        }
        @Override
        public void onFailed(String error, String type) {

        }
    }

    public static class UploadCardData implements IData {
        private Context mContext;
        public Context getContext() {
            return mContext;
        }

        public UploadCardData(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

    class UploadCardDataTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {
        private Api api;
        private Context context;

        public UploadCardDataTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<Void> doInBackground(String... params) {
            try {
                WLog.e(TAG,"uploadCardInfo task");
                return api.uploadCardInfo(context, params[0], params[1], params[2], params[3], params[4], params[5]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Void> result) {
            super.onPostExecute(result);
            WLog.e(TAG,"uploadCardInfo,result = " + result);

            if (ApiRequest.handleResponse(UploadCardDataMode.this.mContext, result,false)) {
                WLog.e(TAG,"uploadCardInfo is ok!");
            } else {
                WLog.e(TAG,"uploadCardInfo is not ok!");
            }
        }
    }
}
