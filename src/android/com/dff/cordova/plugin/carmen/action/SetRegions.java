package com.dff.cordova.plugin.carmen.action;

import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.model.BeaconRegion;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetRegions extends CarmenAction {
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.SetRegions";
    public static final String ACTION_NAME = "setRegions";
    private static final String[] JSON_ARGS = {
            CarmenServiceWorker.ARG_REGIONS
    };

    public SetRegions(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                      CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        try {
            JSONObject jsonArgs = super.checkJsonArgs(args, JSON_ARGS);
            Message msg = Message.obtain(null, CarmenServiceWorker.WHAT.SET_REGIONS.ordinal());

            JSONArray jsonRegions = jsonArgs.getJSONArray(CarmenServiceWorker.ARG_REGIONS);
            msg.getData().putParcelableArrayList(CarmenServiceWorker.ARG_REGIONS, BeaconRegion.fromJson(jsonRegions));

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
