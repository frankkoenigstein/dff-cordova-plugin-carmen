package com.dff.cordova.plugin.carmen.model;

import com.dff.cordova.plugin.carmen.service.AbstractCarmenBeaconManager;
import com.estimote.sdk.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JsonRegion {
    public static JSONObject toJson(Region region) throws JSONException {
        JSONObject jsonThread = new JSONObject();

        jsonThread.put(AbstractCarmenBeaconManager.ARG_IDENTIFIER, region.getIdentifier());
        jsonThread.put(AbstractCarmenBeaconManager.ARG_MAJOR, region.getMajor());
        jsonThread.put(AbstractCarmenBeaconManager.ARG_MINOR, region.getMinor());
        jsonThread.put(AbstractCarmenBeaconManager.ARG_UUID, region.getProximityUUID());

        return jsonThread;
    }

    public static ArrayList<Region> fromJson(JSONArray regions) throws JSONException {
        ArrayList<Region> regionsList = new ArrayList<Region>();

        if (regions != null) {
            for (int i = 0; i < regions.length(); i++) {
                regionsList.add(fromJson(regions.getJSONObject(i)));
            }
        }

        return regionsList;
    }

    public static Region fromJson(JSONObject jsonRegion) throws JSONException {
        String identifier = null;
        UUID uuid = null;
        Integer major = null;
        Integer minor = null;

        if (!jsonRegion.isNull(AbstractCarmenBeaconManager.ARG_IDENTIFIER)) {
            identifier = jsonRegion.getString(AbstractCarmenBeaconManager.ARG_IDENTIFIER);
        }

        if (!jsonRegion.isNull(AbstractCarmenBeaconManager.ARG_MAJOR)) {
            major = new Integer(jsonRegion.getInt(AbstractCarmenBeaconManager.ARG_MAJOR));
        }

        if (!jsonRegion.isNull(AbstractCarmenBeaconManager.ARG_MINOR)) {
            minor = new Integer(jsonRegion.getInt(AbstractCarmenBeaconManager.ARG_MINOR));
        }

        if (!jsonRegion.isNull(AbstractCarmenBeaconManager.ARG_UUID)) {
            uuid = UUID.fromString(jsonRegion.getString(AbstractCarmenBeaconManager.ARG_UUID));
        }

        return new Region(identifier, uuid, major, minor);
    }
}
