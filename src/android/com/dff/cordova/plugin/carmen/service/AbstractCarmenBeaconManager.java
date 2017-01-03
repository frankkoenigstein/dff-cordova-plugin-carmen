package com.dff.cordova.plugin.carmen.service;

import android.content.Context;
import android.os.*;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.classes.BeaconRegion;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class AbstractCarmenBeaconManager extends Handler {
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
        CarmenServiceWorker.WHAT msgWhat = CarmenServiceWorker.WHAT.values()[msg.what];

        // CordovaPluginLog.d(TAG, "handle msg " + msg.what + " " + msgWhat.name());

        switch (msgWhat) {
            case REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            case SET_FOREGROUND_SCANPERIOD: {
                long scanPeriodMillis = msg.getData().getLong(CarmenServiceWorker.ARG_SCANPERIODMILLIS);
                long waitTimeMillis = msg.getData().getLong(CarmenServiceWorker.ARG_WAITTIMEMILLIS);
                setForegroundScanPeriod(scanPeriodMillis, waitTimeMillis);

                break;
            }
            case SET_BACKGROUND_SCANPERIOD: {
                long scanPeriodMillis = msg.getData().getLong(CarmenServiceWorker.ARG_SCANPERIODMILLIS);
                long waitTimeMillis = msg.getData().getLong(CarmenServiceWorker.ARG_WAITTIMEMILLIS);
                setBackgroundScanPeriod(scanPeriodMillis, waitTimeMillis);
                break;
            }
            case SET_REGION_EXIT_EXPIRATION: {
                long period = msg.getData().getLong(CarmenServiceWorker.ARG_PERIOD);
                setRegionExitExpiration(period);
            }
            case START_MONITORING:
            case STOP_MONITORING:
            case START_RANGING:
            case STOP_RANGING: {
                try {
                    int majminDefault = -1;
                    UUID uuid = UUID.fromString(msg.getData().getString(CarmenServiceWorker.ARG_UUID));
                    String identifier = msg.getData().getString(CarmenServiceWorker.ARG_IDENTIFIER);
                    int majorInt = msg.getData().getInt(CarmenServiceWorker.ARG_MAJOR, majminDefault);
                    int minorInt = msg.getData().getInt(CarmenServiceWorker.ARG_MINOR, majminDefault);
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
                    Message errMsg = Message.obtain(null, CarmenServiceWorker.WHAT_EVENT.ERROR.ordinal(), e.getMessage());
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
        Log.d(TAG, CarmenServiceWorker.WHAT_EVENT.SERVICE_READY.name());
        notifyClients(Message.obtain(null, CarmenServiceWorker.WHAT_EVENT.SERVICE_READY.ordinal()));
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

                for (BeaconRegion br : mRegions.values()) {
                    br.setEntered(false);
                }
            } catch (IOException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            }
        } else {
            CordovaPluginLog.w(TAG, "cannot read " + mRegionsFile.getAbsolutePath());
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
}
