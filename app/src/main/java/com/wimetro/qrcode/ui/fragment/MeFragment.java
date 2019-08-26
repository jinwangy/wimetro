package com.wimetro.qrcode.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseFragment;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.WLog;

/**
 * jwyuan on 2018-3-9 13:00.
 */

public class MeFragment extends BaseFragment {

    private String TAG = MeFragment.class.getSimpleName();

    private RelativeLayout qrcode_tab_rl;
    private TextView qrcode_tab_tv;
    private TextView qrcode_tab_line;

    private RelativeLayout nfc_tab_rl;
    private TextView nfc_tab_tv;
    private TextView nfc_tab_line;

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initData(View view) {
        if(view == null) return;
        TextView tv = (TextView)view.findViewById(R.id.me_tele);
        tv.setText(DeviceUtil.get(mContext,"tele_no",""));

        qrcode_tab_rl = (RelativeLayout)view.findViewById(R.id.qrcode_tab_rl);
        qrcode_tab_tv = (TextView)view.findViewById(R.id.qrcode_tab_tv);
        qrcode_tab_line = (TextView)view.findViewById(R.id.qrcode_tab_line);

        nfc_tab_rl = (RelativeLayout)view.findViewById(R.id.nfc_tab_rl);
        nfc_tab_tv = (TextView)view.findViewById(R.id.nfc_tab_tv);
        nfc_tab_line = (TextView)view.findViewById(R.id.nfc_tab_line);

        qrcode_tab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrcode_tab_tv.setTextColor(Color.parseColor("#2199ef"));
                qrcode_tab_line.setBackgroundColor(Color.parseColor("#2199ef"));

                nfc_tab_tv.setTextColor(Color.BLACK);
                nfc_tab_line.setBackgroundColor(Color.parseColor("#d4d4d4"));
            }
        });

        nfc_tab_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nfc_tab_tv.setTextColor(Color.parseColor("#2199ef"));
                nfc_tab_line.setBackgroundColor(Color.parseColor("#2199ef"));

                qrcode_tab_tv.setTextColor(Color.BLACK);
                qrcode_tab_line.setBackgroundColor(Color.parseColor("#d4d4d4"));
            }
        });
    }

    @Override
    protected void onInvisible() {

    }
}
