package com.wimetro.qrcode.mode.interfaces;

/**
 * jwyuan on 2017-9-26 09:27.
 */

public interface IResult {

    /**
     * 执行业务成功
     */
    void onSuccess(String message,String type);

    /**
     * 执行业务失败
     *
     * @param error
     */
    void onFailed(String error,String type);


}
