package com.wimetro.qrcode.mode;

import com.wimetro.qrcode.mode.interfaces.IData;
import com.wimetro.qrcode.mode.interfaces.IMode;
import com.wimetro.qrcode.mode.interfaces.IResult;

/**
 * jwyuan on 2017-9-26 09:31.
 */

public class Presenter {

    private final IResult view;
    private final IMode mode;

    public Presenter(IResult view,String type) {
        this.view = view;
        this.mode = buildMode(type);
    }

    public void excute(IData data) {
        if(this.mode == null) return;
        this.mode.onCreate();
        this.mode.excute(this.view,data);
    }

    public void onStop() {
        if(this.mode == null) return;
        this.mode.onStop();
    }

    private IMode buildMode(String type) {
        if(type.equals("login")) {
            //return new LoginMode();
            return null;
        } else if(type.equals("register")) {
            //return new RegisterMode();
            return null;
        } else if(type.equals("forgetpwd")) {
            //return new ForgetPwdMode();
            return null;
        } else if(type.equals("activate")) {
            //return new ActivateMode();
            return null;
        } else if(type.equals("sendrandomcode")) {
            //return new SendRandomCodeMode();
            return null;
        } else if(type.equals("downloadcarddata")) {
            return new DownloadCardDataMode();
        } else if(type.equals("uploadcarddata")) {
            return new UploadCardDataMode();
        } else if(type.equals("reportstationdata")) {
            return new ReportStationDataMode();
        } else if(type.equals("getstationdata")) {
            return new GetStationDataMode();
        } else if(type.equals("checkversion")) {
            return new CheckVersionMode();
        } else if(type.equals("reportofflinestationdata")) {
            return ReportOfflineStationDataMode.getInstance();
        } else if(type.equals("request_cert")) {
            return new RequestCertMode();
        } else if(type.equals("silent_login")) {
            return new SilentLoginMode();
        } else {
            return null;
        }
    }

}
