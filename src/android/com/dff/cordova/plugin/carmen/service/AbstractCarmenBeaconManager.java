package com.dff.cordova.plugin.carmen.service;

import android.content.Context;
import android.os.*;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.classes.BeaconRegion;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.sdk.Region;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class AbstractCarmenBeaconManager extends Handler {
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

    private static final String TAG = "AbstractCarmenBeaconManager";

    private static final String REGIONS_FILENAME = "regions.ser";
    protected HashMap<String, BeaconRegion> mRegions = new HashMap<String, BeaconRegion>();
    protected File mRegionsFile;
    protected Context mContext;
    protected boolean mBeaconServiceConnected = false;
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    public AbstractCarmenBeaconManager(Looper looper, Context context) {
        super(looper);
        mContext = context;
        mRegionsFile = new File(context.getFilesDir().getAbsolutePath(), REGIONS_FILENAME);

        restoreRegions();
    }

    protected abstract void startMonitoring(String identifier, UUID uuid, Integer major, Integer minor);

    protected abstract void stopMonitoring(String identifier, UUID uuid, Integer major, Integer minor);

    protected abstract void startRanging(String identifier, UUID uuid, Integer major, Integer minor);

    protected abstract void stopRanging(String identifier, UUID uuid, Integer major, Integer minor);

    protected abstract void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);

    protected abstract void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis);

    protected abstract void setRegionExitExpiration(long period);

    @Override
    public void handleMessage(Message msg) {
        WHAT msgWhat = WHAT.values()[msg.what];

        // CordovaPluginLog.d(TAG, "handle msg " + msg.what + " " + msgWhat.name());

        switch (msgWhat) {
            case REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
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
                try {
                    int majminDefault = -1;
                    UUID uuid = UUID.fromString(msg.getData().getString(ARG_UUID));
                    String identifier = msg.getData().getString(ARG_IDENTIFIER);
                    int majorInt = msg.getData().getInt(ARG_MAJOR, majminDefault);
                    int minorInt = msg.getData().getInt(ARG_MINOR, majminDefault);
                    Integer major = null;
                    Integer minor = null;

                    if (majorInt > majminDefault) {
                        major = majorInt;
                    }

                    if (minorInt > majminDefault) {
                        minor = minorInt;
                    }

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
                } catch (IllegalArgumentException e) {
                    CordovaPluginLog.e(TAG, e.getMessage(), e);
                    Message errMsg = Message.obtain(null, WHAT_EVENT.ERROR.ordinal(), e.getMessage());
                    notifyClients(errMsg);
                }

                break;
            }
            default:
                CordovaPluginLog.w(TAG, "unhandled msg " + msg);
                break;
        }
    }

    protected void notifyClients(Message msg) {
        for (int i = this.mClients.size() - 1; i >= 0; i--) {
            try {
                this.mClients.get(i).send(Message.obtain(msg));
            } catch (RemoteException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
                // The client is dead. Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                this.mClients.remove(i);
            }
        }
    }

    protected void onServiceReady() {
        Log.d(TAG, WHAT_EVENT.SERVICE_READY.name());
        notifyClients(Message.obtain(null, WHAT_EVENT.SERVICE_READY.ordinal()));
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        storeRegions();
    }

    protected void restoreRegions() {
        if (mRegionsFile.canRead()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(mRegionsFile));
                mRegions = (HashMap<String, BeaconRegion>) in.readObject();
                in.close();
            } catch (IOException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            }
        }
        else {
            CordovaPluginLog.w(TAG , "cannot read " + mRegionsFile.getAbsolutePath());
        }
    }

    public void storeRegions() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mRegionsFile));
            out.writeObject(mRegions);
            out.flush();
            out.close();
        } catch (IOException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        }
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
