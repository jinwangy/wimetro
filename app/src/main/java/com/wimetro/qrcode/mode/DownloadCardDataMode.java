package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.core.PreferencesManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.FileUtil;
import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.greendao.entity.Card;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-10-30 15:35.
 */

public class DownloadCardDataMode implements IMode {

    private static String TAG = DownloadCardDataMode.class.getSimpleName();
    private Context mContext;
    private IResult resultCallback;

    private ExecutorService mFull_task_excutor;
    private DownloadCardDataTask downCardDataTask;

    private short card_ver;
    private String card_flag;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        TaskUtils.cancelTaskInterrupt(downCardDataTask);
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(downCardDataTask);
    }

    public void excute(final IResult result, final IData data) {
        this.mContext = (Context)result;
        this.resultCallback= result;

        card_ver = AppConfig.card_no_version;

        String cardPath = getCardDirectoryPath();
        String cachePath = getCacheDirectoryPath();
        WLog.e(TAG,"CardPath = " + cardPath);
        WLog.e(TAG,"CachePath = " + cachePath);

        if(!mkdir(getCardDirectoryPath(),true)){
            //Toast.makeText(this.mContext, "创建卡目录失败!", Toast.LENGTH_SHORT).show();
            resultCallback.onFailed("下载卡失败,错误码ER002!","download_card");//创建卡目录失败!
            return;
        }

        if(!mkdir(getCacheDirectoryPath(),false)){
            //Toast.makeText(this.mContext, "创建卡目录失败!", Toast.LENGTH_SHORT).show();
            resultCallback.onFailed("下载卡失败,错误码ER003!","download_card");//缓存目录读写失败!
            return;
        }

        short setLocalParam_result = NativeLib.getInstance().setLocalParam(cardPath,cachePath,DeviceUtil.getImei(this.mContext),card_ver,DeviceUtil.getHceId(this.mContext),new byte[1]);
        String login_flag = DeviceUtil.getLoginFlag(this.mContext);
        card_flag = DeviceUtil.getCardFlag(this.mContext);

        WLog.e(TAG,"setLocalParam_result = " + setLocalParam_result + ",login_flag = " + login_flag + ",card_ver = " + card_ver + ",card_flag = " + card_flag);

        if(setLocalParam_result != 0 || (setLocalParam_result == 0 && login_flag.equals("1")) || card_flag.equals("1")) {
            short createDefaultCard_result = NativeLib.getInstance().createDefaultCard();
            WLog.e(TAG, "createDefaultCard_result = " + createDefaultCard_result);
            if(createDefaultCard_result == 0) {
                //DeviceUtil.setCardNo(this.mContext);
                if(!Utils.isNetworkAvailable(this.mContext)) {
                    //Toast.makeText(this.mContext, "网络连接不可用,无法下载卡信息!", Toast.LENGTH_SHORT).show();
                    resultCallback.onFailed("网络连接不可用,无法下载卡信息!","download_card");
                    return;
                }

                downCardDataTask = new DownloadCardDataTask(this.mContext);
                downCardDataTask.executeOnExecutor(mFull_task_excutor,DeviceUtil.getHceId(this.mContext),"0");
            } else {
                //Toast.makeText(this.mContext, "创建默认卡出现异常!", Toast.LENGTH_SHORT).show();
                resultCallback.onFailed("下载卡失败,错误码ER005!","download_card");//创建默认卡出现异常!
            }
       } else {
            DeviceUtil.setCardNo(this.mContext);
            resultCallback.onSuccess("下载卡信息成功!","download_card");
        }
    }

    public String getCardDirectoryPath() {
        return  FileUtil.getStoragePath(this.mContext) + "Card" + File.separator + DeviceUtil.getHceId(this.mContext);
    }

    public String getCacheDirectoryPath() {
        return  FileUtil.getCacheFile(this.mContext);
    }

    private static boolean mkdir(String dir,boolean isCheck) {
        WLog.e(TAG,"want to mkdir = " + dir);
        boolean success = false;
        File dirPath = null;
        try {
            dirPath = new File(dir);
            //FileUtil.deleteFile(dirPath);
            if (!dirPath.exists() && isCheck) {
                WLog.e(TAG,"create dir");
                dirPath.mkdirs();
            }
            success = true;
        } catch (Exception e) {
            WLog.e(TAG,"mkdir got error: "+e.getMessage());
            e.printStackTrace();
            success = false;
        }

        if(dirPath == null || !dirPath.exists() || !dirPath.canRead() || !dirPath.canWrite()) {
            WLog.e(TAG,"exist = " + dirPath.exists() + ",canRead = " + dirPath.canRead() + ",canWrite = " + dirPath.canWrite());
            success = false;
        }

        WLog.e(TAG,"mkdir,result = " + success);
        return success;
    }

    private boolean isALLzero(String str) {
        boolean isAll = false;
        if(TextUtils.isEmpty(str.trim())) {
            return false;
        }
        int size = str.trim().length();
        int count = 0;
        for(int i = 0 ;i < size; i++) {
            if(str.charAt(i) == '0') {
                count++;
            }
        }
        if(count == size) {
            return true;
        }
        WLog.e(TAG,"count = " + count + ",size = " + size);
        return isAll;
    }

    class DownloadCardDataTask extends AsyncTask<String, Integer,  ApiResponse<Card> > {
        private Api api;
        private Context context;

        public DownloadCardDataTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //DownloadCardDataMode.this.mBaseActivity.startLoading();
        }

        @Override
        protected ApiResponse<Card> doInBackground(String... params) {
            try {
                WLog.e(TAG,"downloadCardInfo task");
                return api.downloadCardInfo(context, params[0], params[1]);
            } catch (IOException e) {
                resultCallback.onFailed("下载卡失败,错误码ER007!","download_card");//创建卡信息过程中出现未知异常!
                e.printStackTrace();
            }
            return null;
        }

        private byte getValue(String str, int beginIndex, int endIndex) {
            if(TextUtils.isEmpty(str) || beginIndex < 0 || endIndex < 0 || (endIndex - beginIndex) > str.length()) return 0x00;
            return (byte)(Integer.valueOf(str.substring(beginIndex,endIndex)).intValue());
        }

        @Override
        protected void onPostExecute(ApiResponse<Card> result) {
            super.onPostExecute(result);
            //DownloadCardDataMode.this.mBaseActivity.stopLoading();
            WLog.e(TAG,"....................");
            WLog.e(TAG,"downloadCardInfo,result = " + result);
            WLog.e(TAG,"....................");
            try{
                if (ApiRequest.handleResponse(DownloadCardDataMode.this.mContext, result,false)) {
                    Card cardData = (Card)result.getObject();
                    WLog.e(TAG,"cardData = " + cardData);
                    if(cardData != null) {
                        String card_no = cardData.getCardNo();
                        if(TextUtils.isEmpty(card_no)) {
                            WLog.e(TAG,"card_no is null");
                            resultCallback.onFailed("下载卡失败,错误码ER008!","download_card");//逻辑卡号不能为空,下载卡信息失败
                            return;
                        }
                        if(isALLzero(card_no)) {
                            WLog.e(TAG,"card_no is invalite");
                            resultCallback.onFailed("下载卡失败,错误码ER009!","download_card");//逻辑卡号分配错误,下载卡信息失败
                            return;
                        }
                        String mf_005 = cardData.getMF_0005();
                        if(TextUtils.isEmpty(mf_005)) {
                            WLog.e(TAG,"mf_005 is null");
                            resultCallback.onFailed("下载卡失败,错误码ER010!","download_card");//mf_005 is null,下载卡信息失败
                            return;
                        } else {
                            WLog.e(TAG,"mf_005 substring = " + mf_005.substring(16,32));
                            if(!mf_005.substring(16,32).equals(card_no)) {
                                WLog.e(TAG,"mf_005 not contain card_no");
                                resultCallback.onFailed("下载卡失败,错误码ER011!","download_card");//mf_005未包含逻辑卡号,下载卡信息失败
                                return;
                            }
                            short mf_005_result = NativeLib.getInstance().updateCardMF05(HEX.hexToBytes(mf_005),(short)30);
                            WLog.e(TAG,"mf_005_result = " + mf_005_result);
                            if(mf_005_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER012!","download_card");//mf_005_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String adf1_0015 = cardData.getADF1_0015();
                        if(TextUtils.isEmpty(adf1_0015)) {
                            WLog.e(TAG,"adf1_0015 is null");
                            resultCallback.onFailed("下载卡失败,错误码ER013!","download_card");//adf1_0015 is null,下载卡信息失败
                            return;
                        } else {
                            short adf1_0015_result = NativeLib.getInstance().updateCardADF0115(HEX.hexToBytes(adf1_0015),(short)48);
                            WLog.e(TAG,"adf1_0015_result = " + adf1_0015_result);
                            if(adf1_0015_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER014!","download_card");//adf1_0015_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String adf1_0017 = cardData.getADF1_0017_01();
                        if(TextUtils.isEmpty(adf1_0017)) {
                            WLog.e(TAG,"adf1_0017 is null");
                            resultCallback.onFailed("下载卡失败,错误码ER015!","download_card");//adf1_0017 is null,下载卡信息失败
                            return;
                        } else {
                            if(isALLzero(adf1_0017)) {
                                WLog.e(TAG,"adf1_0017 is invalite");
                                resultCallback.onFailed("下载卡失败,错误码ER016!","download_card");//adf1_0017分配错误,下载卡信息失败
                                return;
                            }
                            short adf1_0017_result = NativeLib.getInstance().updateCardADF0117((byte)01,HEX.hexToBytes(adf1_0017),(short)61);
                            WLog.e(TAG,"adf1_0017_result = " + adf1_0017_result);
                            if(adf1_0017_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER017!","download_card");//adf1_0017_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String damk_02 = cardData.getDamk_02();
                        String damk_02_attribute = cardData.getDamk_02_attribute();

                        if(TextUtils.isEmpty(damk_02) || TextUtils.isEmpty(damk_02_attribute)) {
                            WLog.e(TAG,"damk_02 is null");
                            resultCallback.onFailed("下载卡失败,错误码ER018!","download_card");//damk_02 is null,下载卡信息失败
                            return;
                        } else {
                            short damk_02_result = NativeLib.getInstance().updateCardADF01key(getValue(damk_02_attribute,0,2),
                                    getValue(damk_02_attribute,2,4),
                                    getValue(damk_02_attribute,4,6),
                                    getValue(damk_02_attribute,6,8),
                                    getValue(damk_02_attribute,8,10),
                                    HEX.hexToBytes(damk_02),(byte)16);
                            WLog.e(TAG,"damk_02_result = " + damk_02_result);
                            if(damk_02_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER019!","download_card");//damk_02_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String dpk_01 = cardData.getDpk_01();
                        String dpk_01_attribute = cardData.getDpk_01_attribute();

                        if(TextUtils.isEmpty(dpk_01) || TextUtils.isEmpty(dpk_01_attribute)) {
                            WLog.e(TAG,"dpk_01 is null");
                            resultCallback.onFailed("下载卡失败,错误码ER020!","download_card");//dpk_01 is null,下载卡信息失败
                            return;
                        } else {
                            short dpk_01_result = NativeLib.getInstance().updateCardADF01key(getValue(dpk_01_attribute,0,2),
                                    getValue(dpk_01_attribute,2,4),
                                    getValue(dpk_01_attribute,4,6),
                                    getValue(dpk_01_attribute,6,8),
                                    getValue(dpk_01_attribute,8,10),
                                    HEX.hexToBytes(dpk_01),(byte)16);
                            WLog.e(TAG,"dpk_01_result = " + dpk_01_result);
                            if(dpk_01_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER021!","download_card");//dpk_01_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String dtk = cardData.getDtk();
                        String dtk_attribute = cardData.getDtk_attribute();

                        if(TextUtils.isEmpty(dtk) || TextUtils.isEmpty(dtk_attribute)) {
                            WLog.e(TAG,"dtk is null");
                            resultCallback.onFailed("下载卡失败,错误码ER022!","download_card");//dtk is null,下载卡信息失败
                            return;
                        } else {
                            short dtk_result = NativeLib.getInstance().updateCardADF01key(getValue(dtk_attribute,0,2),
                                    getValue(dtk_attribute,2,4),
                                    getValue(dtk_attribute,4,6),
                                    getValue(dtk_attribute,6,8),
                                    getValue(dtk_attribute,8,10),
                                    HEX.hexToBytes(dtk),(byte)16);
                            WLog.e(TAG,"dtk_result = " + dtk_result);
                            if(dtk_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER023!","download_card");//dtk_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        String fee_total = cardData.getFee_total();
                        String onlineval = cardData.getOnlineval();
                        String offlineval = cardData.getOfflineval();

                        if(TextUtils.isEmpty(fee_total) || TextUtils.isEmpty(onlineval) || TextUtils.isEmpty(offlineval)) {
                            WLog.e(TAG,"threeFields is null");
                            resultCallback.onFailed("下载卡失败,错误码ER024!","download_card");//threeFields is null,下载卡信息失败
                            return;
                        } else {

                            int flag = (DownloadCardDataMode.this.card_flag.equals("1") == true ? 1:0);
                            WLog.e(TAG,"flag = " + flag);

                            short threeFields_result = NativeLib.getInstance().updateCardADF01wallet(
                                    Integer.valueOf(fee_total).intValue(),
                                    (short)(Integer.valueOf(offlineval).intValue()),
                                    (short)(Integer.valueOf(onlineval).intValue()),
                                    (byte)flag);
                            WLog.e(TAG,"threeFields_result = " + threeFields_result);

                            if(threeFields_result != 0) {
                                resultCallback.onFailed("下载卡失败,错误码ER025!","download_card");//threeFields_result != 0,下载卡信息失败
                                return;
                            }
                        }
                        WLog.e(TAG,"....................");
                        NativeLib.getInstance().updateCardEnd(card_ver);

                        PreferencesManager.put(mContext,"card_flag","0");
                        PreferencesManager.put(mContext,"login_flag","");
                        PreferencesManager.put(DownloadCardDataMode.this.mContext,"card_no",card_no);
                        resultCallback.onSuccess("下载卡信息成功!","download_card");
                    } else {
                        //Toast.makeText(DownloadCardDataMode.this.mContext, "未获取到正确的卡信息!", Toast.LENGTH_SHORT).show();
                        resultCallback.onFailed("下载卡失败,错误码ER027!","download_card");//未获取到正确的卡信息!
                    }
                } else {
                    //Toast.makeText(DownloadCardDataMode.this.mContext, "更新卡信息失败!", Toast.LENGTH_SHORT).show();
                    resultCallback.onFailed("下载卡失败,错误码ER028!","download_card");//更新卡信息失败!
                }
            }catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(DownloadCardDataMode.this.mContext, "更新卡信息过程中出现异常!", Toast.LENGTH_SHORT).show();
                resultCallback.onFailed("下载卡失败,错误码ER029!","download_card");//更新卡信息过程中出现异常!
            }
        }
    }
}
