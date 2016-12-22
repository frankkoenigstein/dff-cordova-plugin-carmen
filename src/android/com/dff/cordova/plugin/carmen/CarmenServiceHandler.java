package com.dff.cordova.plugin.carmen;

import android.content.ComponentName;
import android.os.*;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.carmen.service.CarmenService;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.dff.cordova.plugin.common.service.ServiceHandler;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

public class CarmenServiceHandler extends ServiceHandler {
    private static final String TAG = "CarmenServiceHandler";
    private CallbackContext mOnEventCallbackContext;
    private Messenger serviceClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mOnEventCallbackContext != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, msg.toString());
                result.setKeepCallback(true);
                mOnEventCallbackContext.sendPluginResult(result);
            }
        }
    });

    public CarmenServiceHandler(CordovaInterface cordova) {
        super(cordova, CarmenService.class);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        Message msg = Message.obtain(null, AbstractCarmenBeaconManager.WHAT.REGISTER_CLIENT.ordinal());
        msg.replyTo = serviceClientMessenger;
        try {
            getService().send(msg);
        } catch (RemoteException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        super.onServiceDisconnected(name);
    }

    public void setOnEventCallbackContext(CallbackContext onEventCallbackContext) {
        mOnEventCallbackContext = onEventCallbackContext;
    }
}
