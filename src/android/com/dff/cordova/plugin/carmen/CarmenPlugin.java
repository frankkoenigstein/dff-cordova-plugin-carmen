package com.dff.cordova.plugin.carmen;

import android.util.Log;
import com.dff.cordova.plugin.carmen.action.*;
import com.dff.cordova.plugin.common.action.CordovaAction;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.dff.cordova.plugin.common.service.CommonServicePlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CarmenPlugin extends CommonServicePlugin {
    private static final String TAG = "com.dff.cordova.plugin.carmen.CarmenPlugin";
    private HashMap<String, Class<? extends CarmenAction>> mActions = new HashMap<String, Class<? extends CarmenAction>>();
    private CarmenServiceHandler mCarmenServiceHandler;

    public CarmenPlugin() {
        super(TAG);

        mActions.put(StartService.ACTION_NAME, StartService.class);
        mActions.put(StartMonitoring.ACTION_NAME, StartMonitoring.class);
        mActions.put(StopMonitoring.ACTION_NAME, StopMonitoring.class);
        mActions.put(StartRanging.ACTION_NAME, StartRanging.class);
        mActions.put(StopRanging.ACTION_NAME, StopRanging.class);
        mActions.put(SetBackgroundScanPeriod.ACTION_NAME, SetBackgroundScanPeriod.class);
        mActions.put(SetForegroundScanPeriod.ACTION_NAME, SetForegroundScanPeriod.class);
        mActions.put(SetRegionExitExpiration.ACTION_NAME, SetRegionExitExpiration.class);
        mActions.put(SetRegions.ACTION_NAME, SetRegions.class);
    }

    @Override
    public void pluginInitialize() {
        mCarmenServiceHandler = new CarmenServiceHandler(this.cordova);
        super.pluginInitialize(mCarmenServiceHandler);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "call for action: " + action + "; args: " + args);

        CordovaAction cordovaAction = null;

        if ("onEvent".equals(action)) {
            mCarmenServiceHandler.setOnEventCallbackContext(callbackContext);
            return true;
        }
        else if (mActions.containsKey(action)) {
            Class<? extends CarmenAction> actionClass = mActions.get(action);

            Log.d(TAG, "found action: " + actionClass.getName());

            try {
                cordovaAction = actionClass.getConstructor(String.class
                        , JSONArray.class
                        , CallbackContext.class
                        , CordovaInterface.class
                        , CarmenServiceHandler.class
                )
                        .newInstance(action, args, callbackContext, this.cordova, mCarmenServiceHandler);
            } catch (InstantiationException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (InvocationTargetException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            } catch (SecurityException e) {
                CordovaPluginLog.e(TAG, e.getMessage(), e);
            }
        }

        if (cordovaAction != null) {
            cordova.getThreadPool().execute(cordovaAction);
            return true;
        }

        return super.execute(action, args, callbackContext);
    }
}
