package com.dff.cordova.plugin.carmen.model;

import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Event implements Serializable {
    private static final String TAG = "com.dff.cordova.plugin.carmen.model.Event";

    private int mType;
    private String mTypeName;
    private String mUserId;
    private String mRegionId;
    private long mTimeStamp;

    public Event(int type, String typeName, String userId, String regionId, long timeStamp) {
        mType = type;
        mTypeName = typeName;
        mUserId = userId;
        mRegionId = regionId;
        mTimeStamp = timeStamp;
    }

    public static JSONArray toJson(List<Event> events) {
        JSONArray jsonEvents = new JSONArray();

        if (events != null) {
            for (Event e : events) {
                jsonEvents.put(e.toJson());
            }
        }

        return jsonEvents;
    }

    public JSONObject toJson() {
        JSONObject jsonEvent = new JSONObject();

        try {
            jsonEvent.put("type", mType);
            jsonEvent.put("typeName", mTypeName);
            jsonEvent.put("user", mUserId);
            jsonEvent.put("region", mRegionId);
            jsonEvent.put("timestamp", mTimeStamp);
        } catch (JSONException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        }

        return jsonEvent;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public void setTypeName(String typeName) {
        mTypeName = typeName;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getRegionId() {
        return mRegionId;
    }

    public void setRegionId(String regionId) {
        mRegionId = regionId;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }
}
