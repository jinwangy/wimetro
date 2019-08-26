package com.wimetro.qrcode.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.otech.yoda.utils.Pager;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.adapter.PullableAdapter;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.StationCacheDao;
import com.wimetro.qrcode.greendao.entity.StationCache;
import com.wimetro.qrcode.mode.GetStationDataMode;
import com.wimetro.qrcode.mode.Presenter;
import com.wimetro.qrcode.mode.interfaces.IResult;
import com.wimetro.qrcode.pulltorefreshlib.PullToRefreshLayout;
import com.wimetro.qrcode.pulltorefreshlib.PullableController;
import com.wimetro.qrcode.pulltorefreshlib.PullableListView;

import java.util.List;

import butterknife.Bind;

/**
 * jwyuan on 2017-12-1 15:33.
 */

public class TradeDetailActivity extends BaseActivity implements PullableController.Callback<StationCache>,IResult {

    private String TAG = TradeDetailActivity.class.getSimpleName();

    @Bind(R.id.refresh_view_trade)
    PullToRefreshLayout pullToRefreshLayout;
    @Bind(R.id.content_view_trade)
    PullableListView listView;

    private PullableAdapter adapter;
    private PullableController<StationCache> mPullableController;

    private int pageNumber;
    private int pageSize;

    private StationCacheDao mStationCacheDao;

    private Presenter presenter_getstationdata;
    private Context mContext;

    private TradeRefreshReceiver receiver;
    private IntentFilter filter;

    @Override
    public String setHeaderTitle() {
        return "交易明细";
    }

    @Override
    protected Object initLayout() {
        return R.layout.trade_detail;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        WLog.e(TAG, "excuteOnCreate");
        mContext = TradeDetailActivity.this;

        receiver = new TradeRefreshReceiver();
        filter = new IntentFilter("com.trade.intent");

        presenter_getstationdata = new Presenter(this,"getstationdata");
        mStationCacheDao = DaoManager.getInstance().getSession().getStationCacheDao();

        adapter = new PullableAdapter(this);

        //listView.setEmptyView(empty_image_view);
        listView.setAdapter(adapter);

        mPullableController = new PullableController<StationCache>(this, pullToRefreshLayout,listView, adapter,null,false);
        mPullableController.setCallback(this);
        mPullableController.setPageSize(10);
        mPullableController.initData(true);
    }

    @Override
    protected void excuteOnResume() {
        super.excuteOnResume();
        WLog.e(TAG, "excuteOnResume");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onSuccess(String message, String type) {
        WLog.e(TAG, message);
    }

    @Override
    public void onFailed(String error, String type) {
        Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadData(Pager pager) {
        WLog.e(TAG, "onLoadData:pageNumber = " + pager.pageNumber+",pageSize = " + pager.pageSize);
        this.pageNumber = pager.pageNumber;
        this.pageSize = pager.pageSize;

        presenter_getstationdata.excute(new GetStationDataMode.GetStationData(DeviceUtil.getHceId(this), "", "",  "20170101000000", Utils.getCurrent(),
                this.pageNumber + "",this.pageSize + "",mPullableController));
    }

    @Override
    public void onSaveData(List<StationCache> data) {
        WLog.e(TAG, "onSaveData");
        if (data != null) {
            mStationCacheDao.deleteAll();
            mStationCacheDao.insertInTx(data);
        }
    }

    @Override
    public List<StationCache> onLoadCache(Pager pager) {
        WLog.e(TAG, "onLoadCache");
        return mStationCacheDao.loadAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WLog.e(TAG,"onPause");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        WLog.e(TAG,"onStop");
        presenter_getstationdata.onStop();
    }

    @Override
    protected void excuteOnDestroy() {
        super.excuteOnDestroy();
        WLog.e(TAG, "excuteOnDestroy");
    }

    public class TradeRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WLog.e(TAG,"TradeRefreshReceiver:onReceive");
            String action =  intent.getAction();
            if("com.trade.intent".equals(action)){
                WLog.e(TAG,"TradeRefreshReceiver:do");
                if(mPullableController != null) {
                    mPullableController.onGetIntentRefresh();
                }
            }
        }
    }
}
