package com.dff.cordova.plugin.carmen.broadcasts;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.CarmenService;

/**
 * Created by frank on 21.12.16.
 */
public class CarmenServiceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CarmenServiceBroadcastReceiver";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());

        String action = intent.getAction();

        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) ||
                action.equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent serviceIntent = new Intent(context, CarmenService.class);
            context.startService(serviceIntent);
        }
    }
}
