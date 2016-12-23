package com.dff.cordova.plugin.carmen.model;

import com.estimote.sdk.Nearable;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBatteryLevel {
	public static JSONObject toJson(Nearable.BatteryLevel batteryLevel) throws JSONException {
		JSONObject jsonBatteryLevel = new JSONObject();

		if (batteryLevel != null) {
			jsonBatteryLevel.put("voltage", batteryLevel.voltage);
			jsonBatteryLevel.put("name", batteryLevel.name());
			jsonBatteryLevel.put("ordinal", batteryLevel.ordinal());
		}

		return jsonBatteryLevel;
	}
}
