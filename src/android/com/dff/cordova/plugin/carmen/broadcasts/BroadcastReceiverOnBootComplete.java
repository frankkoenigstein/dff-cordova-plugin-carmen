package com.dff.cordova.plugin.carmen.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dff.cordova.plugin.carmen.service.CarmenService;

/**
 * Created by frank on 21.12.16.
 */
public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    private static final String TAG = "com.dff.cordova.plugin.carmen.broadcasts.BroadcastReceiverOnBootComplete";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED) ||
                intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent serviceIntent = new Intent(context, CarmenService.class);
            context.startService(serviceIntent);
        }
    }
}
