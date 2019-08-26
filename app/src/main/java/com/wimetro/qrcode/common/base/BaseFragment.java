package com.wimetro.qrcode.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wimetro.qrcode.common.utils.WLog;

/**
 * jwyuan on 2018-3-12 11:13.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    public Context mContext;
    protected boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;

    public BaseFragment() {}

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            WLog.e(getLogTag(),"setUserVisibleHint,true");
            isVisible = true;
            onVisible();
            lazyLoad();
        } else {
            WLog.e(getLogTag(),"setUserVisibleHint,false");
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        WLog.e(getLogTag(),"onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WLog.e(getLogTag(),"onCreate");
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WLog.e(getLogTag(),"onCreateView");
        if (mRootView == null) {
            WLog.e(getLogTag(),"initView");
            mRootView = inflater.inflate(setContentView(), container, false);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                WLog.e(getLogTag(),"removeView");
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WLog.e(getLogTag(),"onActivityCreated");
        isPrepared = true;
        lazyLoad();
    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        WLog.e(getLogTag(),"....................initData....................");
        initData(mRootView);
        isFirst = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        WLog.e(getLogTag(),"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        WLog.e(getLogTag(),"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        WLog.e(getLogTag(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        WLog.e(getLogTag(),"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WLog.e(getLogTag(),"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WLog.e(getLogTag(),"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        WLog.e(getLogTag(),"onDetach");
    }

    protected abstract String getLogTag();

    protected abstract int setContentView();

    protected abstract void initData(View view);

    protected void onVisible(){};

    protected abstract void onInvisible();
}
