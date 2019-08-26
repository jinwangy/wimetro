package com.wimetro.qrcode.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * wangjianfeng on 2017-9-26 12:28.
 */

@Entity
public class Station {
    @Id(autoincrement = true)
    private Long id;

    private String hce_id;
    private String info_type;
    private String deal_device_code;
    private String deal_seq_group_no;
    private String deal_seq_no;
    private String deal_station;
    private String deal_type;
    private String main_type;
    private String sub_type;
    private String area_code;
    private String sam_code;
    private String logical_code;
    private String read_count;
    private String deal_amount;

    private String balance;
    private String deal_time;
    private String last_deal_dev_code;
    private String last_deal_sq_no;
    private String last_deal_amount;
    private String last_deal_time;
    private String tac;
    private String degrade_mode;
    private String in_gate_station;
    private String in_gate_dev;
    private String in_gate_time;
    private String pay_type;
    private String pay_card_no;
    private String destination_station;
    private String deal_cause;
    private String deal_total_amount;
    private String deposit;
    private String deal_fee;
    private String expiry_date;

    private String last_expiry_date;
    private String oper_id;
    private String work_sq_no;

    private String info_id;
    private String is_report;
    private String phoneType;

    private String para_1;
    private String para_2;
    private String para_3;
    @Generated(hash = 1866506350)
    public Station(Long id, String hce_id, String info_type,
            String deal_device_code, String deal_seq_group_no, String deal_seq_no,
            String deal_station, String deal_type, String main_type,
            String sub_type, String area_code, String sam_code, String logical_code,
            String read_count, String deal_amount, String balance, String deal_time,
            String last_deal_dev_code, String last_deal_sq_no,
            String last_deal_amount, String last_deal_time, String tac,
            String degrade_mode, String in_gate_station, String in_gate_dev,
            String in_gate_time, String pay_type, String pay_card_no,
            String destination_station, String deal_cause, String deal_total_amount,
            String deposit, String deal_fee, String expiry_date,
            String last_expiry_date, String oper_id, String work_sq_no,
            String info_id, String is_report, String phoneType, String para_1,
            String para_2, String para_3) {
        this.id = id;
        this.hce_id = hce_id;
        this.info_type = info_type;
        this.deal_device_code = deal_device_code;
        this.deal_seq_group_no = deal_seq_group_no;
        this.deal_seq_no = deal_seq_no;
        this.deal_station = deal_station;
        this.deal_type = deal_type;
        this.main_type = main_type;
        this.sub_type = sub_type;
        this.area_code = area_code;
        this.sam_code = sam_code;
        this.logical_code = logical_code;
        this.read_count = read_count;
        this.deal_amount = deal_amount;
        this.balance = balance;
        this.deal_time = deal_time;
        this.last_deal_dev_code = last_deal_dev_code;
        this.last_deal_sq_no = last_deal_sq_no;
        this.last_deal_amount = last_deal_amount;
        this.last_deal_time = last_deal_time;
        this.tac = tac;
        this.degrade_mode = degrade_mode;
        this.in_gate_station = in_gate_station;
        this.in_gate_dev = in_gate_dev;
        this.in_gate_time = in_gate_time;
        this.pay_type = pay_type;
        this.pay_card_no = pay_card_no;
        this.destination_station = destination_station;
        this.deal_cause = deal_cause;
        this.deal_total_amount = deal_total_amount;
        this.deposit = deposit;
        this.deal_fee = deal_fee;
        this.expiry_date = expiry_date;
        this.last_expiry_date = last_expiry_date;
        this.oper_id = oper_id;
        this.work_sq_no = work_sq_no;
        this.info_id = info_id;
        this.is_report = is_report;
        this.phoneType = phoneType;
        this.para_1 = para_1;
        this.para_2 = para_2;
        this.para_3 = para_3;
    }
    @Generated(hash = 833410198)
    public Station() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getHce_id() {
        return this.hce_id;
    }
    public void setHce_id(String hce_id) {
        this.hce_id = hce_id;
    }
    public String getInfo_type() {
        return this.info_type;
    }
    public void setInfo_type(String info_type) {
        this.info_type = info_type;
    }
    public String getDeal_device_code() {
        return this.deal_device_code;
    }
    public void setDeal_device_code(String deal_device_code) {
        this.deal_device_code = deal_device_code;
    }
    public String getDeal_seq_group_no() {
        return this.deal_seq_group_no;
    }
    public void setDeal_seq_group_no(String deal_seq_group_no) {
        this.deal_seq_group_no = deal_seq_group_no;
    }
    public String getDeal_seq_no() {
        return this.deal_seq_no;
    }
    public void setDeal_seq_no(String deal_seq_no) {
        this.deal_seq_no = deal_seq_no;
    }
    public String getDeal_station() {
        return this.deal_station;
    }
    public void setDeal_station(String deal_station) {
        this.deal_station = deal_station;
    }
    public String getDeal_type() {
        return this.deal_type;
    }
    public void setDeal_type(String deal_type) {
        this.deal_type = deal_type;
    }
    public String getMain_type() {
        return this.main_type;
    }
    public void setMain_type(String main_type) {
        this.main_type = main_type;
    }
    public String getSub_type() {
        return this.sub_type;
    }
    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }
    public String getArea_code() {
        return this.area_code;
    }
    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }
    public String getSam_code() {
        return this.sam_code;
    }
    public void setSam_code(String sam_code) {
        this.sam_code = sam_code;
    }
    public String getLogical_code() {
        return this.logical_code;
    }
    public void setLogical_code(String logical_code) {
        this.logical_code = logical_code;
    }
    public String getRead_count() {
        return this.read_count;
    }
    public void setRead_count(String read_count) {
        this.read_count = read_count;
    }
    public String getDeal_amount() {
        return this.deal_amount;
    }
    public void setDeal_amount(String deal_amount) {
        this.deal_amount = deal_amount;
    }
    public String getBalance() {
        return this.balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getDeal_time() {
        return this.deal_time;
    }
    public void setDeal_time(String deal_time) {
        this.deal_time = deal_time;
    }
    public String getLast_deal_dev_code() {
        return this.last_deal_dev_code;
    }
    public void setLast_deal_dev_code(String last_deal_dev_code) {
        this.last_deal_dev_code = last_deal_dev_code;
    }
    public String getLast_deal_sq_no() {
        return this.last_deal_sq_no;
    }
    public void setLast_deal_sq_no(String last_deal_sq_no) {
        this.last_deal_sq_no = last_deal_sq_no;
    }
    public String getLast_deal_amount() {
        return this.last_deal_amount;
    }
    public void setLast_deal_amount(String last_deal_amount) {
        this.last_deal_amount = last_deal_amount;
    }
    public String getLast_deal_time() {
        return this.last_deal_time;
    }
    public void setLast_deal_time(String last_deal_time) {
        this.last_deal_time = last_deal_time;
    }
    public String getTac() {
        return this.tac;
    }
    public void setTac(String tac) {
        this.tac = tac;
    }
    public String getDegrade_mode() {
        return this.degrade_mode;
    }
    public void setDegrade_mode(String degrade_mode) {
        this.degrade_mode = degrade_mode;
    }
    public String getIn_gate_station() {
        return this.in_gate_station;
    }
    public void setIn_gate_station(String in_gate_station) {
        this.in_gate_station = in_gate_station;
    }
    public String getIn_gate_dev() {
        return this.in_gate_dev;
    }
    public void setIn_gate_dev(String in_gate_dev) {
        this.in_gate_dev = in_gate_dev;
    }
    public String getIn_gate_time() {
        return this.in_gate_time;
    }
    public void setIn_gate_time(String in_gate_time) {
        this.in_gate_time = in_gate_time;
    }
    public String getPay_type() {
        return this.pay_type;
    }
    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }
    public String getPay_card_no() {
        return this.pay_card_no;
    }
    public void setPay_card_no(String pay_card_no) {
        this.pay_card_no = pay_card_no;
    }
    public String getDestination_station() {
        return this.destination_station;
    }
    public void setDestination_station(String destination_station) {
        this.destination_station = destination_station;
    }
    public String getDeal_cause() {
        return this.deal_cause;
    }
    public void setDeal_cause(String deal_cause) {
        this.deal_cause = deal_cause;
    }
    public String getDeal_total_amount() {
        return this.deal_total_amount;
    }
    public void setDeal_total_amount(String deal_total_amount) {
        this.deal_total_amount = deal_total_amount;
    }
    public String getDeposit() {
        return this.deposit;
    }
    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }
    public String getDeal_fee() {
        return this.deal_fee;
    }
    public void setDeal_fee(String deal_fee) {
        this.deal_fee = deal_fee;
    }
    public String getExpiry_date() {
        return this.expiry_date;
    }
    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }
    public String getLast_expiry_date() {
        return this.last_expiry_date;
    }
    public void setLast_expiry_date(String last_expiry_date) {
        this.last_expiry_date = last_expiry_date;
    }
    public String getOper_id() {
        return this.oper_id;
    }
    public void setOper_id(String oper_id) {
        this.oper_id = oper_id;
    }
    public String getWork_sq_no() {
        return this.work_sq_no;
    }
    public void setWork_sq_no(String work_sq_no) {
        this.work_sq_no = work_sq_no;
    }
    public String getInfo_id() {
        return this.info_id;
    }
    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }
    public String getIs_report() {
        return this.is_report;
    }
    public void setIs_report(String is_report) {
        this.is_report = is_report;
    }
    public String getPhoneType() {
        return this.phoneType;
    }
    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
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

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", hce_id='" + hce_id + '\'' +
                ", info_type='" + info_type + '\'' +
                ", deal_device_code='" + deal_device_code + '\'' +
                ", deal_seq_group_no='" + deal_seq_group_no + '\'' +
                ", deal_seq_no='" + deal_seq_no + '\'' +
                ", deal_station='" + deal_station + '\'' +
                ", deal_type='" + deal_type + '\'' +
                ", main_type='" + main_type + '\'' +
                ", sub_type='" + sub_type + '\'' +
                ", area_code='" + area_code + '\'' +
                ", sam_code='" + sam_code + '\'' +
                ", logical_code='" + logical_code + '\'' +
                ", read_count='" + read_count + '\'' +
                ", deal_amount='" + deal_amount + '\'' +
                ", balance='" + balance + '\'' +
                ", deal_time='" + deal_time + '\'' +
                ", last_deal_dev_code='" + last_deal_dev_code + '\'' +
                ", last_deal_sq_no='" + last_deal_sq_no + '\'' +
                ", last_deal_amount='" + last_deal_amount + '\'' +
                ", last_deal_time='" + last_deal_time + '\'' +
                ", tac='" + tac + '\'' +
                ", degrade_mode='" + degrade_mode + '\'' +
                ", in_gate_station='" + in_gate_station + '\'' +
                ", in_gate_dev='" + in_gate_dev + '\'' +
                ", in_gate_time='" + in_gate_time + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", pay_card_no='" + pay_card_no + '\'' +
                ", destination_station='" + destination_station + '\'' +
                ", deal_cause='" + deal_cause + '\'' +
                ", deal_total_amount='" + deal_total_amount + '\'' +
                ", deposit='" + deposit + '\'' +
                ", deal_fee='" + deal_fee + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", last_expiry_date='" + last_expiry_date + '\'' +
                ", oper_id='" + oper_id + '\'' +
                ", work_sq_no='" + work_sq_no + '\'' +
                ", info_id='" + info_id + '\'' +
                ", is_report='" + is_report + '\'' +
                ", phoneType='" + phoneType + '\'' +
                ", para_1='" + para_1 + '\'' +
                ", para_2='" + para_2 + '\'' +
                ", para_3='" + para_3 + '\'' +
                '}';
    }
}
