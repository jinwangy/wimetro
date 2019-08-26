package com.wimetro.qrcode.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * jwyuan on 2017-9-26 12:28.
 */

@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;

    private String app_user;
    private String name;
    private String tele_no;
    private String hce_id;
    private String email;
    private String sex;
    private String activate_type;
    private String idcard;

    private String login_flag;
    private String card_flag;
    private String voucher_id;

    private String user_id;
    private String alipay_user_id;
    private String flag;

    private String requestMessage;
    private String ca_flag;
    private String result;

    private String agreement_no;
    private String channel_type;

    @Transient
    public String nopay_amount;

    @Transient
    public String nopay_num;

    private String para_1;
    private String para_2;
    private String para_3;
    private String para_4;
    private String para_5;
    private String para_6;
    @Generated(hash = 27221406)
    public User(Long id, String app_user, String name, String tele_no,
            String hce_id, String email, String sex, String activate_type,
            String idcard, String login_flag, String card_flag, String voucher_id,
            String user_id, String alipay_user_id, String flag,
            String requestMessage, String ca_flag, String result,
            String agreement_no, String channel_type, String para_1, String para_2,
            String para_3, String para_4, String para_5, String para_6) {
        this.id = id;
        this.app_user = app_user;
        this.name = name;
        this.tele_no = tele_no;
        this.hce_id = hce_id;
        this.email = email;
        this.sex = sex;
        this.activate_type = activate_type;
        this.idcard = idcard;
        this.login_flag = login_flag;
        this.card_flag = card_flag;
        this.voucher_id = voucher_id;
        this.user_id = user_id;
        this.alipay_user_id = alipay_user_id;
        this.flag = flag;
        this.requestMessage = requestMessage;
        this.ca_flag = ca_flag;
        this.result = result;
        this.agreement_no = agreement_no;
        this.channel_type = channel_type;
        this.para_1 = para_1;
        this.para_2 = para_2;
        this.para_3 = para_3;
        this.para_4 = para_4;
        this.para_5 = para_5;
        this.para_6 = para_6;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getApp_user() {
        return this.app_user;
    }
    public void setApp_user(String app_user) {
        this.app_user = app_user;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTele_no() {
        return this.tele_no;
    }
    public void setTele_no(String tele_no) {
        this.tele_no = tele_no;
    }
    public String getHce_id() {
        return this.hce_id;
    }
    public void setHce_id(String hce_id) {
        this.hce_id = hce_id;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getActivate_type() {
        return this.activate_type;
    }
    public void setActivate_type(String activate_type) {
        this.activate_type = activate_type;
    }
    public String getIdcard() {
        return this.idcard;
    }
    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
    public String getLogin_flag() {
        return this.login_flag;
    }
    public void setLogin_flag(String login_flag) {
        this.login_flag = login_flag;
    }
    public String getCard_flag() {
        return this.card_flag;
    }
    public void setCard_flag(String card_flag) {
        this.card_flag = card_flag;
    }
    public String getVoucher_id() {
        return this.voucher_id;
    }
    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }
    public String getUser_id() {
        return this.user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getAlipay_user_id() {
        return this.alipay_user_id;
    }
    public void setAlipay_user_id(String alipay_user_id) {
        this.alipay_user_id = alipay_user_id;
    }
    public String getFlag() {
        return this.flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getRequestMessage() {
        return this.requestMessage;
    }
    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
    public String getCa_flag() {
        return this.ca_flag;
    }
    public void setCa_flag(String ca_flag) {
        this.ca_flag = ca_flag;
    }
    public String getResult() {
        return this.result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getAgreement_no() {
        return this.agreement_no;
    }
    public void setAgreement_no(String agreement_no) {
        this.agreement_no = agreement_no;
    }
    public String getChannel_type() {
        return this.channel_type;
    }
    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }
    public String getPara_1() {
        return this.para_1;
    }
    public void setPara_1(String para_1) {
        this.para_1 = para_1;
    }
    public String getPara_2() {
        return this.para_2;
    }
    public void setPara_2(String para_2) {
        this.para_2 = para_2;
    }
    public String getPara_3() {
        return this.para_3;
    }
    public void setPara_3(String para_3) {
        this.para_3 = para_3;
    }
    public String getPara_4() {
        return this.para_4;
    }
    public void setPara_4(String para_4) {
        this.para_4 = para_4;
    }
    public String getPara_5() {
        return this.para_5;
    }
    public void setPara_5(String para_5) {
        this.para_5 = para_5;
    }
    public String getPara_6() {
        return this.para_6;
    }
    public void setPara_6(String para_6) {
        this.para_6 = para_6;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", app_user='" + app_user + '\'' +
                ", name='" + name + '\'' +
                ", tele_no='" + tele_no + '\'' +
                ", hce_id='" + hce_id + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", activate_type='" + activate_type + '\'' +
                ", idcard='" + idcard + '\'' +
                ", login_flag='" + login_flag + '\'' +
                ", card_flag='" + card_flag + '\'' +
                ", voucher_id='" + voucher_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", alipay_user_id='" + alipay_user_id + '\'' +
                ", flag='" + flag + '\'' +
                ", requestMessage='" + requestMessage + '\'' +
                ", ca_flag='" + ca_flag + '\'' +
                ", result='" + result + '\'' +
                ", agreement_no='" + agreement_no + '\'' +
                ", channel_type='" + channel_type + '\'' +
                ", nopay_amount='" + nopay_amount + '\'' +
                ", nopay_num='" + nopay_num + '\'' +
                ", para_1='" + para_1 + '\'' +
                ", para_2='" + para_2 + '\'' +
                ", para_3='" + para_3 + '\'' +
                ", para_4='" + para_4 + '\'' +
                ", para_5='" + para_5 + '\'' +
                ", para_6='" + para_6 + '\'' +
                '}';
    }
}
