package com.wimetro.qrcode.module.versionchecklib.utils;

import android.util.Log;
import com.wimetro.qrcode.module.versionchecklib.core.AllenChecker;

public class ALog {
    public static void e(String msg) {
        if (AllenChecker.isDebug()) {
            if (msg != null && !msg.isEmpty())
                Log.e("Allen Checker", msg);
        }
    }
}
