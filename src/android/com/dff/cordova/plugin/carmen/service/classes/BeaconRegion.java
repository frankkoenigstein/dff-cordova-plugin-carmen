package com.dff.cordova.plugin.carmen.service.classes;

import android.os.Parcel;
import android.os.Parcelable;
import com.dff.cordova.plugin.carmen.model.JsonRegion;
import com.dff.cordova.plugin.common.log.CordovaPluginLog;
import com.estimote.sdk.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.classes.BeaconRegion";
    public String mIdentifier;
    public String mUuid;
    public Integer mMajor;
    public Integer mMinor;
    private boolean mEntered = false;

    public BeaconRegion(Region region) {
        mIdentifier = region.getIdentifier();
        mUuid = region.getProximityUUID().toString();
        mMajor = region.getMajor();
        mMinor = region.getMinor();
    }

    private BeaconRegion(Parcel in) {
        mIdentifier = in.readString();
        mUuid = in.readString();
        mMajor = in.readInt();
        mMinor = in.readInt();
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

    public boolean isEntered() {
        return mEntered;
    }

    public void setEntered(boolean entered) {
        mEntered = entered;
    }

    public JSONObject toJson() {
        JSONObject jsonRegion = new JSONObject();

        try {
            jsonRegion = JsonRegion.toJson(new Region(mIdentifier, UUID.fromString(mUuid), mMajor, mMinor));
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
        out.writeString(mIdentifier);
        out.writeString(mUuid);
        out.writeInt(mMajor);
        out.writeInt(mMinor);
    }

}
