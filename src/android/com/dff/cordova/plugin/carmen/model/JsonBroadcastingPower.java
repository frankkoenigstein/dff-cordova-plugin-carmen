package com.dff.cordova.plugin.carmen.model;

import com.estimote.sdk.cloud.model.BroadcastingPower;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBroadcastingPower {
    public static JSONObject toJson(BroadcastingPower broadcastingPower) throws JSONException {
        JSONObject jsonBroadcastingPower = new JSONObject();

        if (broadcastingPower != null) {
            jsonBroadcastingPower.put("powerInDbm", broadcastingPower.powerInDbm);
            jsonBroadcastingPower.put("ordinal", broadcastingPower.ordinal());
            jsonBroadcastingPower.put("name", broadcastingPower.name());
        }

        return jsonBroadcastingPower;
    }
}
