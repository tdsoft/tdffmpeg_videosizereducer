package com.tdsolutions.android.tdffmpeg_videosizereducer;

import java.io.File;

public interface OnVideoCompressListener {
    void onStart();
    void onFailed();
    void onFinish();
    void onSuccess(File out);
    void onProgress(int progress);
}
