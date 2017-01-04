package com.dff.cordova.plugin.carmen;

import android.content.ComponentName;
import android.os.*;
import com.dff.cordova.plugin.carmen.model.BeaconRegion;
import com.dff.cordova.plugin.carmen.model.JsonBeacon;
import com.dff.cordova.plugin.carmen.service.CarmenService;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT_EVENT;
import com.dff.cordova.plugin.common.AbstractPluginListener;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.dff.cordova.plugin.common.service.ServiceHandler;
import com.estimote.sdk.Beacon;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CarmenServiceHandler extends ServiceHandler {
    private static final String TAG = "CarmenServiceWorker";
    private CallbackContext mOnEventCallbackContext;
    private Messenger serviceClientMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Beacon> beacons = null;
            JSONObject jsonMsg = new JSONObject();
            WHAT_EVENT msgWHat = WHAT_EVENT.values()[msg.what];

            try {
                jsonMsg.put("what", msg.what);
                jsonMsg.put("arg1", msg.arg1);
                jsonMsg.put("arg2", msg.arg2);
                jsonMsg.put("whatName", msgWHat.name());

                switch (msgWHat) {
                    case ERROR:
                    case BEACON_ERROR:
                        jsonMsg.put("error", msg.obj.toString());
                        break;
                    case BEACONS_DISCOVERED:
                    case ENTERED_REGION:
                        beacons = msg.getData().getParcelableArrayList(CarmenServiceWorker.ARG_BEACONS);
                    case EXITED_REGION:
                        BeaconRegion region = (BeaconRegion) msg.obj;
                        JSONObject jsonRegion = region.toJson();

                        if (beacons != null) {
                            jsonRegion.put(CarmenServiceWorker.ARG_BEACONS, JsonBeacon.toJson(beacons));
                        }

                        jsonMsg.put(CarmenServiceWorker.ARG_REGION, jsonRegion);
                        break;
                    case SCAN_START:
                    case SCAN_STOP:
                        break;
                    default:
                        CordovaPluginLog.w(TAG, "unknown event " + msg);
                        break;
                }

                AbstractPluginListener.sendPluginResult(mOnEventCallbackContext, jsonMsg);
            } catch (JSONException e) {
                AbstractPluginListener.sendPluginResult(mOnEventCallbackContext, e);
            } catch (Exception e) {
                AbstractPluginListener.sendPluginResult(mOnEventCallbackContext, e);
            }
        }
    });

    public CarmenServiceHandler(CordovaInterface cordova) {
        super(cordova, CarmenService.class);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        Message msg = Message.obtain(null, WHAT.REGISTER_CLIENT.ordinal());
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
