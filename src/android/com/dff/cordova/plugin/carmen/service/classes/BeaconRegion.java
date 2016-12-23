package com.dff.cordova.plugin.carmen.service.classes;

import com.dff.cordova.plugin.carmen.model.JsonRegion;
import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.sdk.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

public class BeaconRegion implements Serializable {
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.classes.BeaconRegion";

    public String mIdentifier;
    public String mUuid;
    public Integer mMajor;
    public Integer mMinor;

    public boolean mEntered = false;

    public BeaconRegion(Region region) {
        mIdentifier = region.getIdentifier();
        mUuid = region.getProximityUUID().toString();
        mMajor = region.getMajor();
        mMinor = region.getMinor();
    }

    public JSONObject toJson() {
        JSONObject jsonRegion = new JSONObject();

        try {
            JSONObject estimoteJsonRegion = JsonRegion.toJson(new Region(mIdentifier, UUID.fromString(mUuid), mMajor, mMinor));
            jsonRegion.put(AbstractCarmenBeaconManager.ARG_REGION, estimoteJsonRegion);
            jsonRegion.put("entered", mEntered);
        } catch (JSONException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        }

        return jsonRegion;
    }
}
