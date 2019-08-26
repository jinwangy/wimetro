package com.wimetro.qrcode.common.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.DaoMaster;
import com.wimetro.qrcode.greendao.DaoSession;

/**
 * jwyuan on 2017-10-20 08:37.
 */

public class DaoManager {

    private final static String TAG = DaoManager.class.getSimpleName();

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //获取GreenDao管理类的单例
    private static class SingletonHolder {
        private static final DaoManager INSTANCE = new DaoManager();
    }
    private DaoManager (){}
    public static final DaoManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context mContext){
        //创建数据库
        DBHelper helper = new DBHelper(mContext);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        mDaoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public class DBHelper extends DaoMaster.OpenHelper {
        private final static String dbName = "wim_qrcode.db";

        public DBHelper(Context context) {
            super(context, dbName, null);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            WLog.e(TAG, "oldVersion = " + oldVersion + ",newVersion = " + newVersion);
            if (oldVersion < newVersion) {

            }
        }
    }


}
