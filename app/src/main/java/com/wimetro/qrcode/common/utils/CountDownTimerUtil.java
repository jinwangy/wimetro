package com.wimetro.qrcode.common.utils;

import android.os.CountDownTimer;

/**
 * jwyuan on 2017-9-20 14:08.
 */

public class CountDownTimerUtil extends CountDownTimer {

    private CountListener countListener;

    public CountDownTimerUtil(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        countListener.onTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        countListener.onFinish();
    }

    public void setCountListener(CountListener countListener){
        this.countListener = countListener;
    }
    public static interface CountListener{
        public void onTick(Long time);
        public void onFinish();
    }
}
