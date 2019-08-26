package com.wimetro.qrcode.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.otech.yoda.utils.TaskUtils;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.InfoUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.Validator;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.Api;
import com.wimetro.qrcode.http.ApiClient;
import com.wimetro.qrcode.http.ApiFactory;
import com.wimetro.qrcode.http.ApiRequest;
import com.wimetro.qrcode.http.ApiResponse;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.OnClick;

import static cn.com.infosec.volley.VolleyLog.TAG;

/**
 * jwyuan on 2017-12-23 14:27.
 */

public class JieYueActivity extends BaseActivity {

    @Bind(R.id.xyh_tv)
    TextView mXyhText;

    @Bind(R.id.jyqd_tv)
    TextView mQyqdText;

    @Bind(R.id.pwd_et)
    EditText pwd_et;

    @Bind(R.id.submit_btn)
    Button submit_btn;



    private GainResultTask mGainResultTask;
    private CancelAgreeMentTask mCancelAgreeMentTask;
    private ExecutorService FULL_TASK_EXECUTOR;

    private final String TAG = JieYueActivity.class.getSimpleName();
    private String channal_type,activateType;

    private static final String UNAGREEGMENT = "0";
    private static final String AGREEGMENT = "1";
    private static final String INREVIEW = "2";
    private static final int LOGOUT = 1;


    @Override
    public String setHeaderTitle() {
        return "解约说明";
    }

    @Override
    protected Object initLayout() {
        return R.layout.jieyue;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        FULL_TASK_EXECUTOR = (ExecutorService) ApiClient.createPool();

        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this,"网络连接不可用,无法获取签约协议号和签约渠道", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskUtils.cancelTaskInterrupt(mGainResultTask);
        mGainResultTask = new GainResultTask(this);
        mGainResultTask.executeOnExecutor(FULL_TASK_EXECUTOR, DeviceUtil.getTeleNo(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        TaskUtils.cancelTaskInterrupt(mGainResultTask);
    }


    class GainResultTask extends AsyncTask<String, Integer, ApiResponse<User>> {

        private Api api;
        private Context context;


        public GainResultTask(Context context) {
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
                return api.gainActivateResultByAppUser(context,params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            WLog.e(TAG,"result  = " + result);

            if (ApiRequest.handleResponse(context, result,false)) {
                User mUser = result.getObject();
                if(mUser != null) {
                    if(!TextUtils.isEmpty(mUser.getActivate_type())) {
                        activateType = mUser.getActivate_type();
                        if (UNAGREEGMENT.equals(activateType)) {
                            submit_btn.setText("已解约");
                        } else if (AGREEGMENT.equals(activateType)) {
                            submit_btn.setText("解约");
                        } else if (INREVIEW.equals(activateType)) {
                            submit_btn.setText("审核中");
                        }


                        if(!mUser.getActivate_type().equals("1")) {
                            return;
                        }
                    } else {
                        submit_btn.setText("解约");
                    }
                    channal_type = mUser.getChannel_type();
                    if(!TextUtils.isEmpty(mUser.getAgreement_no())) {
                        mXyhText.setText(mUser.getAgreement_no());
                    }
                    if(!TextUtils.isEmpty(mUser.getChannel_type())) {
                        if(mUser.getChannel_type().equals("APMP")) {
                            mQyqdText.setText("支付宝");
                        } else if(mUser.getChannel_type().equals("WX")) {
                            mQyqdText.setText("微信");
                        }else if(mUser.getChannel_type().equals("ICBC")) {
                            mQyqdText.setText(mUser.getChannel_type());
                        }
                    }
                }
            } else {
                submit_btn.setText("解约");
            }
        }

    }


    class CancelAgreeMentTask extends AsyncTask<String, Integer, ApiResponse<User>> {

        private Api api;
        private Context context;


        public CancelAgreeMentTask(Context context) {
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

                return api.cancelAlipyWeiXinAgreementStatus(context,params[0],params[1],params[2]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(ApiResponse<User> result) {
            super.onPostExecute(result);
            stopLoading();
            WLog.e(TAG,"result  = " + result);

            if (ApiRequest.handleResponse(context, result,true)) {
                User mUser = result.getObject();
                if(mUser != null) {
                    if (!TextUtils.isEmpty(mUser.getActivate_type())) {
                        activateType = mUser.getActivate_type();
                        if (UNAGREEGMENT.equals(activateType)) {
                            submit_btn.setText("已解约");
                            Toast.makeText(JieYueActivity.this, "解约成功！", Toast.LENGTH_LONG).show();
                            setResult(LOGOUT);
                            finish();
                        } else if (AGREEGMENT.equals(activateType)) {
                            submit_btn.setText("解约");
                        } else if (INREVIEW.equals(activateType)) {
                            submit_btn.setText("审核中");
                            Toast.makeText(JieYueActivity.this, "解约成功！", Toast.LENGTH_LONG).show();
                            setResult(LOGOUT);
                            finish();
                        }
                    }
                }
            } else {
                submit_btn.setText("解约");
            }
        }

    }



    @OnClick(R.id.submit_btn)
    public void onSubmitClick() {
        int size = DeviceUtil.getOffDataCount();
        WLog.e(TAG,"offdata_count = " + size);
        if(size > 0) {
            Toast.makeText(this, "本地存在脱机数据,无法解约", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this,"网络连接不可用,无法解约", Toast.LENGTH_SHORT).show();
            return;
        }
        showTokenDialog(this);
    }

    private void setAgreegment(String password) {

        if (!Validator.judgeNotEmpty( this, password, R.string.password_not_empty)) {
            return;
        }
        if (activateType != null && activateType.equals("1")) {
            TaskUtils.cancelTaskInterrupt(mCancelAgreeMentTask);
            mCancelAgreeMentTask = new CancelAgreeMentTask(this);
            if (channal_type != null && !"".equals(channal_type)) {
                Log.i("Log", "DeviceUtil.getHceId(this)=" + DeviceUtil.getHceId(this) + ",password=" + password + ",channal_type=" + channal_type);
                mCancelAgreeMentTask.executeOnExecutor(FULL_TASK_EXECUTOR, DeviceUtil.getHceId(this), password, channal_type);
            } else {
                Toast.makeText(this, "获取渠道失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "未签约！", Toast.LENGTH_SHORT).show();
        }
    }

    public void showTokenDialog(final Context mContext) {
        final EditText input = new EditText(mContext);
        input.setHint("请输入密码（登录密码）");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        Dialog alertDialog=new AlertDialog.Builder(mContext,android.R.style.Theme_Material_Light_Dialog_Alert)
                .setMessage("解约").setView(input).setNegativeButton("取消", null)
                .setPositiveButton("确定",new  DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setAgreegment(input.getText().toString().trim());

                    }

                }).create();


        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                return false;
            }




        });


        alertDialog.show();

    }



}
