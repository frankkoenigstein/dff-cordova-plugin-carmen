package com.dff.cordova.plugin.carmen.service;

import android.content.Context;
import android.os.*;
import android.os.Process;
import com.dff.cordova.plugin.carmen.service.ibeacon.estimote.EstimoteBeaconManager;

public class CarmenServiceWorker extends Handler {
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.CarmenServiceWorker";

    public static final String ARG_UUID = "uuid";
    public static final String ARG_IDENTIFIER = "identifier";
    public static final String ARG_MAJOR = "major";
    public static final String ARG_MINOR = "minor";
    public static final String ARG_SCANPERIODMILLIS = "scanPeriodMillis";
    public static final String ARG_WAITTIMEMILLIS = "waitTimeMillis";
    public static final String ARG_PERIOD = "period";
    public static final String ARG_REGION = "region";
    public static final String ARG_REGIONS = "regions";
    public static final String ARG_BEACON = "beacon";
    public static final String ARG_BEACONS = "beacons";

    private Context mContext;
    private AbstractCarmenBeaconManager mCarmenBeaconManager;
    private HandlerThread mHandlerThread;

    public CarmenServiceWorker(Looper looper, Context context) {
        super(looper);
        mContext = context;
        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        mCarmenBeaconManager = new EstimoteBeaconManager(mHandlerThread.getLooper(), mContext);
    }

    @Override
    public void handleMessage(Message msg) {
        mCarmenBeaconManager.sendMessage(Message.obtain(msg));
    }

    public void onDestroy() {
        mCarmenBeaconManager.onDestroy();
    }


    public enum WHAT {
        CONNECT,
        DISCONNECT,
        START,
        STOP,
        START_MONITORING,
        STOP_MONITORING,
        START_RANGING,
        STOP_RANGING,
        SET_FOREGROUND_SCANPERIOD,
        SET_BACKGROUND_SCANPERIOD,
        SET_REGION_EXIT_EXPIRATION,
        REGISTER_CLIENT,
        UNREGISTER_CLIENT,
        SET_REGIONS,
        GET_REGIONS,
        RESULT
    }

    public enum WHAT_EVENT {
        SERVICE_READY,
        ENTERED_REGION,
        EXITED_REGION,
        BEACONS_DISCOVERED,
        SCAN_START,
        SCAN_STOP,
        ERROR,
        BEACON_ERROR
    }
}
