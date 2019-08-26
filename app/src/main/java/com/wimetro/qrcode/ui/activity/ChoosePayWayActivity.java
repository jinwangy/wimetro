package com.wimetro.qrcode.ui.activity;

import android.widget.CheckBox;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseActivity;
import com.wimetro.qrcode.common.utils.DeviceUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * jwyuan on 2018-3-14 16:47.
 */

public class ChoosePayWayActivity extends BaseActivity {

    @Bind(R.id.choose_qrcode_checkbox)
    CheckBox mChooseQrcodeCheckbox;

    @Bind(R.id.choose_nfc_checkbox)
    CheckBox mChooseNfcCheckbox;

    @Override
    public String setHeaderTitle() {
        return "乘车方式";
    }

    @Override
    protected Object initLayout() {
        return R.layout.pay_way;
    }

    @Override
    public void excuteOnCreate() {
        super.excuteOnCreate();
        String way = DeviceUtil.getRideWay(this);
        if(way.equals("qrcode")) {
            mChooseQrcodeCheckbox.setChecked(true);
            mChooseNfcCheckbox.setChecked(false);
        } else {
            mChooseQrcodeCheckbox.setChecked(false);
            mChooseNfcCheckbox.setChecked(true);
        }
    }

    @OnClick(R.id.choose_qrcode_rl)
    public void onQrcodeClick() {
        mChooseQrcodeCheckbox.setChecked(true);
        mChooseNfcCheckbox.setChecked(false);
        DeviceUtil.setRideWay(this,"qrcode");
    }

    @OnClick(R.id.choose_nfc_rl)
    public void onNfcClick() {
        mChooseQrcodeCheckbox.setChecked(false);
        mChooseNfcCheckbox.setChecked(true);
        DeviceUtil.setRideWay(this,"nfc");
    }
}
