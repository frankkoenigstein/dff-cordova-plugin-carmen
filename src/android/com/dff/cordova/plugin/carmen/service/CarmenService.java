package com.dff.cordova.plugin.carmen.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.*;
import android.os.Process;
import android.util.Log;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenService extends Service {
    private static final String TAG = "CarmenService";
    protected CarmenServiceWorker mCarmenServiceWorker;
    protected HandlerThread mHandlerThread;
    protected Messenger mServiceMessenger;
    int counter = 0;


    @Override
    public void onCreate() {
        super.onCreate();

        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mCarmenServiceWorker = new CarmenServiceWorker(mHandlerThread.getLooper());
        mServiceMessenger = new Messenger(mCarmenServiceWorker);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mCarmenServiceWorker.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"counter = " + counter++);
                mCarmenServiceWorker.postDelayed(this,5000);
            }
        },5000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mServiceMessenger != null) {
            return mServiceMessenger.getBinder();
        }

        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
