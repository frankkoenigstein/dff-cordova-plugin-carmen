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

public class SetForegroundScanPeriod extends CarmenAction {
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.SetForegroundScanPeriod";
    public static final String ACTION_NAME = "setForegroundScanPeriod";
    private static final String[] JSON_ARGS = {
            CarmenServiceWorker.ARG_SCANPERIODMILLIS,
            CarmenServiceWorker.ARG_WAITTIMEMILLIS,
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
            Message msg = Message.obtain(null, WHAT.SET_FOREGROUND_SCANPERIOD.ordinal());

            msg.getData().putLong(CarmenServiceWorker.ARG_SCANPERIODMILLIS, jsonArgs.getLong(CarmenServiceWorker.ARG_SCANPERIODMILLIS));
            msg.getData().putLong(CarmenServiceWorker.ARG_WAITTIMEMILLIS, jsonArgs.getLong(CarmenServiceWorker.ARG_WAITTIMEMILLIS));

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
