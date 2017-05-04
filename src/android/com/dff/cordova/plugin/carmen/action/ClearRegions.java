package com.dff.cordova.plugin.carmen.action;

import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;

public class ClearRegions extends CarmenAction {
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.ClearRegions";
    public static final String ACTION_NAME = "clearRegions";

    public ClearRegions(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                        CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        try {
            Message msg = Message.obtain(null, CarmenServiceWorker.WHAT.CLEAR_REGIONS.ordinal());
            Log.d(TAG, msg.toString());

            serviceHandler.getService().send(msg);
            this.callbackContext.success();
        } catch (Exception e) {
            CordovaPluginLog.e(this.getClass().getName(), e.getMessage(), e);
            this.callbackContext.error(e.getMessage());
        }
    }

}
