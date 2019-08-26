package com.wimetro.qrcode.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.greendao.UserDao;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.OnClick;
import cn.com.infosec.mobile.android.cert.InfosecCert;
import cn.com.infosec.mobile.android.result.Result;


public class SettingsActivity extends BaseActivity  {

    private UserDao dao;
    private String userId;
    private ExecutorService FULL_TASK_EXECUTOR;
    private LoginOutTask mLoginOutTask;
    private static final int LOGOUT = 1;

    @Bind(R.id.version_name_tv)
    TextView mVersionNameText;

    //private SharedPreferences mShardPreferences;
    //private SharedPreferences.Editor mEditor;

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();

        String v_name = "";
        if(WLog.isDebug) {
            v_name = "(测试版)";
        }

        mVersionNameText.setText(Utils.getVersionName(this) + v_name);
        //mShardPreferences = getSharedPreferences("logicCardNo", Context.MODE_PRIVATE);
        //mEditor= mShardPreferences.edit();
        dao = DaoManager.getInstance().getSession().getUserDao();
        queryData(dao);
        TaskUtils.cancelTaskInterrupt(mLoginOutTask);
        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();

    }

    public static void startThisAct(Context mContext)	{
        Intent intent=new Intent(mContext,SettingsActivity.class);
        mContext.startActivity(intent);
    }


    @Override
    public String setHeaderTitle() {
        return "设置";
    }

    @Override
    protected Object initLayout() {
        return R.layout.setting;
    }

    @OnClick(R.id.set_rl)
    public void onJieYueClick() {
        Intent intent=new Intent(this,JieYueActivity.class);
        startActivityForResult(intent,LOGOUT);
    }

    @OnClick(R.id.login_out_btn)
    public void onLoginClick() {
        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, "网络连接不可用,无法退出当前用户!", Toast.LENGTH_SHORT).show();
            return;
        }

        int size = DeviceUtil.getOffDataCount();
        WLog.e("SettingsActivity","offdata_count = " + size);
        if(size > 0) {
            Toast.makeText(this, "本地存在脱机数据,请先联网后等待片刻再退出!", Toast.LENGTH_SHORT).show();
            return;
        }

        mLoginOutTask = new LoginOutTask(this);
        mLoginOutTask.executeOnExecutor(FULL_TASK_EXECUTOR, DeviceUtil.getAppUser(this));
    }

    @OnClick(R.id.payway_rl)
    public void onChoosePayWayClick() {
        Intent intent=new Intent(this,ChoosePayWayActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskUtils.cancelTaskInterrupt(mLoginOutTask);
    }

    private void queryData(UserDao dao) {
        List<User> users = dao.loadAll();
        userId = "";
        int maxSize = users.size();
        if (maxSize > 0)
            userId += users.get(maxSize - 1).getUser_id();
        WLog.i("Log","userId="+userId);
    }

    class LoginOutTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {

        private Api api;
        private Context context;


        public LoginOutTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WLog.i("Log","onPreExecute");
            startLoading();
        }

        @Override
        protected ApiResponse<Void> doInBackground(String... params) {

            try {
                return api.logout(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Void> result) {
            super.onPostExecute(result);
            stopLoading();
            WLog.i("Log","onPostExecute ");

            if (result == null)
                return;

            if (ApiRequest.handleResponse(context, result)) {
                DeviceUtil.clearLocal(context);
                //ApiClient.deletePool();
                dao.deleteAll();
                //stopTokenService();
                setResult(LOGOUT);
                finish();

            } else {
                Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }

        private void stopTokenService() {
            cancelAlarmColock();
            //mEditor.clear();
            //mEditor.commit();
            Intent intent = new Intent();
            intent.setAction("android.intent.action.TokenService");
            intent.setPackage(getPackageName());
            stopService(intent);
        }

        private void cancelAlarmColock() {
            //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
            Intent intent = new Intent("android.intent.action.TokenService");
            intent.putExtra("msg","下载令牌！");
            //intent.putExtra("alarmTime",);

            //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
            //也就是发送了action 为"ELITOR_CLOCK"的intent
            PendingIntent pi = PendingIntent.getService(SettingsActivity.this,0,intent,0);

            //AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

            am.cancel(pi);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case LOGOUT:
                mLoginOutTask = new LoginOutTask(this);
                mLoginOutTask.executeOnExecutor(FULL_TASK_EXECUTOR, DeviceUtil.getAppUser(this));

                break;
            default:
                break;
        }
    }

}
