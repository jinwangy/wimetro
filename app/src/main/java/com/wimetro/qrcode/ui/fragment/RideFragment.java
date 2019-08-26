package com.wimetro.qrcode.ui.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseFragment;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.WLog;

/**
 * jwyuan on 2018-3-9 12:58.
 */

public class RideFragment extends BaseFragment {

    private String TAG = RideFragment.class.getSimpleName();

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_ride;
    }

    @Override
    protected void initData(View view) {}

    @Override
    protected void onInvisible() {}

    @Override
    public void onVisible() {
        super.onVisible();
        if(mRootView == null) return;
        String way = DeviceUtil.getRideWay(mContext);
        RelativeLayout qrcode_ll = (RelativeLayout)mRootView.findViewById(R.id.qrcode_view_ll);
        RelativeLayout nfc_ll = (RelativeLayout)mRootView.findViewById(R.id.nfc_view_ll);
        TextView rideway_tv = (TextView) mRootView.findViewById(R.id.rideway_info);
        if(way.equals("qrcode")) {
            qrcode_ll.setVisibility(View.VISIBLE);
            nfc_ll.setVisibility(View.GONE);
            rideway_tv.setText("二维码过闸");
        } else {
            qrcode_ll.setVisibility(View.GONE);
            nfc_ll.setVisibility(View.VISIBLE);
            rideway_tv.setText("NFC过闸");
        }
    }
}
