package com.dff.cordova.plugin.carmen.model;


import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.recognition.utils.MacAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class JsonBeacon {
    public static JSONObject toJson(Beacon beacon) throws JSONException {
        JSONObject jsonBeacon = new JSONObject();

        jsonBeacon.put(CarmenServiceWorker.ARG_MAJOR, beacon.getMajor());
        jsonBeacon.put("measuredPower", beacon.getMeasuredPower());
        jsonBeacon.put(CarmenServiceWorker.ARG_MINOR, beacon.getMinor());
        jsonBeacon.put("rssi", beacon.getRssi());
        jsonBeacon.put("macAddress", beacon.getMacAddress());
        jsonBeacon.put(CarmenServiceWorker.ARG_UUID, beacon.getProximityUUID());

        return jsonBeacon;
    }

    public static JSONArray toJson(List<Beacon> beacons) throws JSONException {
        JSONArray jsonBeacons = new JSONArray();

        if (beacons != null) {
            for (Beacon b : beacons) {
                jsonBeacons.put(toJson(b));
            }
        }

        return jsonBeacons;
    }

    public static Beacon fromJson(JSONObject jsonBeacon) throws JSONException {
        int major = -1;
        int measuredPower = -1;
        int minor = -1;
        int rssi = -1;
        MacAddress macAddress = null;
        UUID uuid = null;

        if (!jsonBeacon.isNull(CarmenServiceWorker.ARG_MAJOR)) {
            major = jsonBeacon.getInt(CarmenServiceWorker.ARG_MAJOR);
        }


        if (!jsonBeacon.isNull("measuredPower")) {
            measuredPower = jsonBeacon.getInt("measuredPower");
        }


        if (!jsonBeacon.isNull(CarmenServiceWorker.ARG_MINOR)) {
            minor = jsonBeacon.getInt(CarmenServiceWorker.ARG_MINOR);
        }


        if (!jsonBeacon.isNull("rssi")) {
            rssi = jsonBeacon.getInt("rssi");
        }


        if (!jsonBeacon.isNull("macAddress")) {
            macAddress = MacAddress.fromString(jsonBeacon.getString("macAddress"));
        }


        if (!jsonBeacon.isNull(CarmenServiceWorker.ARG_UUID)) {
            uuid = UUID.fromString(jsonBeacon.getString(CarmenServiceWorker.ARG_UUID));
        }

        return new Beacon(uuid, macAddress, major, minor, measuredPower, rssi);
    }
}
