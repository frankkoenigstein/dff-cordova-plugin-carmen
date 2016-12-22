package com.dff.cordova.plugin.carmen.action;

import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetForegroundScanPeriod extends CarmenAction {
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.SetForegroundScanPeriod";
    public static final String ACTION_NAME = "setForegroundScanPeriod";
    private static final String[] JSON_ARGS = {
            AbstractCarmenBeaconManager.ARG_SCANPERIODMILLIS,
            AbstractCarmenBeaconManager.ARG_WAITTIMEMILLIS,
    };

    public SetForegroundScanPeriod(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                                   CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        try {
            JSONObject jsonArgs = super.checkJsonArgs(args, JSON_ARGS);
            Message msg = Message.obtain(null, AbstractCarmenBeaconManager.WHAT.SET_FOREGROUND_SCANPERIOD.ordinal());

            msg.getData().putLong(AbstractCarmenBeaconManager.ARG_SCANPERIODMILLIS, jsonArgs.getLong(AbstractCarmenBeaconManager.ARG_SCANPERIODMILLIS));
            msg.getData().putLong(AbstractCarmenBeaconManager.ARG_WAITTIMEMILLIS, jsonArgs.getLong(AbstractCarmenBeaconManager.ARG_WAITTIMEMILLIS));

            Log.d(TAG, msg.toString());

            serviceHandler.getService().send(msg);
            this.callbackContext.success();
        } catch (JSONException e) {
            CordovaPluginLog.e(this.getClass().getName(), e.getMessage(), e);
            this.callbackContext.error(e.getMessage());
        } catch (Exception e) {
            CordovaPluginLog.e(this.getClass().getName(), e.getMessage(), e);
            this.callbackContext.error(e.getMessage());
        }
    }

}
