package com.dff.cordova.plugin.carmen.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenServiceWorker extends Handler {
    private static final String TAG = "CarmenServiceWorker";

    public CarmenServiceWorker(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        CordovaPluginLog.d(TAG, "handle msg " + msg);
    }
}
