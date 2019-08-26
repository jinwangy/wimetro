package com.wimetro.qrcode.module.versionchecklib.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GetVersionService extends AVersionService {
    public GetVersionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {

    }
}
