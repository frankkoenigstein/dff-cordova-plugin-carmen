package com.dff.cordova.plugin.carmen.action;

import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetOptions extends CarmenAction {
    public static final String ACTION_NAME = "setOptions";
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.SetOptions";
    private static final String[] JSON_ARGS = {};

    public SetOptions(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                      CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        try {
            JSONObject jsonArgs = super.checkJsonArgs(args, JSON_ARGS);
            Message msg = Message.obtain(null, CarmenServiceWorker.WHAT.SET_OPTIONS.ordinal());

            if (jsonArgs.has(CarmenServiceWorker.ARG_USER_ID)) {
                msg.getData().putString(CarmenServiceWorker.ARG_USER_ID, jsonArgs.getString(CarmenServiceWorker.ARG_USER_ID));
            }

            if (jsonArgs.has(CarmenServiceWorker.ARG_JWT)) {
                msg.getData().putString(CarmenServiceWorker.ARG_JWT, jsonArgs.getString(CarmenServiceWorker.ARG_JWT));
            }

            if (jsonArgs.has(CarmenServiceWorker.ARG_SERVER_URL)) {
                msg.getData().putString(CarmenServiceWorker.ARG_SERVER_URL, jsonArgs.getString(CarmenServiceWorker.ARG_SERVER_URL));
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
