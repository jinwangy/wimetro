package com.wimetro.qrcode.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * wangjianfeng on 2017-9-26 12:28.
 */

@Entity
public class Card {
    @Id(autoincrement = true)
    private Long id;

    private String cardNo;
    private String MF_0005;
    private String ADF1_0015;
    private String ADF1_0016;
    private String ADF1_0017_01;

    private String damk_02;
    private String damk_02_attribute;
    private String dpk_01;
    private String dpk_01_attribute;
    private String dpk_02;

    private String dpk_02_attribute;
    private String dpk_03;
    private String dpk_03_attribute;
    private String dpk_04;
    private String dpk_04_attribute;

    private String dpk_05;
    private String dpk_05_attribute;
    private String dlk_01;
    private String dlk_01_attribute;
    private String dlk_02;

    private String dlk_02_attribute;
    private String dlk_03;
    private String dlk_03_attribute;
    private String dlk_04;
    private String dlk_04_attribute;

    private String dlk_05;
    private String dlk_05_attribute;
    private String dtk;
    private String dtk_attribute;
    private String rul;
    private String create_time;

    private String fee_total;
    private String onlineval;
    private String offlineval;
    @Generated(hash = 1210422997)
    public Card(Long id, String cardNo, String MF_0005, String ADF1_0015,
            String ADF1_0016, String ADF1_0017_01, String damk_02,
            String damk_02_attribute, String dpk_01, String dpk_01_attribute,
            String dpk_02, String dpk_02_attribute, String dpk_03,
            String dpk_03_attribute, String dpk_04, String dpk_04_attribute,
            String dpk_05, String dpk_05_attribute, String dlk_01,
            String dlk_01_attribute, String dlk_02, String dlk_02_attribute,
            String dlk_03, String dlk_03_attribute, String dlk_04,
            String dlk_04_attribute, String dlk_05, String dlk_05_attribute,
            String dtk, String dtk_attribute, String rul, String create_time,
            String fee_total, String onlineval, String offlineval) {
        this.id = id;
        this.cardNo = cardNo;
        this.MF_0005 = MF_0005;
        this.ADF1_0015 = ADF1_0015;
        this.ADF1_0016 = ADF1_0016;
        this.ADF1_0017_01 = ADF1_0017_01;
        this.damk_02 = damk_02;
        this.damk_02_attribute = damk_02_attribute;
        this.dpk_01 = dpk_01;
        this.dpk_01_attribute = dpk_01_attribute;
        this.dpk_02 = dpk_02;
        this.dpk_02_attribute = dpk_02_attribute;
        this.dpk_03 = dpk_03;
        this.dpk_03_attribute = dpk_03_attribute;
        this.dpk_04 = dpk_04;
        this.dpk_04_attribute = dpk_04_attribute;
        this.dpk_05 = dpk_05;
        this.dpk_05_attribute = dpk_05_attribute;
        this.dlk_01 = dlk_01;
        this.dlk_01_attribute = dlk_01_attribute;
        this.dlk_02 = dlk_02;
        this.dlk_02_attribute = dlk_02_attribute;
        this.dlk_03 = dlk_03;
        this.dlk_03_attribute = dlk_03_attribute;
        this.dlk_04 = dlk_04;
        this.dlk_04_attribute = dlk_04_attribute;
        this.dlk_05 = dlk_05;
        this.dlk_05_attribute = dlk_05_attribute;
        this.dtk = dtk;
        this.dtk_attribute = dtk_attribute;
        this.rul = rul;
        this.create_time = create_time;
        this.fee_total = fee_total;
        this.onlineval = onlineval;
        this.offlineval = offlineval;
    }
    @Generated(hash = 52700939)
    public Card() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardNo() {
        return this.cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    public String getMF_0005() {
        return this.MF_0005;
    }
    public void setMF_0005(String MF_0005) {
        this.MF_0005 = MF_0005;
    }
    public String getADF1_0015() {
        return this.ADF1_0015;
    }
    public void setADF1_0015(String ADF1_0015) {
        this.ADF1_0015 = ADF1_0015;
    }
    public String getADF1_0016() {
        return this.ADF1_0016;
    }
    public void setADF1_0016(String ADF1_0016) {
        this.ADF1_0016 = ADF1_0016;
    }
    public String getADF1_0017_01() {
        return this.ADF1_0017_01;
    }
    public void setADF1_0017_01(String ADF1_0017_01) {
        this.ADF1_0017_01 = ADF1_0017_01;
    }
    public String getDamk_02() {
        return this.damk_02;
    }
    public void setDamk_02(String damk_02) {
        this.damk_02 = damk_02;
    }
    public String getDamk_02_attribute() {
        return this.damk_02_attribute;
    }
    public void setDamk_02_attribute(String damk_02_attribute) {
        this.damk_02_attribute = damk_02_attribute;
    }
    public String getDpk_01() {
        return this.dpk_01;
    }
    public void setDpk_01(String dpk_01) {
        this.dpk_01 = dpk_01;
    }
    public String getDpk_01_attribute() {
        return this.dpk_01_attribute;
    }
    public void setDpk_01_attribute(String dpk_01_attribute) {
        this.dpk_01_attribute = dpk_01_attribute;
    }
    public String getDpk_02() {
        return this.dpk_02;
    }
    public void setDpk_02(String dpk_02) {
        this.dpk_02 = dpk_02;
    }
    public String getDpk_02_attribute() {
        return this.dpk_02_attribute;
    }
    public void setDpk_02_attribute(String dpk_02_attribute) {
        this.dpk_02_attribute = dpk_02_attribute;
    }
    public String getDpk_03() {
        return this.dpk_03;
    }
    public void setDpk_03(String dpk_03) {
        this.dpk_03 = dpk_03;
    }
    public String getDpk_03_attribute() {
        return this.dpk_03_attribute;
    }
    public void setDpk_03_attribute(String dpk_03_attribute) {
        this.dpk_03_attribute = dpk_03_attribute;
    }
    public String getDpk_04() {
        return this.dpk_04;
    }
    public void setDpk_04(String dpk_04) {
        this.dpk_04 = dpk_04;
    }
    public String getDpk_04_attribute() {
        return this.dpk_04_attribute;
    }
    public void setDpk_04_attribute(String dpk_04_attribute) {
        this.dpk_04_attribute = dpk_04_attribute;
    }
    public String getDpk_05() {
        return this.dpk_05;
    }
    public void setDpk_05(String dpk_05) {
        this.dpk_05 = dpk_05;
    }
    public String getDpk_05_attribute() {
        return this.dpk_05_attribute;
    }
    public void setDpk_05_attribute(String dpk_05_attribute) {
        this.dpk_05_attribute = dpk_05_attribute;
    }
    public String getDlk_01() {
        return this.dlk_01;
    }
    public void setDlk_01(String dlk_01) {
        this.dlk_01 = dlk_01;
    }
    public String getDlk_01_attribute() {
        return this.dlk_01_attribute;
    }
    public void setDlk_01_attribute(String dlk_01_attribute) {
        this.dlk_01_attribute = dlk_01_attribute;
    }
    public String getDlk_02() {
        return this.dlk_02;
    }
    public void setDlk_02(String dlk_02) {
        this.dlk_02 = dlk_02;
    }
    public String getDlk_02_attribute() {
        return this.dlk_02_attribute;
    }
    public void setDlk_02_attribute(String dlk_02_attribute) {
        this.dlk_02_attribute = dlk_02_attribute;
    }
    public String getDlk_03() {
        return this.dlk_03;
    }
    public void setDlk_03(String dlk_03) {
        this.dlk_03 = dlk_03;
    }
    public String getDlk_03_attribute() {
        return this.dlk_03_attribute;
    }
    public void setDlk_03_attribute(String dlk_03_attribute) {
        this.dlk_03_attribute = dlk_03_attribute;
    }
    public String getDlk_04() {
        return this.dlk_04;
    }
    public void setDlk_04(String dlk_04) {
        this.dlk_04 = dlk_04;
    }
    public String getDlk_04_attribute() {
        return this.dlk_04_attribute;
    }
    public void setDlk_04_attribute(String dlk_04_attribute) {
        this.dlk_04_attribute = dlk_04_attribute;
    }
    public String getDlk_05() {
        return this.dlk_05;
    }
    public void setDlk_05(String dlk_05) {
        this.dlk_05 = dlk_05;
    }
    public String getDlk_05_attribute() {
        return this.dlk_05_attribute;
    }
    public void setDlk_05_attribute(String dlk_05_attribute) {
        this.dlk_05_attribute = dlk_05_attribute;
    }
    public String getDtk() {
        return this.dtk;
    }
    public void setDtk(String dtk) {
        this.dtk = dtk;
    }
    public String getDtk_attribute() {
        return this.dtk_attribute;
    }
    public void setDtk_attribute(String dtk_attribute) {
        this.dtk_attribute = dtk_attribute;
    }
    public String getRul() {
        return this.rul;
    }
    public void setRul(String rul) {
        this.rul = rul;
    }
    public String getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getFee_total() {
        return this.fee_total;
    }
    public void setFee_total(String fee_total) {
        this.fee_total = fee_total;
    }
    public String getOnlineval() {
        return this.onlineval;
    }
    public void setOnlineval(String onlineval) {
        this.onlineval = onlineval;
    }
    public String getOfflineval() {
        return this.offlineval;
    }
    public void setOfflineval(String offlineval) {
        this.offlineval = offlineval;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNo='" + cardNo + '\'' +
                ", MF_0005='" + MF_0005 + '\'' +
                ", ADF1_0015='" + ADF1_0015 + '\'' +
                ", ADF1_0016='" + ADF1_0016 + '\'' +
                ", ADF1_0017_01='" + ADF1_0017_01 + '\'' +
                ", damk_02='" + damk_02 + '\'' +
                ", damk_02_attribute='" + damk_02_attribute + '\'' +
                ", dpk_01='" + dpk_01 + '\'' +
                ", dpk_01_attribute='" + dpk_01_attribute + '\'' +
                ", dpk_02='" + dpk_02 + '\'' +
                ", dpk_02_attribute='" + dpk_02_attribute + '\'' +
                ", dpk_03='" + dpk_03 + '\'' +
                ", dpk_03_attribute='" + dpk_03_attribute + '\'' +
                ", dpk_04='" + dpk_04 + '\'' +
                ", dpk_04_attribute='" + dpk_04_attribute + '\'' +
                ", dpk_05='" + dpk_05 + '\'' +
                ", dpk_05_attribute='" + dpk_05_attribute + '\'' +
                ", dlk_01='" + dlk_01 + '\'' +
                ", dlk_01_attribute='" + dlk_01_attribute + '\'' +
                ", dlk_02='" + dlk_02 + '\'' +
                ", dlk_02_attribute='" + dlk_02_attribute + '\'' +
                ", dlk_03='" + dlk_03 + '\'' +
                ", dlk_03_attribute='" + dlk_03_attribute + '\'' +
                ", dlk_04='" + dlk_04 + '\'' +
                ", dlk_04_attribute='" + dlk_04_attribute + '\'' +
                ", dlk_05='" + dlk_05 + '\'' +
                ", dlk_05_attribute='" + dlk_05_attribute + '\'' +
                ", dtk='" + dtk + '\'' +
                ", dtk_attribute='" + dtk_attribute + '\'' +
                ", rul='" + rul + '\'' +
                ", create_time='" + create_time + '\'' +
                ", fee_total='" + fee_total + '\'' +
                ", onlineval='" + onlineval + '\'' +
                ", offlineval='" + offlineval + '\'' +
                '}';
    }
}
