package com.dff.cordova.plugin.carmen.service;

import android.content.Context;
import android.os.*;
import android.os.Process;
import com.dff.cordova.plugin.carmen.model.BeaconRegion;
import com.dff.cordova.plugin.carmen.service.event.CarmenEventServiceWorker;
import com.dff.cordova.plugin.carmen.service.ibeacon.estimote.EstimoteBeaconManager;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CarmenServiceWorker extends Handler {
    public static final String ARG_UUID = "uuid";
    public static final String ARG_ID = "_id";
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
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_SERVER_URL = "serverUrl";
    public static final String ARG_JWT = "jwt";
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.CarmenServiceWorker";
    private static final String REGIONS_FILENAME = "regions.ser";
    private Context mContext;
    private AbstractCarmenBeaconManager mCarmenBeaconManager;
    private CarmenEventServiceWorker mCarmenEventServiceWorker;
    private HandlerThread mHandlerThread;
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    private HashMap<String, BeaconRegion> mRegions = new HashMap<String, BeaconRegion>();
    private File mRegionsFile;

    public CarmenServiceWorker(Looper looper, Context context) {
        super(looper);
        mContext = context;

        mRegionsFile = new File(context.getFilesDir().getAbsolutePath(), REGIONS_FILENAME);
        restoreRegions();

        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        mCarmenBeaconManager = new EstimoteBeaconManager(mHandlerThread.getLooper(), mContext, this);
        mCarmenEventServiceWorker = new CarmenEventServiceWorker(looper, mContext);
        // connect handler
        mClients.add(new Messenger(mCarmenEventServiceWorker.getClientHandler()));
    }

    public HashMap<String, BeaconRegion> getRegions() {
        return mRegions;
    }

    public BeaconRegion getRegion(String identifier) {
        return mRegions.get(identifier);
    }

    @Override
    public void handleMessage(Message msg) {
        WHAT msgWhat = WHAT.values()[msg.what];


        switch (msgWhat) {
            case REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            case CLEAR_REGIONS:
                clearRegions();
                break;
            case SET_REGIONS:
                clearRegions();

                ArrayList<BeaconRegion> regions = msg.getData().getParcelableArrayList(CarmenServiceWorker.ARG_REGIONS);
                for (BeaconRegion region : regions) {
                    mRegions.put(region.getIdentifier(), region);
                }

                startMonitoring();
                storeRegions();

                break;
            case GET_REGIONS:
                Message msgResult = Message.obtain(null, WHAT.RESULT.ordinal(), msg.what, 0);
                msgResult.getData().putParcelableArrayList(CarmenServiceWorker.ARG_REGIONS, new ArrayList<Parcelable>(mRegions.values()));

                try {
                    msg.replyTo.send(msgResult);
                } catch (RemoteException e) {
                    CordovaPluginLog.e(TAG, e.getMessage(), e);
                }

                break;
            default:
                mCarmenBeaconManager.sendMessage(Message.obtain(msg));
                mCarmenEventServiceWorker.sendMessage(Message.obtain(msg));
                break;
        }
    }

    public void onDestroy() {
        storeRegions();
    }

    public void notifyClients(Message msg) {
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

    public void startMonitoring() {
        for (BeaconRegion region : mRegions.values()) {
            mCarmenBeaconManager.startMonitoring(region.getIdentifier(), UUID.fromString(region.getUuid()), region.getMajor(), region.getMinor());
        }
    }

    public void stopMonitoring() {
        for (BeaconRegion region : mRegions.values()) {
            mCarmenBeaconManager.stopMonitoring(region.getIdentifier(), UUID.fromString(region.getUuid()), region.getMajor(), region.getMinor());
        }
    }

    private void clearRegions() {
        stopMonitoring();
        mRegions.clear();
        storeRegions();
    }

    private void restoreRegions() {
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
        } else {
            CordovaPluginLog.w(TAG, "cannot read " + mRegionsFile.getAbsolutePath());
        }
    }

    private void storeRegions() {
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
        CLEAR_REGIONS,
        SET_OPTIONS,
        GET_OPTIONS,
        CLEAR_OPTIONS,
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
