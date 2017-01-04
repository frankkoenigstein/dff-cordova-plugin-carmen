package com.dff.cordova.plugin.carmen.service.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferencesHelper {
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.helpers.PreferencesHelper";
    private String mPreferencesName;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public PreferencesHelper(String preferencesName, Context context) {
        mPreferencesName = preferencesName;
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(mPreferencesName, Context.MODE_PRIVATE);
    }

    public String getString(String preferencesName, String defaultValue) {
        return mSharedPreferences.getString(preferencesName, defaultValue);
    }

    public boolean putString(String preferenceName, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(preferenceName, value);
        boolean res = editor.commit();
        Log.d(TAG, "putString " + preferenceName + ": " + res);
        return res;
    }
}
