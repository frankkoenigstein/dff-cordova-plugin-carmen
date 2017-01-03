package com.dff.cordova.plugin.carmen.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
import com.dff.cordova.plugin.carmen.service.event.CarmenEventServiceWorker;

public class CarmenService extends Service {
    private static final String TAG = "CarmenService";

    private CarmenServiceWorker mCarmenServiceWorker;
    private Messenger mServiceMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        HandlerThread handlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        mCarmenServiceWorker = new CarmenServiceWorker(handlerThread.getLooper(), getApplicationContext());
        mServiceMessenger = new Messenger(mCarmenServiceWorker);

        // event service
        CarmenEventServiceWorker carmenEventServiceWorker = new CarmenEventServiceWorker(handlerThread.getLooper(), getApplicationContext());
        // connect handler
        Message eventClientMsg = Message.obtain(mCarmenServiceWorker, WHAT.REGISTER_CLIENT.ordinal());
        eventClientMsg.replyTo = new Messenger(carmenEventServiceWorker);
        eventClientMsg.sendToTarget();
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

        mCarmenServiceWorker.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mServiceMessenger != null) {
            return mServiceMessenger.getBinder();
        }

        return null;
    }
}
