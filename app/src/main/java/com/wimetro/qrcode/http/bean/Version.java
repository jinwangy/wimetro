package com.wimetro.qrcode.http.bean;

/**
 * jwyuan on 2017-12-15 15:58.
 */
import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;

public class Version implements Serializable {
    private static final long serialVersionUID = 1L;
    private long _id;

    @JSONField(name = "version_url")
    private String version_url;

    @JSONField(name = "version_no")
    private String version_no;

    @JSONField(name = "version_name")
    private String version_name;

    @JSONField(name = "update_flag")
    private String update_flag;

    @JSONField(name = "card_version_no")
    private String card_version_no;

    public String getVersion_url() {
        return version_url;
    }

    public void setVersion_url(String version_url) {
        this.version_url = version_url;
    }

    public String getVersion_no() {
        return version_no;
    }

    public void setVersion_no(String version_no) {
        this.version_no = version_no;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getUpdate_flag() {
        return update_flag;
    }

    public void setUpdate_flag(String update_flag) {
        this.update_flag = update_flag;
    }

    public String getCard_version_no() {
        return card_version_no;
    }

    public void setCard_version_no(String card_version_no) {
        this.card_version_no = card_version_no;
    }

    @Override
    public String toString() {
        return "Version{" +
                "version_url='" + version_url + '\'' +
                ", version_no='" + version_no + '\'' +
                ", version_name='" + version_name + '\'' +
                ", update_flag='" + update_flag + '\'' +
                ", card_version_no='" + card_version_no + '\'' +
                '}';
    }
}
