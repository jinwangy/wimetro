package com.wimetro.qrcode.http;

import android.content.Context;

public class ApiFactory {

    public static Api getApi(Context context) {
        return ApiClient.getInstance(context);
    }
    
}
