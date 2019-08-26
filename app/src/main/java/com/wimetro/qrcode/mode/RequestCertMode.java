package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;
import cn.com.infosec.mobile.android.cert.InfosecCert;
import cn.com.infosec.mobile.android.result.Result;

/**
 * jwyuan on 2017-12-8 17:12.
 */

public class RequestCertMode implements IMode {
    private static String TAG = RequestCertMode.class.getSimpleName();
    private Context mContext;
    private IResult resultCallback;

    private InfosecCert infosecCert;
    private String CN;
    private String imei;

    private Message message;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WLog.e(TAG,"handleMessage,what = " + msg.what);
            switch(msg.what){
                case 0:
                    resultCallback.onSuccess("下载证书成功", "request_cert");
                    break;
                case 1:
                    resultCallback.onFailed("证书下载过程中网络不可用", "request_cert");
                    break;
                case 2:
                    resultCallback.onFailed("证书下载过程中JSON解析异常", "request_cert");
                    break;
                default:
                    resultCallback.onFailed("证书下载过程中出现未知异常", "request_cert");
                    break;
            }
        }
    };

    public void onCreate() {
        if (infosecCert == null) {
            infosecCert = new InfosecCert();
        }
    }

    public void excute(final IResult result, final IData data) {
        this.mContext = (Context) result;
        this.resultCallback = result;

        imei = DeviceUtil.getImei(this.mContext);
        CN = DeviceUtil.getAlipayUserId(this.mContext);
        WLog.e(TAG, "imei = " + imei + ",CN = " + CN);

        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(CN)) {
            resultCallback.onFailed("CN或IMEI值错误,证书无法下载!", "request_cert");
            return;
        }

        Boolean isCheckCertExist = infosecCert.checkCertExist(CN);
        WLog.e(TAG, "isCheckCertExist = " + isCheckCertExist);
        if (isCheckCertExist) {
            resultCallback.onSuccess("证书已存在!", "request_cert");
            return;
        } else {
            if (!Utils.isNetworkAvailable(this.mContext)) {
                resultCallback.onFailed("网络连接不可用,无法下载证书信息!", "request_cert");
                return;
            }

            mHandler.removeCallbacksAndMessages(null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestCert();
                }
            }).start();
        }
    }

    public void onStop() {
    }

    private void requestCert() {
        WLog.e(TAG,"requestCert task");
        //证书下载
        infosecCert.requestCert(CN, AppConfig.CERT_Algorithm, AppConfig.KEY_LENGTH, imei, AppConfig.VALIDITY, new Result.ResultListener() {
            @Override
            public void handleResult(Result result) {
                message = Message.obtain();
                switch (result.getResultID()) {
                    case Result.OPERATION_SUCCEED:
                        //证书下载成功
                        WLog.e(TAG, "证书下载成功:" + result.getResultDesc());
                        message.what = 0;
                        //resultCallback.onSuccess("下载证书成功", "request_cert");
                        break;
                    case Result.NETWORK_UNAVAILABLE:
                        WLog.e(TAG, "网络不可用:" + result.getResultDesc());
                        message.what = 1;
                        //resultCallback.onFailed("证书下载过程中网络不可用", "request_cert");
                        break;
                    case Result.JSON_EXCAPTION:
                        WLog.e(TAG, "JSON解析异常:" + result.getResultDesc());
                        message.what = 2;
                        //resultCallback.onFailed("证书下载过程中JSON解析异常", "request_cert");
                        break;
                    default:
                        WLog.e(TAG, "未知异常:ResultID = " + result.getResultID() + ",ResultDesc = " + result.getResultDesc());
                        message.what = 3;
                        //resultCallback.onFailed("证书下载过程中出现未知异常", "request_cert");
                        break;
                }
                WLog.e(TAG,"sendMessage");
                mHandler.sendMessage(message);
            }
        });
    }
}
