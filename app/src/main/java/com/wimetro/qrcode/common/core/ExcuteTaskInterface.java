package com.wimetro.qrcode.common.core;

/**
 * jwyuan on 2017-10-23 16:55.
 */

public interface ExcuteTaskInterface {
    public void excuteTaskBefore();
    public void excuteTaskAfter();
    public void excuteTaskJob();
    public void excuteTaskSuccess(String message, String type);
    public void excuteTaskFailed(String error, String type);
}
