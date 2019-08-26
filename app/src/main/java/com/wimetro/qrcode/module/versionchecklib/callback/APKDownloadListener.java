package com.wimetro.qrcode.module.versionchecklib.callback;

import java.io.File;

public interface APKDownloadListener {
    void onDownloading(int progress);
    void onDownloadSuccess(File file);
    void onDownloadFail();
}
