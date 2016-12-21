package com.dff.cordova.plugin.carmen;

import android.annotation.SuppressLint;
import android.util.Log;
import com.dff.cordova.plugin.carmen.action.CarmenAction;
import com.dff.cordova.plugin.carmen.action.StartService;
import com.dff.cordova.plugin.common.action.CordovaAction;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.dff.cordova.plugin.common.service.CommonServicePlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenPlugin extends CommonServicePlugin {
    private static final String TAG = "com.dff.cordova.plugin.carmen.CarmenPlugin";
    protected HashMap<String, Class<? extends CarmenAction>> mActions = new HashMap<String, Class<? extends CarmenAction>>();
    private CarmenServiceHandler mCarmenServiceHandler;

    public CarmenPlugin() {
        super(TAG);

        mActions.put(StartService.ACTION_NAME, StartService.class);
    }

    @Override
    public void pluginInitialize() {
        mCarmenServiceHandler = new CarmenServiceHandler(this.cordova);
        super.pluginInitialize(mCarmenServiceHandler);
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "call for action: " + action + "; args: " + args);

        CordovaAction cordovaAction = null;

        if (mActions.containsKey(action)) {
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
