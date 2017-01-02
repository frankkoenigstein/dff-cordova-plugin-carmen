package com.dff.cordova.plugin.carmen.service;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.ibeacon.estimote.EstimoteBeaconManager;

public class CarmenService extends Service {
    private static final String TAG = "CarmenService";

    protected AbstractCarmenBeaconManager mCarmenBeaconManager;
    protected HandlerThread mHandlerThread;
    protected Messenger mServiceMessenger;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mCarmenBeaconManager = new EstimoteBeaconManager(mHandlerThread.getLooper(), getApplicationContext());
        mServiceMessenger = new Messenger(mCarmenBeaconManager);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        /*
        int counter = 0;

        mCarmenBeaconManager.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"counter = " + counter++);
                mCarmenBeaconManager.postDelayed(this,5000);
            }
        },5000);
        */

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCarmenBeaconManager.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mServiceMessenger != null) {
            return mServiceMessenger.getBinder();
        }

        return null;
    }
}
