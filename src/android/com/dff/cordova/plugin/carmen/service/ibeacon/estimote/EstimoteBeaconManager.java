package com.dff.cordova.plugin.carmen.service.ibeacon.estimote;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT_EVENT;
import com.dff.cordova.plugin.carmen.service.classes.BeaconRegion;
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
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                EstimoteBeaconManager.super.onServiceReady();

                for (BeaconRegion beaconRegion : mRegions.values()) {
                    startMonitoring(beaconRegion.mIdentifier, UUID.fromString(beaconRegion.mUuid), beaconRegion.mMajor, beaconRegion.mMinor);
                }
            }
        });

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
                notifyClients(Message.obtain(null, WHAT_EVENT.BEACON_ERROR.ordinal(), errorId, 0, errorName));
            }
        });

        mBeaconManager.setScanStatusListener(new BeaconManager.ScanStatusListener() {
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
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.ENTERED_REGION.name() + " " + region.getIdentifier());

                BeaconRegion beaconRegion = mRegions.get(region.getIdentifier());
                if (beaconRegion != null) {
                    beaconRegion.setEntered(true);
                }

                Message msg = Message.obtain(null, WHAT_EVENT.ENTERED_REGION.ordinal(), region);
                msg.getData().putParcelableArrayList(CarmenServiceWorker.ARG_BEACONS, new ArrayList<Beacon>(beacons));
                notifyClients(msg);
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d(TAG, WHAT_EVENT.EXITED_REGION.name() + " " + region.getIdentifier());

                BeaconRegion beaconRegion = mRegions.get(region.getIdentifier());
                if (beaconRegion != null) {
                    beaconRegion.setEntered(false);
                }

                Message msg = Message.obtain(null, WHAT_EVENT.EXITED_REGION.ordinal(), region);
                notifyClients(msg);
            }
        });

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d(TAG, WHAT_EVENT.BEACONS_DISCOVERED.name() + " " + region.getIdentifier());

                Message msg = Message.obtain(null, WHAT_EVENT.BEACONS_DISCOVERED.ordinal(), region);
                msg.getData().putParcelableArrayList(CarmenServiceWorker.ARG_BEACONS, new ArrayList<Beacon>(beacons));
                notifyClients(msg);
            }
        });
    }

    @Override
    protected void startMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        Region region = new Region(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "start monitoring " + region);
        mBeaconManager.startMonitoring(region);
    }

    @Override
    protected void stopMonitoring(String identifier, UUID uuid, Integer major, Integer minor) {
        Region region = new Region(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "stop monitoring " + region);
        mBeaconManager.stopMonitoring(region);
    }

    @Override
    protected void startRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        Region region = new Region(identifier, uuid, major, minor);
        CordovaPluginLog.d(TAG, "start ranging " + region);
        mBeaconManager.startRanging(region);
    }

    @Override
    protected void stopRanging(String identifier, UUID uuid, Integer major, Integer minor) {
        Region region = new Region(identifier, uuid, major, minor);
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
    public void handleMessage(Message msg)
    {
        WHAT msgWhat = WHAT.values()[msg.what];

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
            case SET_REGIONS:
                ArrayList<Region> regions = msg.getData().getParcelableArrayList(CarmenServiceWorker.ARG_REGIONS);
                for (Region region : regions) {
                    mRegions.put(region.getIdentifier(), new BeaconRegion(region));
                    mBeaconManager.startMonitoring(region);
                }

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
                super.handleMessage(msg);
                break;
        }
    }
}
