package com.wimetro.qrcode.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by jfwang on 2018-1-15.
 */
@Entity
public class Moblie {

    @Id(autoincrement = true)
    private Long id;

    private String mobile_type;
    private String mobile_code;
    private String mobile_name;
    @Generated(hash = 1205001131)
    public Moblie(Long id, String mobile_type, String mobile_code,
            String mobile_name) {
        this.id = id;
        this.mobile_type = mobile_type;
        this.mobile_code = mobile_code;
        this.mobile_name = mobile_name;
    }
    @Generated(hash = 1352505709)
    public Moblie() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMobile_type() {
        return this.mobile_type;
    }
    public void setMobile_type(String mobile_type) {
        this.mobile_type = mobile_type;
    }
    public String getMobile_code() {
        return this.mobile_code;
    }
    public void setMobile_code(String mobile_code) {
        this.mobile_code = mobile_code;
    }
    public String getMobile_name() {
        return this.mobile_name;
    }
    public void setMobile_name(String mobile_name) {
        this.mobile_name = mobile_name;
    }



}
