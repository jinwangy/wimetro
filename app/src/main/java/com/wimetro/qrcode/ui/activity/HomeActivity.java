package com.wimetro.qrcode.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.otech.yoda.utils.Pager;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.adapter.PullableAdapter;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.core.TokenManager;
import com.wimetro.qrcode.common.utils.ActivityCollector;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.StationCacheDao;
import com.wimetro.qrcode.greendao.entity.StationCache;
import com.wimetro.qrcode.jni.NativeLib;
import com.wimetro.qrcode.mode.GetStationDataMode;
import com.wimetro.qrcode.mode.Presenter;
import com.wimetro.qrcode.mode.ReportOfflineStationDataMode;
import com.wimetro.qrcode.mode.interfaces.IResult;
import com.wimetro.qrcode.pulltorefreshlib.PullToRefreshLayout;
import com.wimetro.qrcode.pulltorefreshlib.PullableController;
import com.wimetro.qrcode.pulltorefreshlib.PullableListView;

import java.util.List;

import butterknife.Bind;

/**
 * jwyuan on 2017-9-18 11:22.
 */

public class HomeActivity extends BaseActivity implements PullableController.Callback<StationCache>,IResult {

    private String TAG = HomeActivity.class.getSimpleName();

    @Bind(R.id.card_iv) ImageView mImageView;
    @Bind(R.id.card_no_tv) TextView mTextView;

    @Bind(R.id.refresh_view) PullToRefreshLayout pullToRefreshLayout;
    @Bind(R.id.content_view) PullableListView listView;

    private PullableAdapter adapter;
    private PullableController<StationCache> mPullableController;

    private int pageNumber;
    private int pageSize;

    private StationCacheDao mStationCacheDao;

    private Presenter presenter_downloadcard;
    private Presenter presenter_getstationdata;
    private Presenter presenter_reportofflinestationdata;
    private Presenter presenter_requestCert;
    private Presenter presenter_checkupdate;

    private Context mContext;
    private static final int LOGOUT = 1;
    //private TokenManager mTokenManager;
	
	private TradeRefreshReceiver receiver;
    private IntentFilter filter;

    private TokenRefreshReceiver receiver_token;
    private IntentFilter filter_token;

    protected Dialog hceDialog;
	
    @Override
    public String setHeaderTitle() {
        return "手机过闸";
    }

    @Override
    protected Object initLayout() {
        return R.layout.home;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        WLog.e(TAG,"excuteOnCreate");
        mContext = HomeActivity.this;
		//mTokenManager = new TokenManager(this);
        mImageView.setImageResource(R.drawable.card_deault);

        receiver = new TradeRefreshReceiver();
        filter = new IntentFilter("com.trade.intent");

        receiver_token = new TokenRefreshReceiver();
        filter_token = new IntentFilter("com.token.intent");

        getTopBar().setupRightView(R.drawable.sz_1,new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mContext,SettingsActivity.class),LOGOUT);
            }
        });

        presenter_downloadcard = new Presenter(this,"downloadcarddata");
        presenter_getstationdata = new Presenter(this,"getstationdata");
        presenter_reportofflinestationdata = new Presenter(this,"reportofflinestationdata");
        presenter_requestCert = new Presenter(this,"request_cert");
        presenter_checkupdate = new Presenter(this,"checkversion");

        mStationCacheDao = DaoManager.getInstance().getSession().getStationCacheDao();

        adapter = new PullableAdapter(this);
        //View header = getLayoutInflater().inflate(R.layout.home_header, null);
        //listView.addHeaderView(header);

        View footer = getLayoutInflater().inflate(R.layout.home_footer, null);
        LinearLayout mFooterParent = new LinearLayout(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        footer.setLayoutParams(param);
        mFooterParent.addView(footer);//在footer的最外面再套一层LinearLayout（即footerParent）
        footer.setVisibility(View.GONE);
        listView.addFooterView(mFooterParent);//把footerParent放到ListView当中

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TradeDetailActivity.class));
            }
        });

        //listView.setEmptyView(empty_image_view);
        listView.setAdapter(adapter);
        listView.setStopPullUp(true);

        mPullableController = new PullableController<StationCache>(this, pullToRefreshLayout,listView, adapter,footer,true);
        mPullableController.setCallback(this);
        mPullableController.setPageSize(6);

        //mPullableController.initData(true);
        //presenter_downloadcard.excute(null);
        //presenter_requestCert.excute(null);
    }

    @Override
    protected void excuteOnResume() {
        super.excuteOnResume();
        WLog.e(TAG, "excuteOnResume");
        registerReceiver(receiver, filter);
        registerReceiver(receiver_token, filter_token);
        presenter_checkupdate.excute(null);
        presenter_requestCert.excute(null);
        presenter_reportofflinestationdata.excute(new ReportOfflineStationDataMode.ReportOffData(this));
    }

    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case LOGOUT:
                LoginActivity.startThisAct(mContext);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(String message, String type) {
        if(type.equals("download_card")) {
            WLog.e(TAG, message);
//            String cardNo = DeviceUtil.getCardNo(this);
//            WLog.e(TAG, "card_no = " + cardNo);
//            mTextView.setText("VIP: "+cardNo);
//            mImageView.setImageResource(R.drawable.card);
//            if (!"".equals(cardNo)) {
//                mTokenManager.getToken();
//            }
            TokenManager.getInstance().startCheckToken(this);
        } else if(type.equals("request_cert")) {
            WLog.e(TAG, message);
            mPullableController.initData(true);
            presenter_downloadcard.excute(null);
        }
    }

    @Override
    public void onFailed(String error, String type) {
        WLog.e(TAG, error);
        if(type.equals("download_card")) {
            Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
            mTextView.setText("");
            mImageView.setImageResource(R.drawable.card_disable);
        } else if(type.equals("request_cert")) {
            Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
            mTextView.setText("");
            mImageView.setImageResource(R.drawable.card_disable);
        }
    }

    @Override
    public void onLoadData(Pager pager) {
        WLog.e(TAG, "onLoadData:pageNumber = " + pager.pageNumber+",pageSize = " + pager.pageSize);
        this.pageNumber = pager.pageNumber;
        this.pageSize = pager.pageSize;

        presenter_getstationdata.excute(new GetStationDataMode.GetStationData(DeviceUtil.getHceId(this), "", "",  "20170101000000",Utils.getCurrent(),
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
        unregisterReceiver(receiver_token);
    }

    @Override
    protected void onStop() {
        super.onStop();
        WLog.e(TAG,"onStop");
        presenter_downloadcard.onStop();
        presenter_getstationdata.onStop();
        presenter_reportofflinestationdata.onStop();
        presenter_requestCert.onStop();
        presenter_checkupdate.onStop();
        TokenManager.getInstance().stopCheckToken(this);
        if (hceDialog != null && hceDialog.isShowing())
            hceDialog.dismiss();
    }

    private void showAlertDialog(String title,String updateMsg) {
        hceDialog = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog_Alert).setTitle(title).setMessage(updateMsg).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        }).create();

        hceDialog.setCanceledOnTouchOutside(false);
        hceDialog.setCancelable(false);
        if(!hceDialog.isShowing()) {
            hceDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WLog.e(TAG,"onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return maybeExit();
        }
        return flag;
    }

    private boolean doubleBackToExitPressedOnce = false;;

    /**
     * 返回键键处理
     * @return
     */
    public boolean maybeExit() {
        if (doubleBackToExitPressedOnce) {
            ActivityCollector.ForcefinishAll();
            System.exit(0);
            return true;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.msg_are_you_sure_to_exit_app,
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

        return false;
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

    public class TokenRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            WLog.e(TAG,"TokenRefreshReceiver:onReceive");
            String action =  intent.getAction();
            if("com.token.intent".equals(action)){
                WLog.e(TAG,"TokenRefreshReceiver:do");

                short needToken =  NativeLib.getInstance().checkToken(Utils.getCurrent());
                WLog.e(TAG,"needToken = " + needToken);
                if(needToken != 0) {
                    mTextView.setText("");
                    mImageView.setImageResource(R.drawable.card_disable);
                    short errorCode = NativeLib.getInstance().getInternelErrorCode();
                    WLog.e(TAG,"errorCode = " + errorCode);
                    String code = Integer.toHexString(((int)errorCode + 65536));
                    String msg = intent.getStringExtra("message");
                    WLog.e(TAG,"msg = " + msg);
                    if(TextUtils.isEmpty(msg)) {
                        //Toast.makeText(HomePageActivity.this, "您的信用额度已不足,请及时联网(" + code.toUpperCase()+")", Toast.LENGTH_SHORT).show();
                        showAlertDialog("提示","您账户的凭证信息已失效，为保证您的正常使用，请及时联网(" + code.toUpperCase()+")");
                    }else {
                        //Toast.makeText(HomePageActivity.this,"提示:"+msg,Toast.LENGTH_SHORT).show();
                        showAlertDialog("提示",msg);
                    }
                } else {
                    String cardNo = DeviceUtil.getCardNo(HomeActivity.this);
                    WLog.e(TAG, "card_no = " + cardNo);
                    mTextView.setText("VIP: "+cardNo);
                    mImageView.setImageResource(R.drawable.card);
                }
            }
        }
    }
}
