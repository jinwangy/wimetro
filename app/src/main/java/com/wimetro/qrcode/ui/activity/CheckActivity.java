package com.wimetro.qrcode.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.CountDownTimerUtil;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;

import java.io.IOException;

/**
 * jwyuan on 2017-10-11 10:33.
 */

public class CheckActivity extends BaseActivity {

    private TextView mPhoneNumberShowText;
    private TextView mMailShowText;
    private EditText mCheckCodeInputText;

    private Button mMessageNextBtn;
    private Button mMailNextBtn;
    private Button mCheckCodeGetBtn;

    private String phoneNum;
    private String checkCode;
    private CountDownTimerUtil timeCount;
    private SendRandomCodeTask mSendRandomCodeTask;
    private ValidatePhoneTask mValidatePhoneTask;

    public static void startThisAct(Context mContext, String userName)	{
        Intent intent=new Intent(mContext,CheckActivity.class);
        intent.putExtra("phoneNum",userName);
        mContext.startActivity(intent);
    }


    @Override
    public String setHeaderTitle() {
        return "找回密码";
    }

    @Override
    protected Object initLayout() {
        return R.layout.check;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        String type = getIntent().getExtras().getString("type","");
        phoneNum = getIntent().getExtras().getString("phoneNum","");
        if(type.equals("message")) {
            View view = ((ViewStub)findViewById(R.id.message_view_stub)).inflate();
            mPhoneNumberShowText = (TextView) view.findViewById(R.id.phoneNumber_show_tv);
            mCheckCodeInputText = (EditText)view.findViewById(R.id.checkCode_input_et);
            mCheckCodeGetBtn = (Button) view.findViewById(R.id.checkCode_get_btn);
            mMessageNextBtn = (Button) view.findViewById(R.id.message_next_btn);

            mPhoneNumberShowText.setText(phoneNum);

            mCheckCodeGetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!Validator.judgeNotEmpty( CheckActivity.this, phoneNum, R.string.phone_not_empty)) {
                        return;
                    }

                    if (!Validator.isPhoneNumberValid( phoneNum)) {

                        Toast.makeText(CheckActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    timeCount.start();

                    TaskUtils.cancelTaskInterrupt(mSendRandomCodeTask);
                    mSendRandomCodeTask = new CheckActivity.SendRandomCodeTask(CheckActivity.this);
                    mSendRandomCodeTask.execute(phoneNum);
                }
            });

            mMessageNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCode = mCheckCodeInputText.getText().toString();
                    if (!Validator.judgeNotEmpty( CheckActivity.this, checkCode, R.string.checkcode_not_empty)) {
                        return;
                    }

                    if(checkCode.length() < 4) {
                        Toast.makeText(CheckActivity.this, "短信验证码不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TaskUtils.cancelTaskInterrupt(mValidatePhoneTask);
                    mValidatePhoneTask = new CheckActivity.ValidatePhoneTask(CheckActivity.this);
                    mValidatePhoneTask.execute(phoneNum,checkCode);
                }
            });

        } else if(type.equals("mail")){
            View view = ((ViewStub)findViewById(R.id.mail_view_stub)).inflate();
            mMailShowText = (TextView) view.findViewById(R.id.mail_show_tv);
            mMailNextBtn = (Button) view.findViewById(R.id.mail_next_btn);

            mMailNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CheckActivity.this, LoginActivity.class);
                    intent.putExtra("phoneNum",phoneNum);
                    startActivity(intent);
                    finish();
                }
            });

        }


        timeCount = new CountDownTimerUtil(60000,1000);
        timeCount.setCountListener(new CountDownTimerUtil.CountListener() {
            @Override
            public void onTick(Long time) {
                if(mCheckCodeGetBtn == null) return;
                mCheckCodeGetBtn.setText(String.valueOf(time/1000).concat("秒后重新发送"));
                mCheckCodeGetBtn.setEnabled(false);//设置不可点击
            }
            @Override
            public void onFinish() {
                if(mCheckCodeGetBtn == null) return;
                mCheckCodeGetBtn.setText("重新获取验证码");
                mCheckCodeGetBtn.setEnabled(true);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskUtils.cancelTaskInterrupt(mSendRandomCodeTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeCount != null) {
            timeCount.cancel();
        }
    }

    class SendRandomCodeTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {

        private Api api;
        private Context context;

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
                    if(mCheckCodeGetBtn != null) {
                        mCheckCodeGetBtn.setText("重新获取验证码");
                        mCheckCodeGetBtn.setEnabled(true);
                    }
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    class ValidatePhoneTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {

        private Api api;
        private Context context;

        public ValidatePhoneTask(Context context) {
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
                return api.verifySMSCode(context,params[0],params[1]);
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
                    Toast.makeText(context, "手机号验证通过", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CheckActivity.this, SetPwdActivity.class);
                    intent.putExtra("phoneNum",phoneNum);
                    intent.putExtra("checkCode",checkCode);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
