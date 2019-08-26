package com.wimetro.qrcode.common.core;

import android.app.Dialog;

/**
 * 支持刷新的页面
 * 
 * @author lds
 * 
 */
public interface LoadingInterface {

    void startLoading();

    void startLoading(int text);

    void stopLoading();

    Dialog getLoadingDialog();

}
