package com.wimetro.qrcode.mode;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.entity.StationCache;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;
import com.wimetro.qrcode.pulltorefreshlib.PullableController;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * jwyuan on 2017-10-31 10:32.
 */

public class GetStationDataMode implements IMode {

    private static String TAG = GetStationDataMode.class.getSimpleName();

    private BaseActivity mBaseActivity;

    private ExecutorService mFull_task_excutor;
    private GetStationDataTask getStationDataTask;

    private GetStationData getStationData;

    public void onCreate() {
        if(mFull_task_excutor == null) {
            mFull_task_excutor = ApiClient.createPool();
        }
    }

    public void onStop() {
        TaskUtils.cancelTaskInterrupt(getStationDataTask);
    }

    public void excute(final IResult result, final IData data) {
        mBaseActivity = (BaseActivity)result;
        getStationData = (GetStationData) data;

        if(!Utils.isNetworkAvailable(mBaseActivity)) {
            Toast.makeText(mBaseActivity, "网络连接不可用,无法获取最新的交易信息!", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskUtils.cancelTaskInterrupt(getStationDataTask);
        getStationDataTask = new GetStationDataTask(mBaseActivity);
        getStationDataTask.executeOnExecutor(mFull_task_excutor,getStationData.getHce_id(),
                getStationData.getDeal_type(),getStationData.getInfo_type(),getStationData.getStart_time(),
                getStationData.getEnd_time(),getStationData.getPage(),getStationData.getRows());
    }

    class GetStationDataTask extends AsyncTask<String, Integer,  ApiResponse<StationCache> > {
        private Api api;
        private Context context;

        public GetStationDataTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ApiResponse<StationCache> doInBackground(String... params) {
            try {
                WLog.e(TAG,"queryStationStateByPage task");
                return api.queryStationStateByPage(context, params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse<StationCache> result) {
            super.onPostExecute(result);
            WLog.e(TAG,"getStation = " + result);

            if (ApiRequest.handleResponse(GetStationDataMode.this.mBaseActivity, result,false)) {
                List<StationCache> stationList = result.getList();
                WLog.e(TAG,"onRefreshUI");

                getStationData.getmPullableController().onRefreshUI(stationList,true);
            } else {
                WLog.e(TAG,"get Data is wrong");
                Toast.makeText(GetStationDataMode.this.mBaseActivity, "交易数据刷新失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class GetStationData implements IData {
        private String hce_id;
        private String deal_type;
        private String info_type;
        private String start_time;
        private String end_time;
        private String page;
        private String rows;

        private PullableController<StationCache> mPullableController;

        public GetStationData(String hce_id, String deal_type, String info_type, String start_time, String end_time, String page, String rows, PullableController<StationCache> mPullableController) {
            this.hce_id = hce_id;
            this.deal_type = deal_type;
            this.info_type = info_type;
            this.start_time = start_time;
            this.end_time = end_time;
            this.page = page;
            this.rows = rows;

            this.mPullableController = mPullableController;
        }

        public String getHce_id() {
            return hce_id;
        }

        public void setHce_id(String hce_id) {
            this.hce_id = hce_id;
        }

        public String getDeal_type() {
            return deal_type;
        }

        public void setDeal_type(String deal_type) {
            this.deal_type = deal_type;
        }

        public String getInfo_type() {
            return info_type;
        }

        public void setInfo_type(String info_type) {
            this.info_type = info_type;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getRows() {
            return rows;
        }

        public void setRows(String rows) {
            this.rows = rows;
        }

        public PullableController<StationCache> getmPullableController() {
            return mPullableController;
        }

        public void setmPullableController(PullableController<StationCache> mPullableController) {
            this.mPullableController = mPullableController;
        }

        @Override
        public String getDescription() {
            return "GetStationData{" +
                    "hce_id='" + hce_id + '\'' +
                    ", deal_type='" + deal_type + '\'' +
                    ", info_type='" + info_type + '\'' +
                    ", start_time='" + start_time + '\'' +
                    ", end_time='" + end_time + '\'' +
                    ", page='" + page + '\'' +
                    ", rows='" + rows + '\'' +
                    '}';
        }
    }

}
