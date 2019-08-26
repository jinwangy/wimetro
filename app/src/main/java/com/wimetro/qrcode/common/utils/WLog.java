package com.wimetro.qrcode.common.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * jwyuan on 2017-12-2 10:53.
 */

public class WLog {

    public static final boolean isDebug = true;
    private static final boolean isWriteToLocal = false;

    private static DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static void v(String tag,String message) {
        if(isDebug) {
            Log.v(tag,message);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    public static void i(String tag,String message) {
        if(isDebug) {
            Log.i(tag,message);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    public static void d(String tag,String message) {
        if(isDebug) {
            Log.d(tag,message);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    public static void e(String tag,String message) {
        if(isDebug) {
            Log.e(tag,message);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    public static void e(String tag,String message,Throwable t) {
        if(isDebug) {
            Log.e(tag,message,t);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    public static void w(String tag,String message) {
        if(isDebug) {
            Log.w(tag,message);
        }
        if(isWriteToLocal) {
            writeToLocal(tag,message);
        }
    }

    private static void writeToLocal(String tag,String message) {
        String path = FileUtil.getLogDirectoryPath();
        if(!TextUtils.isEmpty(path) && mkLogdir(path)) {
            String time = formatter.format(new Date());
            String sb = Utils.getCurrentTestTime() + ":(" + tag + ")" + message;

            try {
                FileOutputStream fos = new FileOutputStream(path +  File.separator + time + ".log", true);
                fos.write(sb.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {}
        }
    }

    public static boolean mkLogdir(String dir) {
        boolean success = false;
        File dirPath = null;
        try {
            dirPath = new File(dir);
            if (!dirPath.exists()) dirPath.mkdirs();
            success = true;
        } catch (Exception e) {
            success = false;
        }

        if(dirPath == null || !dirPath.exists() || !dirPath.canRead() || !dirPath.canWrite()) {
            success = false;
        }

        return success;
    }
}
