package com.dff.cordova.plugin.carmen.service.ibeacon.estimote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.*;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.service.BeaconService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EstimoteBeaconManager extends AbstractCarmenBeaconManager {
    private static final String TAG = "EstimoteBeaconManager";

    private BeaconManager mBeaconManager;

    public EstimoteBeaconManager(Looper looper, Context context) {
        super(looper, context);
        mBeaconManager = new BeaconManager(mContext);

        mBeaconManager.setErrorListener(new BeaconManager.ErrorListener() {
            @Override
            public void onError(Integer errorId) {
                String errorName = null;

                switch (errorId) {
                    case BeaconService.ERROR_BLUETOOTH_SCANNER_UNSTABLE:
                        errorName = "ERROR_BLUETOOTH_SCANNER_UNSTABLE";
                        break;
                    case BeaconService.ERROR_COULD_NOT_START_LOW_ENERGY_SCANNING:
                        errorName = "ERROR_COULD_NOT_START_LOW_ENERGY_SCANNING";
                        break;
                    default:
                        break;
                }

                Log.e(TAG, WHAT_EVENT.ERROR.name() + " " + errorId + ": " + errorName);
                notifyClients(Message.obtain(null, WHAT_EVENT.ERROR.ordinal(), errorName));
            }
        });

        mBeaconManager.setScanStatusListener(new BeaconManager.ScanStatusListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onScanStart() {
                Log.d(TAG, WHAT_EVENT.SCAN_START.name());
                notifyClients(Message.obtain(null, WHAT_EVENT.SCAN_START.ordinal()));
            }

            @Override
            public void onScanStop()
            {
                Log.d(TAG, WHAT_EVENT.SCAN_STOP.name());
                notifyClients(Message.obtain(null, WHAT_EVENT.SCAN_STOP.ordinal()));
            }
        });

        mBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.ENTERED_REGION.name() + " " + region.getIdentifier());

                Message msg = Message.obtain(null, WHAT_EVENT.ENTERED_REGION.ordinal(), region);
                msg.getData().putParcelableArrayList(ARG_BEACONS, new ArrayList<Beacon>(beacons));
                notifyClients(msg);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onExitedRegion(Region region) {
                Log.d(TAG, WHAT_EVENT.EXITED_REGION.name() + " " + region.getIdentifier());

                Message msg = Message.obtain(null, WHAT_EVENT.EXITED_REGION.ordinal(), region);
                notifyClients(msg);
            }
        });

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.BEACONS_DISCOVERED.name() + " " + region.getIdentifier());

                Message msg = Message.obtain(null, WHAT_EVENT.BEACONS_DISCOVERED.ordinal(), region);
                msg.getData().putParcelableArrayList(ARG_BEACONS, new ArrayList<Beacon>(beacons));
                notifyClients(msg);
            }
        });
    }

    @Override
    protected void startMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        mBeaconManager.startMonitoring(new Region(identifier, uuid, major, minor));
    }

    @Override
    protected void stopMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        mBeaconManager.stopMonitoring(new Region(identifier, uuid, major, minor));
    }

    @Override
    protected void startRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        mBeaconManager.startRanging(new Region(identifier, uuid, major, minor));
    }

    @Override
    protected void stopRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        mBeaconManager.stopRanging(new Region(identifier, uuid, major, minor));
    }

    @Override
    protected void setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis) {
        mBeaconManager.setBackgroundScanPeriod(scanPeriodMillis, waitTimeMillis);
    }

    @Override
    protected void setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis) {
        mBeaconManager.setForegroundScanPeriod(scanPeriodMillis, waitTimeMillis);
    }

    @Override
    protected void setRegionExitExpiration(long period) {
        mBeaconManager.setRegionExitExpiration(period);
    }

    @Override
    public void handleMessage(Message msg)
    {
        WHAT msgWhat = WHAT.values()[msg.what];

        CordovaPluginLog.d(TAG, "handle msg " + msg);

        switch (msgWhat) {
            case CONNECT:
                mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        mBeaconServiceConnected = true;
                        notifyClients(Message.obtain(null, WHAT_EVENT.SERVICE_READY.ordinal()));
                    }
                });
                break;
            case DISCONNECT:
                mBeaconManager.disconnect();
                mBeaconServiceConnected = false;
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
