package com.dff.cordova.plugin.carmen.action;

import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
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
            CarmenServiceWorker.ARG_IDENTIFIER
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
            Message msg = Message.obtain(null, WHAT.STOP_MONITORING.ordinal());

            msg.getData().putString(CarmenServiceWorker.ARG_IDENTIFIER, jsonArgs.getString(CarmenServiceWorker.ARG_IDENTIFIER));

            if (!jsonArgs.isNull(CarmenServiceWorker.ARG_UUID)) {
                msg.getData().putString(CarmenServiceWorker.ARG_UUID, jsonArgs.getString(CarmenServiceWorker.ARG_UUID));
            }

            if (!jsonArgs.isNull(CarmenServiceWorker.ARG_MAJOR)) {
                msg.getData().putInt(CarmenServiceWorker.ARG_MAJOR, jsonArgs.getInt(CarmenServiceWorker.ARG_MAJOR));
            }

            if (!jsonArgs.isNull(CarmenServiceWorker.ARG_MINOR)) {
                msg.getData().putInt(CarmenServiceWorker.ARG_MINOR, jsonArgs.getInt(CarmenServiceWorker.ARG_MINOR));
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
