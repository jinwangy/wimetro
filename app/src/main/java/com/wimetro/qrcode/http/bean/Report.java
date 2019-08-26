package com.wimetro.qrcode.http.bean;

import java.io.Serializable;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * jwyuan on 2017-11-14 11:49.
 */

public class Report implements Serializable {

    private static final long serialVersionUID = 1L;
    private long _id;

    @JSONField(name = "info_id")
    private String info_id;

    public String getInfo_id() {
        return info_id;
    }

    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }

}
