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

public class StopMonitoring extends CarmenAction {
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.StopMonitoring";
    public static final String ACTION_NAME = "stopMonitoring";
    private static final String[] JSON_ARGS = {
            AbstractCarmenBeaconManager.ARG_IDENTIFIER
    };

    public StopMonitoring(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                          CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        try {
            JSONObject jsonArgs = super.checkJsonArgs(args, JSON_ARGS);
            Message msg = Message.obtain(null, AbstractCarmenBeaconManager.WHAT.STOP_MONITORING.ordinal());

            msg.getData().putString(AbstractCarmenBeaconManager.ARG_IDENTIFIER, jsonArgs.getString(AbstractCarmenBeaconManager.ARG_IDENTIFIER));

            if (!jsonArgs.isNull(AbstractCarmenBeaconManager.ARG_UUID)) {
                msg.getData().putString(AbstractCarmenBeaconManager.ARG_UUID, jsonArgs.getString(AbstractCarmenBeaconManager.ARG_UUID));
            }

            if (!jsonArgs.isNull(AbstractCarmenBeaconManager.ARG_MAJOR)) {
                msg.getData().putInt(AbstractCarmenBeaconManager.ARG_MAJOR, jsonArgs.getInt(AbstractCarmenBeaconManager.ARG_MAJOR));
            }

            if (!jsonArgs.isNull(AbstractCarmenBeaconManager.ARG_MINOR)) {
                msg.getData().putInt(AbstractCarmenBeaconManager.ARG_MINOR, jsonArgs.getInt(AbstractCarmenBeaconManager.ARG_MINOR));
            }

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
