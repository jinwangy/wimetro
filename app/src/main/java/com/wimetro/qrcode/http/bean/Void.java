package com.wimetro.qrcode.http.bean;

/**
 * jwyuan on 2017/4/17 16:15.
 */

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 单结果集，并且只有一个属性
 *
 * @author Administrator
 *
 */
public class Void {
    @JSONField(name = "Property")
    private String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
