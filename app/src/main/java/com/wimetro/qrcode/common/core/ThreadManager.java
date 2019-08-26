package com.wimetro.qrcode.common.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.wimetro.qrcode.http.ApiClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * jwyuan on 2017-10-24 13:12.
 */

public class ThreadManager {
    private String TAG = ThreadManager.class.getSimpleName();
    private static final int POOL_SIZE = 4;

    private ExecutorService mFixedThreadPool;
    private ExcuteTaskInterface mExcuteTaskInterface;

    private static  final int EXCUTE_BEFORE = 0;
    private static  final int EXCUTE_AFTER = 1;
    private static  final int EXCUTE_SUCCESS = 2;
    private static  final int EXCUTE_FAILED = 3;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG,"handleMessage = " + msg.what);
            switch(msg.what){
                case EXCUTE_BEFORE:
                    Log.e(TAG,"handleMessage(excuteTaskBefore)");
                    mExcuteTaskInterface.excuteTaskBefore();
                    break;
                case EXCUTE_AFTER:
                    Log.e(TAG,"handleMessage(excuteTaskAfter)");
                    mExcuteTaskInterface.excuteTaskAfter();
                    break;
                case EXCUTE_SUCCESS:
                    Log.e(TAG,"handleMessage(excuteTaskSuccess)");
                    //Bundle bundle = msg.getData();
                    mExcuteTaskInterface.excuteTaskSuccess(msg.getData().getString("",""),msg.getData().getString("",""));
                    break;
                case EXCUTE_FAILED:
                    Log.e(TAG,"handleMessage(excuteTaskFailed)");
                    //Bundle bundle = msg.getData();
                    mExcuteTaskInterface.excuteTaskFailed(msg.getData().getString("",""),msg.getData().getString("",""));
                    break;
                default:
                    break;
            }
        }
    };

    //获取线程管理类的单例
    private static class SingletonHolder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
    private ThreadManager (){
        //mFixedThreadPool = Executors.newFixedThreadPool(POOL_SIZE);
        mFixedThreadPool = ApiClient.createPool();
        Log.e(TAG,"newFixedThreadPool");
    }
    public static final ThreadManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setTaskInterface(ExcuteTaskInterface mExcuteTaskInterface) {
        this.mExcuteTaskInterface = mExcuteTaskInterface;
    }

    public void excute() {
        Log.e(TAG,"excute");
        mFixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"run");
                if(mExcuteTaskInterface == null) return;
                sendMessage(0);
                mExcuteTaskInterface.excuteTaskJob();
                sendMessage(1);
            }
        });
    }

    public void sendMessage(int what) {
        mHandler.sendEmptyMessage(what);
    }

    public void sendMessage(int what,String message,String type) {
        Message msg = Message.obtain();
        msg.what = what;
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("type", type);
        msg.setData(bundle);//bundle传值，耗时，效率低
        mHandler.sendMessage(msg);
    }

    public void shutDown() {

    }
}
