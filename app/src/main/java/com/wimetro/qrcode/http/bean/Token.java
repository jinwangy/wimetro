package com.wimetro.qrcode.http.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * jwyuan on 2017-12-19 20:06.
 */

public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    private long _id;

    @JSONField(name = "token_id")
    private String token_id;

    @JSONField(name = "user_id")
    private String user_id;

    @JSONField(name = "expires")
    private String expires;

    @JSONField(name = "balLimit")
    private String balLimit;

    @JSONField(name = "onlineTimes")
    private String onlineTimes;

    @JSONField(name = "offlineTimes")
    private String offlineTimes;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getOnlineTimes() {
        return onlineTimes;
    }

    public void setOnlineTimes(String onlineTimes) {
        this.onlineTimes = onlineTimes;
    }

    public String getBalLimit() {
        return balLimit;
    }

    public void setBalLimit(String balLimit) {
        this.balLimit = balLimit;
    }

    public String getOfflineTimes() {
        return offlineTimes;
    }

    public void setOfflineTimes(String offlineTimes) {
        this.offlineTimes = offlineTimes;
    }

    @Override
    public String toString() {
        return "Token{" +
                "_id=" + _id +
                ", token_id='" + token_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", expires='" + expires + '\'' +
                ", balLimit='" + balLimit + '\'' +
                ", onlineTimes='" + onlineTimes + '\'' +
                ", offlineTimes='" + offlineTimes + '\'' +
                '}';
    }
}
