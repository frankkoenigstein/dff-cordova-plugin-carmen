package com.dff.cordova.plugin.carmen.service.event;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.dff.cordova.plugin.carmen.model.BeaconRegion;
import com.dff.cordova.plugin.carmen.model.Event;
import com.dff.cordova.plugin.carmen.service.CarmenService;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT;
import com.dff.cordova.plugin.carmen.service.CarmenServiceWorker.WHAT_EVENT;
import com.dff.cordova.plugin.carmen.service.helpers.PreferencesHelper;

import java.util.ArrayList;

public class CarmenEventServiceWorker extends Handler {
    private static final String TAG = "com.dff.cordova.plugin.carmen.service.CarmenEventServiceWorker";
    private int mCurrentNotificationID = 0;
    private Context mContext;
    private ArrayList<Event> mEvents = new ArrayList<Event>();
    private String mUserId;
    private PreferencesHelper mPreferencesHelper;

    private Handler mClientHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            WHAT_EVENT msgWhat = WHAT_EVENT.values()[msg.what];

            Event event = new Event(msg.what, msgWhat.name(), mUserId, null, System.currentTimeMillis());
            mEvents.add(event);

            switch (msgWhat) {
                case ENTERED_REGION:
                case EXITED_REGION:
                    BeaconRegion region = (BeaconRegion) msg.obj;

                    event.setRegionId(region.getObjectId());
                    String title = msgWhat.name() + " " + region.getIdentifier();
                    String text = region.getUuid() + " " + region.getMajor() + " " + region.getMinor();

                    // Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();

                    Notification.Builder builder = new Notification.Builder(mContext)
                            .setContentTitle(title)
                            .setContentText(text);

                    sendNotification(builder);

                    break;
                default:
                    break;
            }

            Log.d(TAG, event.toJson().toString());
        }
    };

    public CarmenEventServiceWorker(Looper looper, Context context) {
        super(looper);
        mContext = context;
        mPreferencesHelper = new PreferencesHelper(CarmenService.SHARED_PREFERENCE_NAME, context);
        mUserId = mPreferencesHelper.getString(CarmenServiceWorker.ARG_USER_ID, null);
    }

    public Handler getClientHandler() {
        return mClientHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        WHAT msgWhat = WHAT.values()[msg.what];

        switch (msgWhat) {
            case SET_OPTIONS:
                mUserId = msg.getData().getString(CarmenServiceWorker.ARG_USER_ID);
                mPreferencesHelper.putString(CarmenServiceWorker.ARG_USER_ID, mUserId);
                break;
            default:
                break;
        }
    }

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
