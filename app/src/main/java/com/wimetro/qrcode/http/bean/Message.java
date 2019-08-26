package com.wimetro.qrcode.http.bean;

/**
 * jwyuan on 2017/4/17 16:15.
 */

import com.alibaba.fastjson.annotation.JSONField;

public class Message {
    @JSONField(name = "message")
    private String message;

    @JSONField(name = "result")
    private String result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String property) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
