package com.wimetro.qrcode.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wimetro.qrcode.common.core.LoadingInterface;
import com.wimetro.qrcode.common.utils.ActivityCollector;
import com.wimetro.qrcode.ui.view.MyTopBar;
import com.wimetro.qrcode.ui.view.LoadingDialog;
import butterknife.ButterKnife;
import com.otech.yoda.utils.DialogUtils;

/**
 * jwyuan on 2017/3/14 10:07.
 */

public abstract class BaseActivity extends Activity implements LoadingInterface {

    private MyTopBar mTopBar;
    protected Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutParameter();

        Object obj = this.initLayout();
        if(obj instanceof View) {
            this.setContentView((View)obj);
        } else {
            this.setContentView(((Integer)obj).intValue());
        }

        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);

        initView();
        excuteOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        excuteOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLoading();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(mTopBar != null) {
            mTopBar = null;
        }
        ActivityCollector.removeActivity(this);
        excuteOnDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void setLayoutParameter() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected abstract Object initLayout();
    protected void initView() { }

    protected void excuteOnCreate() {
        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if(!setHeaderTitle().equals("")) {
            mTopBar = new MyTopBar(this);
            mTopBar.setTitleText(setHeaderTitle());
        }
    }

    protected String setHeaderTitle() {
        return "";
    }

    public void setLeftViewVisible(boolean visible) {
        if(mTopBar != null) {
            mTopBar.setLeftViewVisible(false);
        }
    }

    public MyTopBar getTopBar() {
        return mTopBar;
    }

    protected void excuteOnResume() {}

    protected void excuteOnDestroy() {}


    @Override
    public void startLoading() {
        if (!this.isFinishing()) {
            mLoadingDialog = LoadingDialog.show(this);
        }
    }

    @Override
    public void startLoading(int text) {
        if (!this.isFinishing()) {
            mLoadingDialog = LoadingDialog.show(this, text);
        }
    }

    @Override
    public void stopLoading() {
        if (!this.isFinishing()) {
            DialogUtils.dismissDialog(mLoadingDialog);
        }
    }

    @Override
    public Dialog getLoadingDialog() {
        return mLoadingDialog;
    }
}
