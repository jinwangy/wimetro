package com.wimetro.qrcode.module.versionchecklib.callback;

import java.io.File;

public interface DownloadListener {
    void onCheckerDownloading(int progress);
    void onCheckerDownloadSuccess(File file);
    void onCheckerDownloadFail();
    void onCheckerStartDownload();
}
