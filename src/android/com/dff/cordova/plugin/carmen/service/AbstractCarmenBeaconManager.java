package com.dff.cordova.plugin.carmen.service;

import android.content.Context;
import android.os.*;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;

import java.util.ArrayList;
import java.util.UUID;

public abstract class AbstractCarmenBeaconManager extends Handler {
    private static final String TAG = "AbstractCarmenBeaconManager";

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
        UNREGISTER_CLIENT
    }

    public enum WHAT_EVENT {
        SERVICE_READY,
        ENTERED_REGION,
        EXITED_REGION,
        BEACONS_DISCOVERED,
        SCAN_START,
        SCAN_STOP,
        ERROR
    }

    public static final String ARG_BEACONS = "beacons";
    public static final String ARG_UUID = "uuid";
    public static final String ARG_IDENTIFIER = "identifier";
    public static final String ARG_MAJOR = "major";
    public static final String ARG_MINOR = "minor";
    public static final String ARG_SCANPERIODMILLIS = "scanPeriodMillis";
    public static final String ARG_WAITTIMEMILLIS = "waitTimeMillis";
    public static final String ARG_PERIOD = "period";

    protected Context mContext;
    protected boolean mBeaconServiceConnected = false;
    private ArrayList<Messenger> clients = new ArrayList<Messenger>();

    public AbstractCarmenBeaconManager(Looper looper, Context context) {
        super(looper);
        mContext = context;
    }

    protected abstract void startMonitoring(String identifier, UUID uuid, Integer major, Integer minor);
    protected abstract void stopMonitoring(String identifier, UUID uuid, Integer major, Integer minor);
    protected abstract void startRanging(String identifier, UUID uuid, Integer major, Integer minor);
    protected abstract void stopRanging(String identifier, UUID uuid, Integer major, Integer minor);
    protected abstract void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);
    protected abstract void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);
    protected abstract void setRegionExitExpiration(long period);

    @Override
    public void handleMessage(Message msg)
    {
        WHAT msgWhat = WHAT.values()[msg.what];
        switch (msgWhat) {
            case SET_FOREGROUND_SCANPERIOD: {
                long scanPeriodMillis = msg.getData().getLong(ARG_SCANPERIODMILLIS);
                long waitTimeMillis = msg.getData().getLong(ARG_WAITTIMEMILLIS);
                setForegroundScanPeriod(scanPeriodMillis, waitTimeMillis);

                break;
            }
            case SET_BACKGROUND_SCANPERIOD: {
                long scanPeriodMillis = msg.getData().getLong(ARG_SCANPERIODMILLIS);
                long waitTimeMillis = msg.getData().getLong(ARG_WAITTIMEMILLIS);
                setBackgroundScanPeriod(scanPeriodMillis, waitTimeMillis);
                break;
            }
            case SET_REGION_EXIT_EXPIRATION: {
                long period = msg.getData().getLong(ARG_PERIOD);
                setRegionExitExpiration(period);
            }
            case START_MONITORING:
            case STOP_MONITORING:
            case START_RANGING:
            case STOP_RANGING: {
                UUID uuid = UUID.fromString(msg.getData().getString(ARG_UUID));
                String identifier = msg.getData().getString(ARG_IDENTIFIER);
                Integer major = msg.getData().getInt(ARG_MAJOR);
                Integer minor = msg.getData().getInt(ARG_MINOR);

                switch (msgWhat) {
                    case START_MONITORING:
                        startMonitoring(identifier, uuid, major, minor);
                        break;
                    case STOP_MONITORING:
                        stopMonitoring(identifier, uuid, major, minor);
                        break;
                    case START_RANGING:
                        startRanging(identifier, uuid, major, minor);
                        break;
                    case STOP_RANGING:
                        stopRanging(identifier, uuid, major, minor);
                        break;
                }

                break;
            }
        }
    }

    protected void notifyClients(Message msg) {
        for (int i = this.clients.size() - 1; i >= 0; i--) {
            try {
                this.clients.get(i).send(Message.obtain(msg));
            }
            catch (RemoteException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
                // The client is dead. Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                this.clients.remove(i);
            }
        }
    }
}
