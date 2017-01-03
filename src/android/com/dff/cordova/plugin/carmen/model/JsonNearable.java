package com.dff.cordova.plugin.carmen.model;

import com.estimote.sdk.Nearable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonNearable {
    public static JSONObject toJson(Nearable nearable) throws JSONException {
        JSONObject jsonNearable = new JSONObject();

        if (nearable != null) {
            jsonNearable.put("batteryLevel", JsonBatteryLevel.toJson(nearable.batteryLevel));
            jsonNearable.put("bootloaderVersion", nearable.bootloaderVersion);
            jsonNearable.put("color", nearable.color.toString());
            jsonNearable.put("currentMotionStateDuration", nearable.currentMotionStateDuration);
            jsonNearable.put("firmwareState", nearable.firmwareState);
            jsonNearable.put("firmwareStateName", nearable.firmwareState.name());
            jsonNearable.put("firmwareVersion", nearable.firmwareVersion);
            jsonNearable.put("hardwareVersion", nearable.hardwareVersion);
            jsonNearable.put("identifier", nearable.identifier);
            jsonNearable.put("isMoving", nearable.isMoving);
            jsonNearable.put("lastMotionStateDuration", nearable.lastMotionStateDuration);
            jsonNearable.put("orientation", nearable.orientation);
            jsonNearable.put("orientationName", nearable.orientation.name());
            jsonNearable.put("broadcastingPower", JsonBroadcastingPower.toJson(nearable.power));
            jsonNearable.put("region", JsonRegion.toJson(nearable.region));
            jsonNearable.put("rssi", nearable.rssi);
            jsonNearable.put("temperature", nearable.temperature);
            jsonNearable.put("temperature", JsonNearableType.toJson(nearable.type));
            jsonNearable.put("xAcceleration", nearable.xAcceleration);
            jsonNearable.put("yAcceleration", nearable.yAcceleration);
            jsonNearable.put("zAcceleration", nearable.zAcceleration);
        }

        return jsonNearable;
    }

    public static JSONArray toJson(List<Nearable> nearables) throws JSONException {
        JSONArray jsonNearables = new JSONArray();

        if (nearables != null) {
            for (Nearable n : nearables) {
                jsonNearables.put(toJson(n));
            }
        }

        return jsonNearables;
    }
}
