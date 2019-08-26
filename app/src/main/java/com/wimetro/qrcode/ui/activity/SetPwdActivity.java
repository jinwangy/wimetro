package com.wimetro.qrcode.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * jwyuan on 2017-10-11 10:37.
 */

public class SetPwdActivity extends BaseActivity {

    @Bind(R.id.pwd_set_et)
    EditText mPwdSetText;

    @Bind(R.id.confirm_pwd_set_et)
    EditText mConfirmPwdSetText;

    @Bind(R.id.pwd_set_submit_btn)
    Button mPwdSetSubmitBtn;

    @Bind(R.id.pwd_set_ll)
    LinearLayout mPwdSetLl;

    @Bind(R.id.pwd_set_ok_ll)
    LinearLayout mPwdSetOkLl;

    private String phoneNum;
    private String checkCode;

    private SetPwdTask mSetPwdTaskTask;


    @Override
    public String setHeaderTitle() {
        return "设置密码";
    }

    @Override
    protected Object initLayout() {
        return R.layout.set_pwd;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        checkCode = getIntent().getExtras().getString("checkCode", "");
        phoneNum = getIntent().getExtras().getString("phoneNum", "");
    }


    @OnClick(R.id.pwd_set_submit_btn)
    public void onPwdSetSubmitClick() {

        if (!Validator.judgeNotEmpty( this, mPwdSetText.getText().toString(), R.string.password_not_empty)) {
            return;
        }

        if (!Validator.judgeNotEmpty( this, mConfirmPwdSetText.getText().toString(), R.string.comfirm_password_not_empty)) {
            return;
        }

        if(mPwdSetText.getText().toString().length() < 4) {
            Toast.makeText(this, "密码长度需至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mConfirmPwdSetText.getText().toString().length() < 4) {
            Toast.makeText(this, "确认密码长度需至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mPwdSetText.getText().toString().equals(mConfirmPwdSetText.getText().toString())) {
            Toast.makeText(this, "密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskUtils.cancelTaskInterrupt(mSetPwdTaskTask);
        mSetPwdTaskTask = new SetPwdTask(this);
        mSetPwdTaskTask.execute(phoneNum,checkCode,mConfirmPwdSetText.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskUtils.cancelTaskInterrupt(mSetPwdTaskTask );
    }

    @OnClick(R.id.login_in_btn)
    public void onLoginInClick() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    class SetPwdTask extends AsyncTask<String, Integer,  ApiResponse<Void> > {

        private Api api;
        private Context context;

        public SetPwdTask(Context context) {
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
                return api.findPassWord(context,params[0],params[1],params[2]);
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
                    Toast.makeText(context, "新密码设置成功", Toast.LENGTH_SHORT).show();

                    mPwdSetSubmitBtn.setVisibility(View.GONE);
                    mPwdSetLl.setVisibility(View.GONE);
                    mPwdSetOkLl.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
