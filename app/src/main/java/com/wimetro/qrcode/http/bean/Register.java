package com.wimetro.qrcode.http.bean;

import java.io.Serializable;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * jwyuan on 2017/5/17 09:49.
 */

public class Register implements Serializable {
    private static final long serialVersionUID = 1L;
    private long _id;

    @JSONField(name = "moduleCode")
    private String moduleCode;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "rtMessage")
    private String rtMessage;

    @JSONField(name = "sign")
    private String sign;

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRtMessage() {
        return rtMessage;
    }

    public void setRtMessage(String rtMessage) {
        this.rtMessage = rtMessage;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getContent() {
        return "Register : [moduleCode = " + moduleCode + ",status = " + status
                + ",rtMessage = " + rtMessage + ",sign = " + sign + "]";
    }
}
