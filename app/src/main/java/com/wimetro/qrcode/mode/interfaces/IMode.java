package com.wimetro.qrcode.mode.interfaces;

/**
 * jwyuan on 2017-9-26 09:34.
 */

public interface IMode {
    public void onCreate();
    public void excute(IResult result, IData data);
    public void onStop();
}
