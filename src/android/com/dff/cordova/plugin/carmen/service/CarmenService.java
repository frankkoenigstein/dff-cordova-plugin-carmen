package com.dff.cordova.plugin.carmen.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.ibeacon.estimote.EstimoteBeaconManager;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenService extends Service {
    private static final String TAG = "CarmenService";
    protected AbstractCarmenBeaconManager mCarmenServiceWorker;
    protected HandlerThread mHandlerThread;
    protected Messenger mServiceMessenger;

    @Override
    public void onCreate() {
        super.onCreate();

        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mCarmenServiceWorker = new EstimoteBeaconManager(mHandlerThread.getLooper(), getApplicationContext());
        mServiceMessenger = new Messenger(mCarmenServiceWorker);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        /*
        int counter = 0;

        mCarmenServiceWorker.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"counter = " + counter++);
                mCarmenServiceWorker.postDelayed(this,5000);
            }
        },5000);
        */

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mServiceMessenger != null) {
            return mServiceMessenger.getBinder();
        }

        return null;
    }
}
