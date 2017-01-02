package com.dff.cordova.plugin.carmen.action;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.dff.cordova.plugin.carmen.CarmenServiceHandler;
import com.dff.cordova.plugin.carmen.model.JsonRegion;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.carmen.service.classes.BeaconRegion;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.sdk.Region;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class GetRegions extends CarmenAction {
    public static final String ACTION_NAME = "getRegions";
    private static final String TAG = "com.dff.cordova.plugin.carmen.action.GetRegions";

    public GetRegions(String action, JSONArray args, CallbackContext callbackContext, CordovaInterface cordova,
                      CarmenServiceHandler serviceHandler) {
        super(action, args, callbackContext, cordova, serviceHandler);
    }

    protected Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AbstractCarmenBeaconManager.WHAT msgWhat = AbstractCarmenBeaconManager.WHAT.values()[msg.what];

            switch (msgWhat) {
                case RESULT:
                    AbstractCarmenBeaconManager.WHAT arg1What = AbstractCarmenBeaconManager.WHAT.values()[msg.arg1];
                    switch (arg1What) {
                        case GET_REGIONS:
                            try {
                                ArrayList<BeaconRegion> regions = msg.getData().getParcelableArrayList(AbstractCarmenBeaconManager.ARG_REGIONS);
                                JSONArray jsonRegions = BeaconRegion.toJson(regions);
                                callbackContext.success(jsonRegions);
                            }
                            catch (Exception e) {
                                CordovaPluginLog.e(TAG, e.getMessage(), e);
                                callbackContext.error(e.getMessage());
                            }
                            break;
                        default:
                            String errMsg = "unexpected result " + arg1What.name();
                            CordovaPluginLog.w(TAG, errMsg);
                            callbackContext.error(errMsg);
                            break;
                    }

                    break;
                default:
                    String errMsg = "unexpected msg " + msg.toString();
                    CordovaPluginLog.w(TAG, errMsg);
                    callbackContext.error(errMsg);
                    break;
            }
        }
    });

    @Override
    public void run() {
        super.run();

        try {
            Message msg = Message.obtain(null, AbstractCarmenBeaconManager.WHAT.GET_REGIONS.ordinal());
            msg.replyTo = mMessenger;
            serviceHandler.getService().send(msg);
        } catch (RemoteException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
            callbackContext.error(e.getMessage());
        }
    }
}
