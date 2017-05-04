package com.dff.cordova.plugin.carmen.service.ibeacon.estimote;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import com.dff.cordova.plugin.carmen.model.BeaconRegion;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT_EVENT;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.coresdk.service.BeaconService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EstimoteBeaconManager extends AbstractCarmenBeaconManager {
    private static final String TAG = "EstimoteBeaconManager";
    private BeaconManager mBeaconManager;

    public EstimoteBeaconManager(Looper looper, Context context, CarmenServiceWorker carmenServiceWorker) {
        super(looper, context, carmenServiceWorker);

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

                Log.e(TAG, WHAT_EVENT.BEACON_ERROR.name() + " " + errorId + ": " + errorName);
                mCarmenServiceWorker.notifyClients(Message.obtain(null, WHAT_EVENT.BEACON_ERROR.ordinal(), errorId, 0, errorName));
            }
        });

        mBeaconManager.setScanStatusListener(new BeaconManager.ScanStatusListener() {
            @Override
            public void onScanStart() {
                Log.d(TAG, WHAT_EVENT.SCAN_START.name());
                mCarmenServiceWorker.notifyClients(Message.obtain(null, WHAT_EVENT.SCAN_START.ordinal()));
            }

            @Override
            public void onScanStop() {
                Log.d(TAG, WHAT_EVENT.SCAN_STOP.name());
                mCarmenServiceWorker.notifyClients(Message.obtain(null, WHAT_EVENT.SCAN_STOP.ordinal()));
            }
        });

        mBeaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(com.estimote.coresdk.observation.region.beacon.BeaconRegion region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.ENTERED_REGION.name() + " " + region.getIdentifier());

                BeaconRegion beaconRegion = mCarmenServiceWorker.getRegions().get(region.getIdentifier());
                if (beaconRegion != null) {
                    beaconRegion.setEntered(true);
                }

                Message msg = Message.obtain(null, WHAT_EVENT.ENTERED_REGION.ordinal(), mCarmenServiceWorker.getRegion(region.getIdentifier()));
                msg.getData().putParcelableArrayList(CarmenServiceWorker.ARG_BEACONS, new ArrayList<Parcelable>(beacons));
                mCarmenServiceWorker.notifyClients(msg);
            }

            @Override
            public void onExitedRegion(com.estimote.coresdk.observation.region.beacon.BeaconRegion region) {
                Log.d(TAG, WHAT_EVENT.EXITED_REGION.name() + " " + region.getIdentifier());

                BeaconRegion beaconRegion = mCarmenServiceWorker.getRegions().get(region.getIdentifier());
                if (beaconRegion != null) {
                    beaconRegion.setEntered(false);
                }

                Message msg = Message.obtain(null, WHAT_EVENT.EXITED_REGION.ordinal(), mCarmenServiceWorker.getRegion(region.getIdentifier()));
                mCarmenServiceWorker.notifyClients(msg);
            }
        });

        mBeaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(com.estimote.coresdk.observation.region.beacon.BeaconRegion region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.BEACONS_DISCOVERED.name() + " " + region.getIdentifier());

                Message msg = Message.obtain(null, WHAT_EVENT.BEACONS_DISCOVERED.ordinal(), mCarmenServiceWorker.getRegion(region.getIdentifier()));
                msg.getData().putParcelableArrayList(CarmenServiceWorker.ARG_BEACONS, new ArrayList<Beacon>(beacons));
                mCarmenServiceWorker.notifyClients(msg);
            }
        });

        connect();
    }

    private void connect() {
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                mBeaconServiceConnected = true;
                EstimoteBeaconManager.super.onServiceReady();
            }
        });
    }

    @Override
    protected void startMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        com.estimote.coresdk.observation.region.beacon.BeaconRegion region = new com.estimote.coresdk.observation.region.beacon.BeaconRegion(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "start monitoring " + region);
        mBeaconManager.startMonitoring(region);
    }

    @Override
    protected void stopMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        com.estimote.coresdk.observation.region.beacon.BeaconRegion region = new com.estimote.coresdk.observation.region.beacon.BeaconRegion(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "stop monitoring " + region);
        mBeaconManager.stopMonitoring(region.getIdentifier());
    }

    @Override
    protected void startRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        com.estimote.coresdk.observation.region.beacon.BeaconRegion region = new com.estimote.coresdk.observation.region.beacon.BeaconRegion(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "start ranging " + region);
        mBeaconManager.startRanging(region);
    }

    @Override
    protected void stopRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        com.estimote.coresdk.observation.region.beacon.BeaconRegion region = new com.estimote.coresdk.observation.region.beacon.BeaconRegion(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "stop ranging " + region);
        mBeaconManager.stopRanging(region);
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
    public void handleMessage(Message msg) {
        WHAT msgWhat = WHAT.values()[msg.what];

        switch (msgWhat) {
            case CONNECT:
                connect();
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
