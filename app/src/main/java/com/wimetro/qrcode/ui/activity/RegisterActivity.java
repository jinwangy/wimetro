package com.wimetro.qrcode.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.android.volley.Response;
import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.base.BaseApplication;
import com.wimetro.qrcode.common.core.DaoManager;
import com.wimetro.qrcode.common.utils.CountDownTimerUtil;
import com.wimetro.qrcode.common.utils.InfoUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.UserDao;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.Presenter;
//import com.wimetro.qrcode.mode.RegisterMode;
import com.wimetro.qrcode.mode.interfaces.IResult;
//import com.wimetro.qrcode.mode.SendRandomCodeMode;
import com.wimetro.qrcode.http.bean.Message;
import com.wimetro.qrcode.greendao.entity.User;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * jwyuan on 2017-9-13 14:58.
 */

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.userName_et)
    EditText mUserNameText;
    @Bind(R.id.register_number_et)
    EditText mRegisterNumberText;
    @Bind(R.id.register_pwd_et)
    EditText mRegisterPwdText;
    @Bind(R.id.confirm_pwd_et)
    EditText mConfirmPwdText;
    @Bind(R.id.checkcode_et)
    EditText mCheckCodeText;
    @Bind(R.id.getCheckCode_btn)
    Button mCheckCodeBtn;

    private CountDownTimerUtil timeCount;

    private String phoneNum,userName;
    private Context mContext;
    RegisterTask mRegisterTask;
    SendRandomCodeTask mSendRandomCodeTask;
    private ExecutorService FULL_TASK_EXECUTOR;
    private String imei;

    @Override
    public String setHeaderTitle() {
        return "注册";
    }

    @Override
    protected Object initLayout() {
        return R.layout.register;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        mContext = RegisterActivity.this;
        TelephonyManager telephonemanage = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonemanage.getDeviceId();
        timeCount = new CountDownTimerUtil(60000,1000);
        timeCount.setCountListener(new CountDownTimerUtil.CountListener() {
            @Override
            public void onTick(Long time) {
                WLog.e("Register","onTick");
                if(mCheckCodeBtn == null) return;
                mCheckCodeBtn.setText(String.valueOf(time/1000).concat("秒后重新发送"));
                mCheckCodeBtn.setEnabled(false);//设置不可点击
            }
            @Override
            public void onFinish() {
                WLog.e("Register","onFinish");
                if(mCheckCodeBtn == null) return;
                mCheckCodeBtn.setText("重新获取验证码");
                mCheckCodeBtn.setEnabled(true);
            }
        });
    }



    @OnClick(R.id.getCheckCode_btn)
    public void onGetCheckCodeClick() {
        phoneNum = mRegisterNumberText.getText().toString().trim();

        if (!Validator.judgeNotEmpty( RegisterActivity.this, phoneNum, R.string.phone_not_empty)) {
            return;
        }

        if (!Validator.isPhoneNumberValid( phoneNum)) {

            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        timeCount.start();

        TaskUtils.cancelTaskInterrupt(mSendRandomCodeTask);
        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();
        mSendRandomCodeTask = new SendRandomCodeTask(this);
        //mSendRandomCodeTask.execute(phoneNum);
        mSendRandomCodeTask.executeOnExecutor(FULL_TASK_EXECUTOR,phoneNum);
    }

    @OnClick(R.id.submit_register_btn)
    public void onSubmitRegisterClick() {
        userName = mUserNameText.getText().toString().trim();

        if (!Validator.judgeNotEmpty( RegisterActivity.this, userName, R.string.user_not_empty)) {
            return;
        }

        if(userName.length() < 4) {
            Toast.makeText(this, "用户名长度需至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        phoneNum = mRegisterNumberText.getText().toString().trim();

        String password = mRegisterPwdText.getText().toString().trim();
        String comfirmPasswd = mConfirmPwdText.getText().toString().trim();
        String code = mCheckCodeText.getText().toString().trim();

        if (!Validator.judgeNotEmpty( RegisterActivity.this, password, R.string.password_not_empty)) {
            return;
        }

        if (!Validator.judgeNotEmpty( RegisterActivity.this, comfirmPasswd, R.string.comfirm_password_not_empty)) {
            return;
        }

        if(password.length() < 4) {
            Toast.makeText(this, "密码长度需至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(comfirmPasswd.length() < 4) {
            Toast.makeText(this, "确认密码长度需至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(comfirmPasswd)) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validator.judgeNotEmpty( RegisterActivity.this, phoneNum, R.string.phone_not_empty)) {
            return;
        }

        if (!Validator.isPhoneNumberValid( phoneNum)) {

            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validator.judgeNotEmpty( RegisterActivity.this, code, R.string.code_not_empty)) {
            return;
        }

        if(code.length() < 4) {
            Toast.makeText(this, "验证码不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this.mContext, "网络连接不可用,无法注册!", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskUtils.cancelTaskInterrupt(mRegisterTask);
        //ExecutorService FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
        mRegisterTask = new RegisterTask(this);
        //mRegisterTask.executeOnExecutor(FULL_TASK_EXECUTOR,phoneNum, password, mail ,code);
        mRegisterTask.execute(userName,phoneNum, password ,"",code);
    }

    @Override
    protected void onPause() {
        super.onPause();
       
        TaskUtils.cancelTaskInterrupt(mSendRandomCodeTask);
        TaskUtils.cancelTaskInterrupt(mRegisterTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeCount != null) {
            timeCount.cancel();
        }
    }

    @OnClick(R.id.account_login_tv)
    public void onAccountLoginClick() {
        this.finish();
    }

    class RegisterTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;
        private final String ANDROID_DEVICE_TYPE="12";
        private final String CHANNEL_TYPE="APP";


        public RegisterTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            startLoading();
        }

        @Override
        protected ApiResponse<User> doInBackground(String... params) {
            try {
                return api.register(context,params[0] ,params[1],params[2],
                        imei,ANDROID_DEVICE_TYPE,params[3],params[4] ,CHANNEL_TYPE);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            UserDao dao = DaoManager.getInstance().getSession().getUserDao();
            if (result == null)
                return;
            User object = result.getObject();

            if (ApiRequest.handleResponse(context, result)) {
                dao.insert(object);
                Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                ActivateActivity.startThisAct(mContext,object.getHce_id());
                finish();
            } else {
                Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    class SendRandomCodeTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {

        private Api api;
        private Context context;
        private final String ANDROID_DEVICE_TYPE="12";
        private final String CHANNEL_TYPE="APP";


        public SendRandomCodeTask(Context context) {
            this.context = context;
            this.api = ApiFactory.getApi(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            startLoading();
        }

        @Override
        protected ApiResponse<Void> doInBackground(String... params) {
            try {
                return api.sendRandomCode(context,params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<Void> result) {
            super.onPostExecute(result);
            stopLoading();
            if (result != null) {
                if (ApiRequest.handleResponse(context, result)) {
                    Toast.makeText(context, "验证已发送", Toast.LENGTH_SHORT).show();
                } else {
                    if(timeCount != null) {
                        timeCount.cancel();
                    }
                    if(mCheckCodeBtn != null) {
                        mCheckCodeBtn.setText("重新获取验证码");
                        mCheckCodeBtn.setEnabled(true);
                    }
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
