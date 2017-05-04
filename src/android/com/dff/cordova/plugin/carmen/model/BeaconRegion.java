package com.dff.cordova.plugin.carmen.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BeaconRegion implements Serializable, Parcelable {
    public static final Parcelable.Creator<BeaconRegion> CREATOR
            = new Parcelable.Creator<BeaconRegion>() {
        public BeaconRegion createFromParcel(Parcel in) {
            return new BeaconRegion(in);
        }

        public BeaconRegion[] newArray(int size) {
            return new BeaconRegion[size];
        }
    };
    private static final String TAG = "com.dff.cordova.plugin.carmen.model.BeaconRegion";
    public String mObjectId;
    public String mIdentifier;
    public String mUuid;
    public Integer mMajor;
    public Integer mMinor;
    private transient boolean mEntered = false;

    public BeaconRegion(String objectId, String identifier, String uuid, Integer major, Integer minor) {
        mObjectId = objectId;
        mIdentifier = identifier;
        mUuid = uuid;
        mMajor = major;
        mMinor = minor;
    }

    public BeaconRegion(com.estimote.coresdk.observation.region.beacon.BeaconRegion region) {
        mIdentifier = region.getIdentifier();
        mUuid = region.getProximityUUID().toString();
        mMajor = region.getMajor();
        mMinor = region.getMinor();
    }

    private BeaconRegion(Parcel in) {
        mObjectId = in.readString();
        mIdentifier = in.readString();
        mUuid = in.readString();
        mMajor = in.readInt();
        mMinor = in.readInt();
    }

    public static BeaconRegion fromJson(JSONObject jsonRegion) throws JSONException {
        String objectId = null;
        String identifier = null;
        String uuid = null;
        Integer major = null;
        Integer minor = null;

        if (!jsonRegion.isNull(CarmenServiceWorker.ARG_ID)) {
            objectId = jsonRegion.getString(CarmenServiceWorker.ARG_ID);
        }

        if (!jsonRegion.isNull(CarmenServiceWorker.ARG_IDENTIFIER)) {
            identifier = jsonRegion.getString(CarmenServiceWorker.ARG_IDENTIFIER);
        }

        if (!jsonRegion.isNull(CarmenServiceWorker.ARG_MAJOR)) {
            major = jsonRegion.getInt(CarmenServiceWorker.ARG_MAJOR);
        }

        if (!jsonRegion.isNull(CarmenServiceWorker.ARG_MINOR)) {
            minor = jsonRegion.getInt(CarmenServiceWorker.ARG_MINOR);
        }

        if (!jsonRegion.isNull(CarmenServiceWorker.ARG_UUID)) {
            uuid = jsonRegion.getString(CarmenServiceWorker.ARG_UUID);
        }

        return new BeaconRegion(objectId, identifier, uuid, major, minor);
    }

    public static ArrayList<BeaconRegion> fromJson(JSONArray regions) throws JSONException {
        ArrayList<BeaconRegion> regionsList = new ArrayList<BeaconRegion>();

        if (regions != null) {
            for (int i = 0; i < regions.length(); i++) {
                regionsList.add(fromJson(regions.getJSONObject(i)));
            }
        }

        return regionsList;
    }

    public static JSONArray toJson(List<BeaconRegion> regions) {
        JSONArray jsonRegions = new JSONArray();

        if (regions != null) {
            for (BeaconRegion br : regions) {
                jsonRegions.put(br.toJson());
            }
        }

        return jsonRegions;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getUuid() {
        return mUuid;
    }

    public Integer getMajor() {
        return mMajor;
    }

    public Integer getMinor() {
        return mMinor;
    }

    public void setEntered(boolean entered) {
        mEntered = entered;
    }

    public JSONObject toJson() {
        JSONObject jsonRegion = new JSONObject();

        try {
            jsonRegion.put(CarmenServiceWorker.ARG_ID, mObjectId);
            jsonRegion.put(CarmenServiceWorker.ARG_IDENTIFIER, mIdentifier);
            jsonRegion.put(CarmenServiceWorker.ARG_UUID, mUuid);
            jsonRegion.put(CarmenServiceWorker.ARG_MAJOR, mMajor);
            jsonRegion.put(CarmenServiceWorker.ARG_MINOR, mMinor);
            jsonRegion.put(CarmenServiceWorker.ARG_UUID, mUuid);
            jsonRegion.put("entered", mEntered);
        } catch (JSONException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            CordovaPluginLog.e(TAG, e.getMessage(), e);
        }

        return jsonRegion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mObjectId);
        out.writeString(mIdentifier);
        out.writeString(mUuid);
        out.writeInt(mMajor);
        out.writeInt(mMinor);
    }
}
