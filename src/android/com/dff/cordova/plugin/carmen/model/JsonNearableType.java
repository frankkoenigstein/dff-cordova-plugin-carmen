package com.dff.cordova.plugin.carmen.model;

import com.estimote.sdk.cloud.model.NearableType;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonNearableType {
    public static JSONObject toJson(NearableType nearableType) throws JSONException {
        JSONObject jsonNearableType = new JSONObject();

        if (nearableType != null) {
            jsonNearableType.put("text", nearableType.text);
            jsonNearableType.put("ordinal", nearableType.ordinal());
            jsonNearableType.put("name", nearableType.name());
        }

        return jsonNearableType;
    }
}
