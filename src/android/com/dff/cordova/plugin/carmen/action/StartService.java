package com.dff.cordova.plugin.carmen.action;

import android.content.Context;
import android.content.Intent;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.service.CarmenService;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;

public class StartService extends CarmenAction {
    public static final String ACTION_NAME = "startService";

    public StartService(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova, CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    @Override
    public void run() {
        super.run();

        Context context = cordova.getActivity().getApplicationContext();
        context.startService(new Intent(context, CarmenService.class));
    }
}
