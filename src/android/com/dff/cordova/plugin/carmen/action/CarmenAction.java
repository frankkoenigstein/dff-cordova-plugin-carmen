package com.dff.cordova.plugin.carmen.action;

import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.common.service.action.ServiceAction;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenAction extends ServiceAction {
    public CarmenAction(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova, CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }
}
