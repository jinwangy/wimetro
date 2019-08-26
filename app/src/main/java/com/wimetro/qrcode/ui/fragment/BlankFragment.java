package com.wimetro.qrcode.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.utils.WLog;

/**
 * jwyuan on 2018-3-12 10:46.
 */

public class BlankFragment extends Fragment {

    private String TAG = BlankFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        WLog.e(TAG,"onCreateView");
        return view;
    }
}
