package com.dff.cordova.plugin.carmen.service.event;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT_EVENT;
import com.estimote.sdk.Region;

public class CarmenEventServiceWorker extends Handler {
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.CarmenEventServiceWorker";
    private int mCurrentNotificationID = 0;
    private Context mContext;

    public CarmenEventServiceWorker(Looper looper, Context context) {
        super(looper);
        mContext = context;
    }

    @SuppressLint("NewApi")
    @Override
    public void handleMessage(Message msg) {
        WHAT_EVENT msgWhat = WHAT_EVENT.values()[msg.what];

        switch (msgWhat) {
            case ENTERED_REGION:
            case EXITED_REGION:
                Region region = (Region) msg.obj;

                String title = msgWhat.name() + " " + region.getIdentifier();
                String text = region.getProximityUUID() + " " + region.getMajor() + " " + region.getMinor();

                // Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();

                Notification.Builder builder = new Notification.Builder(mContext)
                        .setContentTitle(title)
                        .setContentText(text);

                sendNotification(builder);

                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private void sendNotification(Notification.Builder builder) {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName);
        if (launchIntent != null) {
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        }

        builder.setSmallIcon(applicationInfo.icon);

        if (mCurrentNotificationID >= Integer.MAX_VALUE) {
            mCurrentNotificationID = 0;
        }

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(mCurrentNotificationID++, builder.build());
    }

}
