package com.wimetro.qrcode.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.InfoUtil;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;
import com.wimetro.qrcode.mode.interfaces.IResult;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * jwyuan on 2017-9-18 09:05.
 */

public class ForgetPwdActivity extends BaseActivity implements IResult {

    @Bind(R.id.message_validate_checkbox)
    CheckBox mMessageValidateCheckbox;

    @Bind(R.id.mail_validate_checkbox)
    CheckBox mMailValidateCheckbox;

    @Bind(R.id.message_validate_tv)
    TextView mMessageValidateText;

    @Bind(R.id.mail_validate_tv)
    TextView mMailValidateText;

    @Bind(R.id.forget_account_et)
    TextView forget_account_et;

    private String phoneNum ;
    private SendEmailPassWordTask mSendEmailPassWordTask;


    @Override
    public String setHeaderTitle() {
        return "找回密码";
    }

    @Override
    protected Object initLayout() {
        return R.layout.forget_pwd;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();

        SpannableString spanText = new SpannableString("通过短信验证");
        spanText.setSpan(new AbsoluteSizeSpan(16, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mMessageValidateText.append(spanText);
        mMessageValidateText.append("\n");

        spanText = new SpannableString("需要您绑定的手机可以接收短信");
        spanText.setSpan(new AbsoluteSizeSpan(12, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mMessageValidateText.append(spanText);

        spanText = new SpannableString("通过绑定的密保邮箱验证");
        spanText.setSpan(new AbsoluteSizeSpan(16, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mMailValidateText.append(spanText);
        mMailValidateText.append("\n");

        spanText = new SpannableString("安全链接将发送到您绑定的邮箱里");
        spanText.setSpan(new AbsoluteSizeSpan(12, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        mMailValidateText.append(spanText);

    }

    @OnClick(R.id.message_validate_rl)
    public void onMessageValidateClick() {
        mMessageValidateCheckbox.setChecked(true);
        mMailValidateCheckbox.setChecked(false);
    }

    @OnClick(R.id.mail_validate_rl)
    public void onMailValidateClick() {
        mMessageValidateCheckbox.setChecked(false);
        mMailValidateCheckbox.setChecked(true);
    }

    @OnClick(R.id.find_btn)
    public void onFindPasswordClick(){
        phoneNum = forget_account_et.getText().toString().trim();
        if (!Validator.judgeNotEmpty(this, phoneNum, R.string.phone_not_empty)) {
            return;
        }
        if (!Validator.isPhoneNumberValid( phoneNum)) {
            Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ForgetPwdActivity.this, CheckActivity.class);
        if(mMessageValidateCheckbox.isChecked()) {
            intent.putExtra("type","message");
            intent.putExtra("phoneNum",phoneNum);
            startActivity(intent);
        } else {
//            mSendEmailPassWordTask = new SendEmailPassWordTask(this);
//            mSendEmailPassWordTask.execute(phoneNum);
            //intent.putExtra("type","mail");
            //startActivity(intent);
            Toast.makeText(this, "目前暂不支持邮箱方式找回密码", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSuccess(String message,String type) {
    }

    @Override
    public void onFailed(String error,String type) {
    }

    class SendEmailPassWordTask extends AsyncTask<String, Integer,  ApiResponse<User> > {

        private Api api;
        private Context context;

        public SendEmailPassWordTask(Context context) {
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
                return api.sendEmailPassWord(context,params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            if (result != null) {
                if (ApiRequest.handleResponse(context, result)) {
                    Toast.makeText(context, "已发送安全链接", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgetPwdActivity.this, CheckActivity.class);

                    intent.putExtra("type","mail");

                    startActivity(intent);

                } else {
                    Toast.makeText(context, result.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
