package com.wimetro.qrcode.mode;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.core.TokenManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.HEX;
import com.wimetro.qrcode.common.utils.NotificationUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.StationDao;
import com.wimetro.qrcode.greendao.entity.Station;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.http.bean.Report;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-10-31 10:31.
 */

public class ReportStationDataMode implements IMode {

    private String TAG = ReportStationDataMode.class.getSimpleName();
    private Context mContext;

    private ExecutorService mFull_task_excutor;
    private ReportStationDataTask reportStationDataTask;

    private Presenter presenter;
    private StationDao mStationDao;

    private String _infoId;
    private String _info_type;

    private NotificationUtil notiUtil;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        //TaskUtils.cancelTaskInterrupt(reportStationDataTask);
        if(presenter == null) {
            presenter = new Presenter(new UploadCardDataMode.UploadCardResult(), "uploadcarddata");
        }

        if(mStationDao == null) {
            mStationDao = DaoManager.getInstance().getSession().getStationDao();
        }
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(reportStationDataTask);
        if(presenter != null) {
            presenter.onStop();
        }
    }

    public void excute(final IResult result, final IData data) {
        if(result == null || data == null) return;

        ReportStationData reportData = (ReportStationData)data;
        this.mContext = reportData.getContext();

        String type = HEX.bytesToHex(reportData.tradeTypeArray);
        if(TextUtils.isEmpty(type) || type.length() < 2) return;

        String trade_type = type.substring(0,2);
        WLog.e("ReportData","trade_type = " + trade_type);
        if(trade_type.equals("26") || trade_type.equals("27") || trade_type.equals("17")) {
            String trade_data = HEX.bytesToAscii(reportData.tradeDataArray).substring(0,180);
            WLog.e("ReportData","trade_data = " + trade_data);

            _info_type = "";
            String title;
            if(trade_type.equals("26")) {
                _info_type = "1";
                title = "进站";
            } else if(trade_type.equals("27")) {
                _info_type = "1";
                title = "出站";
            } else {
                _info_type = "0";
                title = "更新";
            }

            if(notiUtil == null) {
                notiUtil = new NotificationUtil(this.mContext);
            }
            notiUtil .postNotification(title + "提醒","你已成功"+ title + "!");

            String _sam_code = getFieldValue(trade_data,30,38);
            String _logical_code = getFieldValue(trade_data,38,54);
            String _deal_time = getFieldValue(trade_data,76,90);
            String _tac = getFieldValue(trade_data,128,136);
            String _rand = ((new Random()).nextInt(899) + 100) + "";

            WLog.e(TAG,"_sam_code = " + _sam_code + ",_logical_code = " + _logical_code + ",_deal_time = " + _deal_time + ",_tac = " + _tac + ",_rand = " + _rand);

            _infoId = _sam_code + _logical_code + _deal_time + _tac + _rand;
            //Log.e(TAG,"infoId = " + _infoId);

            saveStationToLocal(trade_data,_info_type,_infoId);

            presenter.excute(new UploadCardDataMode.UploadCardData(this.mContext));

            if(!Utils.isNetworkAvailable(this.mContext)) {
                //Toast.makeText(this.mContext, "网络连接不可用,无法上报进出站信息!", Toast.LENGTH_SHORT).show();
                return;
            }

            reportStationDataTask = new ReportStationDataTask(this.mContext);
            reportStationDataTask.executeOnExecutor(mFull_task_excutor,DeviceUtil.getHceId(this.mContext),_info_type,getFieldValue(trade_data,0,8),getFieldValue(trade_data,8,10),getFieldValue(trade_data,10,18),getFieldValue(trade_data,18,22),
                    getFieldValue(trade_data,22,24),getFieldValue(trade_data,24,26),getFieldValue(trade_data,26,28),getFieldValue(trade_data,28,30),
                    getFieldValue(trade_data,30,38),getFieldValue(trade_data,38,54),getFieldValue(trade_data,54,60),getFieldValue(trade_data,60,68),
                    getFieldValue(trade_data,68,76),getFieldValue(trade_data,76,90),getFieldValue(trade_data,90,98),getFieldValue(trade_data,98,106),
                    getFieldValue(trade_data,106,114),getFieldValue(trade_data,114,128),getFieldValue(trade_data,128,136),getFieldValue(trade_data,136,140),
                    getFieldValue(trade_data,140,144),getFieldValue(trade_data,144,148),getFieldValue(trade_data,148,162),"",
                    "","","","",
                    "","","","",
                    "","",_infoId);
        } else {

        }
    }

    private String getFieldValue(String data, int beginIndex, int endIndex) {
        return data.substring(beginIndex,endIndex).trim();
    }

    private void saveStationToLocal(String trade_data,String type,String info) {
        WLog.e(TAG,"saveStationToLocal");
        Station station = new Station();

        station.setHce_id(DeviceUtil.getHceId(this.mContext));
        station.setInfo_type(type);
        station.setDeal_device_code(getFieldValue(trade_data,0,8));
        station.setDeal_seq_group_no(getFieldValue(trade_data,8,10));
        station.setDeal_seq_no(getFieldValue(trade_data,10,18));
        station.setDeal_station(getFieldValue(trade_data,18,22));

        station.setDeal_type(getFieldValue(trade_data,22,24));
        station.setMain_type(getFieldValue(trade_data,24,26));
        station.setSub_type(getFieldValue(trade_data,26,28));
        station.setArea_code(getFieldValue(trade_data,28,30));
        station.setSam_code(getFieldValue(trade_data,30,38));
        station.setLogical_code(getFieldValue(trade_data,38,54));


        station.setRead_count(getFieldValue(trade_data,54,60));
        station.setDeal_amount(getFieldValue(trade_data,60,68));
        station.setBalance(getFieldValue(trade_data,68,76));
        station.setDeal_time(getFieldValue(trade_data,76,90));

        station.setLast_deal_dev_code(getFieldValue(trade_data,90,98));
        station.setLast_deal_sq_no(getFieldValue(trade_data,98,106));
        station.setLast_deal_amount(getFieldValue(trade_data,106,114));
        station.setLast_deal_time(getFieldValue(trade_data,114,128));

        station.setTac(getFieldValue(trade_data,128,136));
        station.setDegrade_mode(getFieldValue(trade_data,136,140));
        station.setIn_gate_station(getFieldValue(trade_data,140,144));
        station.setIn_gate_dev(getFieldValue(trade_data,144,148));
        station.setIn_gate_time(getFieldValue(trade_data,148,162));

        station.setPay_type("");
        station.setPay_card_no("");
        station.setDestination_station("");
        station.setDeal_cause("");
        station.setDeal_total_amount("");
        station.setDeposit("");
        station.setDeal_fee("");

        station.setExpiry_date("");
        station.setLast_expiry_date("");
        station.setOper_id("");
        station.setWork_sq_no("");


        station.setInfo_id(info);
        station.setIs_report("0");

        mStationDao.insert(station);
    }

    private void updateStationLocal(String infoId) {
        Station station = mStationDao.queryBuilder().where(StationDao.Properties.Info_id.eq(infoId)).build().unique();
        station.setIs_report("1");
        mStationDao.update(station);
    }

    public static class ReportStationData implements IData {

        private byte[] tradeTypeArray;
        private byte[] tradeDataArray;

        private Context mContext;

        public ReportStationData(byte[] tradeTypeArray, byte[] tradeDataArray,Context mContext) {
            this.tradeTypeArray = tradeTypeArray;
            this.tradeDataArray = tradeDataArray;
            this.mContext = mContext;
        }

        public byte[] getTradeTypeArray() {
            return tradeTypeArray;
        }

        public void setTradeTypeArray(byte[] tradeTypeArray) {
            this.tradeTypeArray = tradeTypeArray;
        }

        public byte[] getTradeDataArray() {
            return tradeDataArray;
        }

        public void setTradeDataArray(byte[] tradeDataArray) {
            this.tradeDataArray = tradeDataArray;
        }

        public Context getContext() {
            return mContext;
        }

        public void setmContext(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public String getDescription() {
            return "ReportStationData:{tradeType = " + HEX.bytesToHex(this.tradeTypeArray) + ",tradeData = " +
                    HEX.bytesToAscii(this.tradeDataArray) + "}";
        }

    }

    public static class ReportStationResult implements IResult {
        @Override
        public void onSuccess(String message, String type) {

        }
        @Override
        public void onFailed(String error, String type) {

        }
    }

    class ReportStationDataTask extends AsyncTask<String, Integer,  ApiResponse<Report> > {
        private Api api;
        private Context context;

        private ApiResponse<Report> result;

        public ReportStationDataTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<Report> doInBackground(String... params) {

            try {
                WLog.e(TAG,"upStationState task");
                result = api.upStationState(context,
                        params[0], params[1], params[2], params[3], params[4], params[5],
                        params[6], params[7], params[8], params[9], params[10], params[11],
                        params[12], params[13], params[14], params[15], params[16], params[17],
                        params[18], params[19], params[20], params[21], params[22], params[23],
                        params[24], params[25], params[26], params[27], params[28], params[29],
                        params[30], params[31], params[32], params[33], params[34], params[35], params[36]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            WLog.e(TAG,"station = " + result);
            if (ApiRequest.handleResponse(ReportStationDataMode.this.mContext, result,false)) {
                WLog.e(TAG,"reportStationData is ok!");
                Report report = result.getObject();
                String _info = report.getInfo_id();
                WLog.e(TAG,"ReportStationDataMode,infoId = " + _info);
                updateStationLocal(_info);

                Intent intent = new  Intent();
                intent.setAction("com.trade.intent");
                (ReportStationDataMode.this.mContext).sendBroadcast(intent);
            } else {
                WLog.e(TAG,"reportStationData is not ok!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Report> result) {
            super.onPostExecute(result);
        }
    }
}
