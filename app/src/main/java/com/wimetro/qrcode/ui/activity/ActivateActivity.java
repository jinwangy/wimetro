package com.wimetro.qrcode.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.CheckBox;
import android.widget.Toast;

//import com.android.volley.Response;
import com.otech.yoda.utils.TaskUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.OpenWebview;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.Log;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.base.BaseApplication;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.core.PreferencesManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.greendao.DaoMaster;
import com.wimetro.qrcode.greendao.UserDao;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.interfaces.IResult;
import com.wimetro.qrcode.http.bean.Message;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.infosec.mobile.android.cert.InfosecCert;
import cn.com.infosec.mobile.android.net.InfosecSSL;
import cn.com.infosec.mobile.android.result.Result;
import cn.com.infosec.mobile.android.sign.InfosecSign;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.constants.ConstantsAPI;

/**
 * jwyuan on 2017-9-18 11:09.
 */

public class ActivateActivity extends BaseActivity implements IWXAPIEventHandler {

    @Bind(R.id.wechat_checkbox)
    CheckBox mWechatCheckbox;

    @Bind(R.id.alipy_checkbox)
    CheckBox mAlipyCheckbox;

    @Bind(R.id.eBank_checkbox)
    CheckBox meBank_checkbox;

    private Context mContext;
    private String hce_id;
    private String CN="",imei,plainText;
    private final String TAG="ActivateActivity";
    private InfosecCert infosecCert;
    private String userId;
    private boolean isActivatIng = false;

    private GainAlipyAgreementPageMessageTask mGainAlipyAgreementPageMessageTask;
    private GainAlipyActivateResultTask mGainAlipyActivateResultTask;
    private GainWeiXinAgreementPageMessageTask mGainWeiXinAgreementPageMessageTask;
    private GainWeiXinActivateResultTask mGainWeiXinActivateResultTask;
    private ExecutorService FULL_TASK_EXECUTOR;
    private static boolean weixinBondCheck = false;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;


    public static void startThisAct(Context mContext,String _hce_id)	{
        Intent intent=new Intent(mContext,ActivateActivity.class);
        intent.putExtra("hce_id_value",_hce_id);
        mContext.startActivity(intent);
    }


    @Override
    public String setHeaderTitle() {
        return "激活账户";
    }

    @Override
    protected Object initLayout() {
        return R.layout.activate;
    }

    @OnClick(R.id.wechat_checkbox)
    public void onWechatBoxClick() {

//        if(true) {
//            Toast.makeText(mContext, "暂不支持绑定微信支付", Toast.LENGTH_SHORT).show();
//            return;
//        }
        mWechatCheckbox.setChecked(true);
        mAlipyCheckbox.setChecked(false);
    }

    @OnClick(R.id.alipy_checkbox)
    public void onAlipyBoxClick() {

        mWechatCheckbox.setChecked(false);
        mAlipyCheckbox.setChecked(true);
    }

    @OnClick(R.id.eBank_checkbox)
    public void onEBankBoxClick() {
        if(true) {
            Toast.makeText(mContext, "暂不支持绑定工银e支付", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @OnClick(R.id.wechat_rl)
    public void onWechatClick() {

//        if(true) {
//            Toast.makeText(mContext, "暂不支持绑定微信支付", Toast.LENGTH_SHORT).show();
//            return;
//        }
        mWechatCheckbox.setChecked(true);
        mAlipyCheckbox.setChecked(false);
    }

    @OnClick(R.id.alipy_rl)
    public void onAlipyClick() {

        mWechatCheckbox.setChecked(false);
        mAlipyCheckbox.setChecked(true);
    }

    @OnClick(R.id.eBank_rl)
    public void onEBankClick() {
        if(true) {
            Toast.makeText(mContext, "暂不支持绑定工银e支付", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @OnClick(R.id.go_bind_btn)
    public void onGoBindClick() {

        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this.mContext, "网络连接不可用,无法激活账户!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isActivatIng) {
            Toast.makeText(this.mContext, "正在激活账户，请稍候...", Toast.LENGTH_SHORT).show();
            return;
        }
//        if(mWechatCheckbox.isChecked()) {
//            Toast.makeText(mContext, "暂不支持绑定微信支付", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (mAlipyCheckbox.isChecked()) {
            if (!checkAliPayInstalled(this)) {
                Toast.makeText(mContext, "请先安装支付宝客户端然后进行支付绑定", Toast.LENGTH_SHORT).show();
                return;
            }

            TaskUtils.cancelTaskInterrupt(mGainAlipyAgreementPageMessageTask);
            mGainAlipyAgreementPageMessageTask = new GainAlipyAgreementPageMessageTask(this);
            mGainAlipyAgreementPageMessageTask.executeOnExecutor(FULL_TASK_EXECUTOR, hce_id);
            //mGainAgreementPageMessageTask.execute(phoneNum);
            weixinBondCheck = false;
        } else {
            if (!checkWeiXinInstalled(this)) {
                Toast.makeText(mContext, "请先安装微信客户端然后进行支付绑定", Toast.LENGTH_SHORT).show();
                return;
            }
            TaskUtils.cancelTaskInterrupt(mGainWeiXinAgreementPageMessageTask);
            mGainWeiXinAgreementPageMessageTask = new GainWeiXinAgreementPageMessageTask(this);
            mGainWeiXinAgreementPageMessageTask.executeOnExecutor(FULL_TASK_EXECUTOR, hce_id);
            //mGainAgreementPageMessageTask.execute(phoneNum);
            weixinBondCheck = true;
        }
    }

    public  boolean checkAliPayInstalled(Context context) {
        return Utils.isInstalled(context,"com.eg.android.AlipayGphone");
    }

    public  boolean checkWeiXinInstalled(Context context) {
        return Utils.isInstalled(context,"com.tencent.mm");
    }


    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();

        mContext = ActivateActivity.this;

        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();

        hce_id = getIntent().getStringExtra("hce_id_value");
        WLog.e(TAG,"OnCreate,hce_id = " + hce_id);
        //UserDao dao = DaoManager.getInstance().getSession().getUserDao();
        //queryData(dao);

        TelephonyManager telephonemanage = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonemanage.getDeviceId();

        infosecCert = new InfosecCert();

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, AppConfig.WEIXIN_APP_ID, true);


    }

    private void queryData(UserDao dao) {
        List<User> users = dao.loadAll();
        userId = "";
        int maxSize = users.size();
        if (maxSize > 0)
            userId += users.get(maxSize - 1).getUser_id();
        WLog.e("Log","userName="+userId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        WLog.e("Log","onNewIntent");
        setIntent(intent);
    }

    @Override
    protected void excuteOnResume() {
        super.excuteOnResume();
        WLog.e("Log","onResume");
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        String dataString = intent.getDataString();
        Uri uri = intent.getData();

        WLog.e(TAG,"scheme = " + scheme + ",dataString = " + dataString + ",uri = " + uri);

        if (uri != null && "T".equals(uri.getQueryParameter("is_success"))){
            WLog.e("Log","签约成功！");

            if(!Utils.isNetworkAvailable(this)) {
                Toast.makeText(this.mContext, "网络连接不可用,无法获取激活结果!", Toast.LENGTH_SHORT).show();
                return;
            }

            setAlipyGainActivateResultTask();
        }else{
            WLog.e("Log","weixinBond="+weixinBondCheck+",mWechatCheckbox.isChecked()="+mWechatCheckbox.isChecked());
            if (weixinBondCheck && mWechatCheckbox.isChecked()) {
                setWeiXinGainActivateResultTask();
            }


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WLog.e("Log","onPause");
        TaskUtils.cancelTaskInterrupt(mGainAlipyAgreementPageMessageTask);
        TaskUtils.cancelTaskInterrupt(mGainAlipyActivateResultTask);
        TaskUtils.cancelTaskInterrupt(mGainWeiXinAgreementPageMessageTask);
        TaskUtils.cancelTaskInterrupt(mGainWeiXinActivateResultTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WLog.e("Log","onDestroy");
    }

    /**
     * 判断是否自动滚动
     */
    private boolean isAlipyRunning = false;
    private boolean isWeiXinRunning = false;
    private static final int ALIPY_ORDER_FAIL = -1;
    private static final int ALIPY_ORDER_SUCCESS = 0;
    private static final int ALIPY_ORDER_CHECK = 1;
    private static final int WEIXIN_ORDER_FAIL = 2;
    private static final int WEIXIN_ORDER_SUCCESS = 3;
    private static final int WEIXIN_ORDER_CHECK = 4;
    private  int countAlipy = 0;
    private static final int ALIPY_MAX_COUNT = 10;
    private  int countWeiXin = 0;
    private static final int WEIXIN_MAX_COUNT = 10;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case ALIPY_ORDER_FAIL:
                    Toast.makeText(mContext, "绑定支付方式失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case ALIPY_ORDER_SUCCESS:
                    isAlipyRunning = false;
                    countAlipy = 0;

                    finish();
                    break;
                case ALIPY_ORDER_CHECK:
                    WLog.e("Log","##countAlipy ="+countAlipy+",isAlipyRunning = "+isAlipyRunning);
                    if(isAlipyRunning){
                        countAlipy ++;
                        setAlipyGainActivateResultTask();

                    }
                    break;
                case WEIXIN_ORDER_FAIL:
                    Toast.makeText(mContext, "绑定微信方式失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case WEIXIN_ORDER_SUCCESS:
                    isWeiXinRunning = false;
                    countWeiXin = 0;

                    finish();
                    break;
                case WEIXIN_ORDER_CHECK:
                    WLog.e("Log","##countWeiXin ="+countWeiXin+",isWeiXinRunning = "+isWeiXinRunning);
                    if(isWeiXinRunning){
                        countWeiXin ++;
                        setWeiXinGainActivateResultTask();

                    }
                    break;
                default:
                    break;
            }
        };
    };


    private void  setAlipyGainActivateResultTask() {
        TaskUtils.cancelTaskInterrupt(mGainAlipyActivateResultTask);
        //FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();
        mGainAlipyActivateResultTask = new GainAlipyActivateResultTask(mContext);
        mGainAlipyActivateResultTask.executeOnExecutor(FULL_TASK_EXECUTOR,hce_id);
        //mGainActivateResultTask.execute(userId);

    }

    private void  setWeiXinGainActivateResultTask() {
        weixinBondCheck = false;
        TaskUtils.cancelTaskInterrupt(mGainWeiXinActivateResultTask);
        //FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();
        mGainWeiXinActivateResultTask = new GainWeiXinActivateResultTask(mContext);
        mGainWeiXinActivateResultTask.executeOnExecutor(FULL_TASK_EXECUTOR,hce_id);
        //mGainActivateResultTask.execute(userId);

    }

    private void requestWeixinPage(String url) {
        OpenWebview.Req req = new OpenWebview.Req();
        req.url = url;
        api.sendReq(req);
    }


//    private void requestCert() {
//        WLog.e("Log","CN="+CN+",imei="+imei);
//        //证书下载
//        infosecCert.requestCert(CN, AppConfig.CERT_Algorithm, AppConfig.KEY_LENGTH,imei,AppConfig.VALIDITY, new Result.ResultListener() {
//            @Override
//            public void handleResult(Result result) {
//                switch (result.getResultID()){
//                    case Result.OPERATION_SUCCEED:
//                        Toast.makeText(mContext,"激活成功!",Toast.LENGTH_SHORT).show();
//                        //证书下载成功
//                        //Toast.makeText(ActivateActivity.this,"证书下载成功",Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"证书下载成功:"+result.getResultDesc());
//                        break;
//                    case Result.NETWORK_UNAVAILABLE:
//                        Toast.makeText(ActivateActivity.this,"证书下载过程中网络不可用",Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"网络不可用:"+result.getResultDesc());
//                        break;
//                    case Result.JSON_EXCAPTION:
//                        Toast.makeText(ActivateActivity.this,"证书下载过程中JSON解析异常",Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"JSON解析异常:"+result.getResultDesc());
//                        break;
//                    default:
//                        Toast.makeText(ActivateActivity.this,"证书下载过程中出现未知异常",Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"未知异常:"+result.getResultDesc());
//                        break;
//                }
//
//                stopLoading();
//                //startActivity(new Intent(mContext, LoginActivity.class));
//                finish();
//            }
//        });
//    }
//
//    private void certUpdate() {
//        //证书更新
//        infosecCert.updateCert(CN, imei, AppConfig.VALIDITY,new Result.ResultListener() {
//            @Override
//            public void handleResult(Result result) {
//                switch(result.getResultID()){
//                    case Result.OPERATION_SUCCEED:
//                        //证书下载成功
//                        Toast.makeText(ActivateActivity.this,"证书更新成功",Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"证书更新成功:"+result.getResultDesc());
//                        break;
//                    case Result.NETWORK_UNAVAILABLE:
//                        Toast.makeText(ActivateActivity.this,"网络不可用"+result.getResultDesc(),Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"网络不可用:"+result.getResultDesc());
//                        break;
//                    case Result.JSON_EXCAPTION:
//                        Toast.makeText(ActivateActivity.this,"JSON解析异常"+result.getResultDesc(),Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"JSON解析异常:"+result.getResultDesc());
//                        break;
//                    default:
//                        Toast.makeText(ActivateActivity.this,"出现未知异常"+result.getResultDesc(),Toast.LENGTH_SHORT).show();
//                        WLog.e(TAG,"未知异常:"+result.getResultDesc());
//                        break;
//                }
//            }
//        });
//    }
//
//    private void certInfo() {
//        //获取证书信息
//        String dn = infosecCert.getCertInfo(CN, 0);
//        String sn = infosecCert.getCertInfo(CN, 1);
//        String issuer = infosecCert.getCertInfo(CN, 2);
//        String keyUsage = infosecCert.getCertInfo(CN, 4);
//        String keyLength = infosecCert.getCertInfo(CN, 5);
//        String notBefore = infosecCert.getCertInfo(CN, 6);
//        String notAfter = infosecCert.getCertInfo(CN, 7);
//        String alg = infosecCert.getCertInfo(CN, 8);
//        String version = infosecCert.getCertInfo(CN, 9);
//        WLog.e(TAG, "version = " + version + "\r\n" +
//                "dn = " + dn + "\r\n" +
//                "sn = " + sn + "\r\n" +
//                "issuer = " + issuer + "\r\n" +
//                "keyUsage = " + keyUsage + "\r\n" +
//                "keyLength = " + keyLength + "\r\n" +
//                "notBefore = " + notBefore + "\r\n" +
//                "notAfter = " + notAfter + "\r\n" +
//                "alg = " + alg + "\r\n");
//    }







    class GainAlipyAgreementPageMessageTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;


        public GainAlipyAgreementPageMessageTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isActivatIng = true;
            startLoading();
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                WLog.e(TAG,"gainAgreementPageMessage task");
                return api.gainAlipyAgreementPageMessage(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            isActivatIng = false;
            //Log.i("Log","url="+url);
            if (ApiRequest.handleResponse(context, result)) {

                User object = (User)result.getObject();
                if (object == null) {
                    Toast.makeText(mContext,"获取签约界面报文失败！",Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = object.getRequestMessage();
                url = URLEncoder.encode(url);
                url = "alipays://platformapi/startapp?appId=20000067&url="+url;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                //finish();


            } else {
                //Toast.makeText(mContext,"获取签约界面链接失败！",Toast.LENGTH_SHORT).show();
            }
        }

    }

    class GainAlipyActivateResultTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;


        public GainAlipyActivateResultTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (countAlipy == 0) {
                startLoading();
                isActivatIng = true;
            }
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                WLog.e(TAG,"gainActivateResult task");
                return api.activateAlipyUserAction_gainActivateResult(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            if (result == null ) {
                if (countAlipy < ALIPY_MAX_COUNT) {
                    isAlipyRunning = true;
                    mHandler.sendEmptyMessageDelayed(ALIPY_ORDER_CHECK, AppConfig.AD_TIME);
                } else {
                    stopLoading();
                    isActivatIng = false;
                    Toast.makeText(mContext, "激活失败!", Toast.LENGTH_SHORT).show();
                    WLog.e(TAG, "激活失败");
                    TaskUtils.cancelTaskInterrupt(mGainAlipyAgreementPageMessageTask);
                    TaskUtils.cancelTaskInterrupt(mGainAlipyActivateResultTask);
                    //finish();
                    return;
                }
            } else {


                WLog.e("Activate", "result = " + result );

                if (ApiRequest.handleResponse(context, result, false)) {
                    stopLoading();
                    isActivatIng = false;
                    Toast.makeText(mContext, "激活成功!", Toast.LENGTH_SHORT).show();
                    WLog.e(TAG, "激活成功");
                    isAlipyRunning = false;
                    countAlipy = 0;

                    //startActivity(new Intent(mContext, LoginActivity.class));
                    finish();
                } else {
                    if (countAlipy < ALIPY_MAX_COUNT) {
                        isAlipyRunning = true;
                        mHandler.sendEmptyMessageDelayed(ALIPY_ORDER_CHECK, AppConfig.AD_TIME);
                    } else {
                        stopLoading();
                        isActivatIng = false;
                        Toast.makeText(mContext, "激活失败!", Toast.LENGTH_SHORT).show();
                        WLog.e(TAG, "激活失败");
                        TaskUtils.cancelTaskInterrupt(mGainAlipyAgreementPageMessageTask);
                        TaskUtils.cancelTaskInterrupt(mGainAlipyActivateResultTask);
                        //finish();
                    }
                }
            }
        }

    }

//    private void certDownload() {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                requestCert();
//            }
//        }).start();
//
//    }


    class GainWeiXinAgreementPageMessageTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;


        public GainWeiXinAgreementPageMessageTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isActivatIng = true;
            startLoading();
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                WLog.e(TAG,"gainAgreementPageMessage task");
                return api.gainWeiXinAgreementPageMessage(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            isActivatIng = false;

            //Log.i("Log","url="+url);
            if (ApiRequest.handleResponse(context, result)) {

                User object = (User)result.getObject();
                if (object == null) {
                    Toast.makeText(mContext,"获取签约界面报文失败！",Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = object.getRequestMessage();
                //url = URLEncoder.encode(url);
                requestWeixinPage(url);
                //requestWeixinPage("https://api.mch.weixin.qq.com/papay/entrustweb?appid=wx19c034b90bba176f&contract_code=16880000006220171219231544668&contract_display_account=WuHanSubWayAutoPay&mch_id=1494164172&notify_url=http%3A%2F%2Fwimetro.imwork.net%3A12844%2FIAFC_HCE_Account_Manager%2Fiafc%2Fhce%2Faccount%2FhceServerNotifyAction_recWXSignAgreementResult&plan_id=93606&request_serial=20171219231544669379835&sign=5F0D2D7FEE1DC4DD4F7FF1273580034E&timestamp=1513696544&version=1.0");
            } else {
                //Toast.makeText(mContext,"获取签约界面链接失败！",Toast.LENGTH_SHORT).show();
            }
        }

    }

    class GainWeiXinActivateResultTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;


        public GainWeiXinActivateResultTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (countWeiXin == 0)
                startLoading();
                isActivatIng = true;
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                WLog.e(TAG,"gainActivateResult task");
                return api.activateWeiXinUserAction_gainActivateResult(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            if (result == null) {
                if (countWeiXin < WEIXIN_MAX_COUNT) {
                    isWeiXinRunning = true;
                    mHandler.sendEmptyMessageDelayed(WEIXIN_ORDER_CHECK, AppConfig.AD_TIME);
                } else {
                    stopLoading();
                    isActivatIng = false;
                    Toast.makeText(mContext, "激活失败!", Toast.LENGTH_SHORT).show();
                    WLog.e(TAG, "激活失败");
                    TaskUtils.cancelTaskInterrupt(mGainWeiXinAgreementPageMessageTask);
                    TaskUtils.cancelTaskInterrupt(mGainWeiXinActivateResultTask);
                    //finish();
                    return;
                }
            } else {


                WLog.e("Activate", "result = " + result);

                if (ApiRequest.handleResponse(context, result, false)) {
                    stopLoading();
                    isActivatIng = false;
                    Toast.makeText(mContext, "激活成功!", Toast.LENGTH_SHORT).show();
                    WLog.e(TAG, "激活成功");
                    isWeiXinRunning = false;
                    countWeiXin = 0;

                    //startActivity(new Intent(mContext, LoginActivity.class));
                    finish();
                } else {
                    if (countWeiXin < WEIXIN_MAX_COUNT) {
                        isWeiXinRunning = true;
                        mHandler.sendEmptyMessageDelayed(WEIXIN_ORDER_CHECK, AppConfig.AD_TIME);
                    } else {
                        stopLoading();
                        isActivatIng = false;
                        Toast.makeText(mContext, "激活失败!", Toast.LENGTH_SHORT).show();
                        WLog.e(TAG, "激活失败");
                        TaskUtils.cancelTaskInterrupt(mGainWeiXinAgreementPageMessageTask);
                        TaskUtils.cancelTaskInterrupt(mGainWeiXinActivateResultTask);
                        //finish();
                    }
                }
            }
        }
    }


    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Log.i("Log","baseresp.getType ="+ req.getType());
        Toast.makeText(this, "req.getType() = " + req.getType(), Toast.LENGTH_SHORT).show();
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

                break;
            default:
                break;
        }
    }



    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        Log.i("Log","baseresp.getType ="+ resp.getType());
        Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //result = R.string.errcode_unsupported;
                break;
            default:
                //result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }



}
