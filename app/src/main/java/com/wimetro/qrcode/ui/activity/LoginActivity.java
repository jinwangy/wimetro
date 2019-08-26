package com.wimetro.qrcode.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.InfoUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.MoblieDao;
import com.wimetro.qrcode.greendao.UserDao;
import com.wimetro.qrcode.greendao.entity.Moblie;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.infosec.mobile.android.cert.InfosecCert;

/**
 * jwyuan on 2017-9-13 14:57.
 */

public class LoginActivity extends BaseActivity implements IResult  {

    private EditText mUserText;
    private EditText mPasswordText;

    private static final String ACTIVITY = "1";
    private static final String UNACTIVITY = "0";
    private String phoneNum,imei,CN,userId,cardNo="";
    private LoginTask mLoginTask;
    private GainMoblieListTask mGainMoblieListTask;
    private UserDao dao;
    private MoblieDao mMoblieDao;
    private User mUser;
    private InfosecCert infosecCert;
    private Context mContext;
    private final String TAG = "LoginActivity";
    private ExecutorService FULL_TASK_EXECUTOR;

    public static boolean isLoginTopActivity = false;


    private   String[] mDeviceModelArray;
    protected Dialog hceDialog;
    private String deviceModel;
    private boolean isClickLogin,isClickReg;

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();

        mUserText = (EditText) findViewById(R.id.loginName_et);
        mPasswordText = (EditText) findViewById(R.id.passwd_et);

        InfoUtil.init(this);
        TelephonyManager telephonemanage = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonemanage.getDeviceId();
        dao = DaoManager.getInstance().getSession().getUserDao();
        mMoblieDao = DaoManager.getInstance().getSession().getMoblieDao();
        mContext = LoginActivity.this;
        //mTokenManager = new TokenManager(this);

        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();
        deviceModel = Utils.getPhoneModel();

    }

    @Override
    public void onSuccess(String message, String type) {}

    @Override
    public void onFailed(String error, String type) {}

    public static void startThisAct(Context mContext)	{
        Intent intent=new Intent(mContext,LoginActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public String setHeaderTitle() {
        String v_name = "";
        if(WLog.isDebug) {
            v_name = "(测试版)";
        }
        return "用户登录" + v_name;
    }

    @Override
    protected Object initLayout() {
        return R.layout.login;
    }

    @OnClick(R.id.login_btn)
    public void onLoginClick() {
        isClickLogin = true;
        isClickReg = false;


        if (matchMoblieType(false)) {
            login();
        } else {
            gainMoblieList();
        }

    }

    private void login() {
        String user = mUserText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();
        if (!Validator.judgeNotEmpty( LoginActivity.this, user, R.string.user_not_empty)) {
            return;
        }

        if (!Validator.judgeNotEmpty( LoginActivity.this, password, R.string.password_not_empty)) {
            return;
        }

        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this.mContext, "网络连接不可用,无法登录!", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskUtils.cancelTaskInterrupt(mLoginTask);
        mLoginTask = new LoginTask(LoginActivity.this);
        mLoginTask.executeOnExecutor(FULL_TASK_EXECUTOR,user, user, password,Utils.getCurrent(),InfoUtil.getInfo());
    }


    @Override
    protected void excuteOnResume() {
        super.excuteOnResume();
        isClickLogin = false;
        isClickReg = false;
        gainMoblieList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WLog.e("Log","onPause");
        TaskUtils.cancelTaskInterrupt(mLoginTask);
        TaskUtils.cancelTaskInterrupt(mGainMoblieListTask);
    }

   @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hceDialog != null && hceDialog.isShowing())
            hceDialog.dismiss();
    }

    @OnClick(R.id.go_register_tv)
    public void onGoRegisterClick() {
        isClickLogin = false;
        isClickReg = true;
        if (matchMoblieType(false)) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else {
            gainMoblieList();
        }


    }

    @OnClick(R.id.pwd_forgot_tv)
    public void onPwdForgotClick() {
        startActivity(new Intent(this, ForgetPwdActivity.class));
    }


    private void gainMoblieList() {
        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this.mContext, "网络连接不可用,无法获取手机适配机型列表信息!", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskUtils.cancelTaskInterrupt(mGainMoblieListTask);
        mGainMoblieListTask = new GainMoblieListTask(this);
        mGainMoblieListTask.executeOnExecutor(FULL_TASK_EXECUTOR);
    }

    private boolean matchMoblieType(boolean isShow) {
        List<Moblie> mlist = mMoblieDao.loadAll();
        if (mlist != null && !mlist.isEmpty()) {
            mDeviceModelArray = new String[mlist.size()];
            for (int i = 0; i < mlist.size(); i++) {
                mDeviceModelArray[i] = mlist.get(i).getMobile_code();

            }

            if (!Utils.matchPhoneModel(mDeviceModelArray, deviceModel)) {
                if (isShow)
                    showAlertDialog("提示", "暂时不支持您的手机型号！系统将持续升级，支持更多手机型号，敬请关注!");
                return false;
            } else {
                return true;
            }
        } else {
            //Toast.makeText(mContext, "获取手机适配机型失败！", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }



    private void showAlertDialog(String title,String updateMsg) {
        hceDialog = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog_Alert).setTitle(title).setMessage(updateMsg).setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create();

        hceDialog.setCanceledOnTouchOutside(false);
        hceDialog.setCancelable(false);
        if(!hceDialog.isShowing()) {
            hceDialog.show();
        }
    }

    class LoginTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;


        public LoginTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WLog.e("Log","onPreExecute");
            startLoading();
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {

            try {
                WLog.e(TAG,"login task");
                return api.login(context,params[0] ,params[1],params[2],
                        imei,params[3],params[4]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            WLog.e("Log","onPostExecute,result  = " + result);

            if (result == null)
                return;
            mUser = result.getObject();

            if (ApiRequest.handleResponse(context, result)) {
                List<User> users = dao.loadAll();
                dao.deleteAll();
                dao.insert(mUser);

                DeviceUtil.saveLocal(context,mUser);
                startActivity(new Intent(mContext, HomePageActivity.class));
                finish();
            } else {
                Integer mCode = result.getCode();
                if(mUser != null && mCode != null && mCode.intValue() == 12) {
                    if(!TextUtils.isEmpty(mUser.getHce_id())) {
                        WLog.e(TAG,"startThisAct");
                        ActivateActivity.startThisAct(context, mUser.getHce_id());
                    }
                }
            }
        }

    }

    class GainMoblieListTask extends AsyncTask<String ,Integer, ApiResponse<Moblie>> {

        private Api api;
        private Context context;


        public GainMoblieListTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WLog.e("Log","onPreExecute");
            startLoading();
        }


        @Override
        protected ApiResponse<Moblie> doInBackground(String... params) {
            try {
                WLog.e(TAG,"GainMoblieListTask");

                return api.gainHCEMobileModelList(context);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Moblie> result) {
            super.onPostExecute(result);
            stopLoading();
            if (result == null)
                return;
            List<Moblie> mlist = result.getList();
            if (ApiRequest.handleResponse(context, result)) {
                if (mlist != null && !mlist.isEmpty()) {
                    mMoblieDao.deleteAll();
                    for(int i = 0;i < mlist.size();i++) {
                        mMoblieDao.insert(mlist.get(i));
                    }
                }

                if (matchMoblieType(true)) {
                    if (isClickLogin)
                        login();

                    if (isClickReg)
                        startActivity(new Intent(context, RegisterActivity.class));

                }
            }
        }

    }


}
