package com.wimetro.qrcode.mode;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.core.DaoManager;
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
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-11-14 15:16.
 */

public class ReportOfflineStationDataMode implements IMode {
    private String TAG = ReportOfflineStationDataMode.class.getSimpleName();
    private Context mContext;

    private ExecutorService mFull_task_excutor;
    private ReportOfflineStationDataTask reportOfflineStationDataTask;

    private Presenter presenter;
    private StationDao mStationDao;
    private List<Station> mStationList;

    private IResult resultCallback;

    private boolean isRunning;

    private static class SingletonHolder {
        private static final ReportOfflineStationDataMode INSTANCE = new ReportOfflineStationDataMode();
    }
    private ReportOfflineStationDataMode (){}
    public static final ReportOfflineStationDataMode getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
        //TaskUtils.cancelTaskInterrupt(reportOfflineStationDataTask);
        if(presenter == null) {
            presenter = new Presenter(new UploadCardDataMode.UploadCardResult(), "uploadcarddata");
        }

        if(mStationDao == null) {
            mStationDao = DaoManager.getInstance().getSession().getStationDao();
        }
    }

    public void excute(final IResult result, final IData data) {
        if(isRunning) return;
        isRunning = true;

        if(result == null) {
            isRunning = false;
            return;
        }
        this.resultCallback = result;

        this.mContext = (Context)(((ReportOffData)data).getContext());
        if(this.mContext == null) {
            isRunning = false;
            return;
        }

        mStationList = mStationDao.queryBuilder().where(StationDao.Properties.Is_report.eq("0")).build().list();
        if(mStationList != null) {
            WLog.e("ReportOffLineData","offline data need to report , size = " + mStationList.size());
            if(!Utils.isNetworkAvailable(this.mContext)) {
                isRunning = false;
                resultCallback.onFailed("","report_offline_data");
                return;
            }

            reportOfflineStationDataTask = new ReportOfflineStationDataTask(this.mContext);
            reportOfflineStationDataTask.executeOnExecutor(mFull_task_excutor);
        } else {
            isRunning = false;
            WLog.e("ReportOffLineData","no offline data need to report!");
            resultCallback.onSuccess("","report_offline_data");
        }
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(reportOfflineStationDataTask);
        if(presenter != null) {
            presenter.onStop();
        }
        this.resultCallback = null;
        //isRunning = false;
    }

    private void updateStationLocal(String infoId) {
        Station station = mStationDao.queryBuilder().where(StationDao.Properties.Info_id.eq(infoId)).build().unique();
        station.setIs_report("1");
        mStationDao.update(station);
    }

    public static class ReportOffData implements IData {
        private Context mContext;
        public ReportOffData(Context mContext) {
            this.mContext = mContext.getApplicationContext();
        }

        public Context getContext() {
            return mContext;
        }
        @Override
        public String getDescription() {
            return null;
        }
    }

    class ReportOfflineStationDataTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {
        private Api api;
        private Context context;

        private ApiResponse<Report> reportResult;

        public ReportOfflineStationDataTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<Void> doInBackground(String... params) {
            WLog.e(TAG,"upOfflineStationState task");
            isRunning = true;
            for(int i = 0 ;i < mStationList.size();i++ ) {
                Station station = mStationList.get(i);
                try {
                    reportResult = api.upStationState(context,
                            station.getHce_id(), station.getInfo_type(),station.getDeal_device_code(), station.getDeal_seq_group_no(), station.getDeal_seq_no(),
                            station.getDeal_station(), station.getDeal_type(), station.getMain_type(), station.getSub_type(), station.getArea_code(),
                            station.getSam_code(), station.getLogical_code(), station.getRead_count(), station.getDeal_amount(), station.getBalance(),
                            station.getDeal_time(), station.getLast_deal_dev_code(), station.getLast_deal_sq_no(), station.getLast_deal_amount(), station.getLast_deal_time(),
                            station.getTac(), station.getDegrade_mode(), station.getIn_gate_station(), station.getIn_gate_dev(), station.getIn_gate_time(),
                            station.getPay_type(), station.getPay_card_no(), station.getDestination_station(), station.getDeal_cause(), station.getDeal_total_amount(),
                            station.getDeposit(), station.getDeal_fee(), station.getExpiry_date(),station.getLast_expiry_date(), station.getOper_id(),
                            station.getWork_sq_no(), station.getInfo_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                WLog.e(TAG,"(offlineReport)result = " + reportResult);
                if (ApiRequest.handleResponse(ReportOfflineStationDataMode.this.mContext, reportResult,false)) {
                    WLog.e(TAG,"offlineReport is ok!");
                    Report report = reportResult.getObject();
                    //if(report != null) {
                    String _info = report.getInfo_id();
                    WLog.e(TAG,"ReportOfflineStationDataMode,infoId = " + _info);
                    updateStationLocal(_info);
                    presenter.excute(new UploadCardDataMode.UploadCardData(ReportOfflineStationDataMode.this.mContext));
//                        } else {
//                            Log.e(TAG,"offlineReport is null!");
//                        }
                } else {
                    WLog.e(TAG,"offlineReport is not ok!");
                }
            }
            isRunning = false;
            if(mStationList.size() > 0) {
                Intent intent = new  Intent();
                intent.setAction("com.trade.intent");
                (ReportOfflineStationDataMode.this.mContext).sendBroadcast(intent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Void> result) {
            super.onPostExecute(result);
            resultCallback.onSuccess("","report_offline_data");
        }
    }
}
