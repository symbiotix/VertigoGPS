package com.bartonstanley.vertigogps;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by bartonstanley on 4/27/15.
 */
public class Notifier {

    private FragmentActivity mActivity = null;
    private NotificationManager mNotificationManager = null;

    private static Notifier mNotifier = null;


    private Notifier(FragmentActivity activity) {
        mActivity = activity;
        mNotificationManager =
                (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public static Notifier getNotifier(FragmentActivity activity) {
        if (mNotifier == null) {
            mNotifier = new Notifier(activity);
        }

        return mNotifier;
    }

    public void cancel(int notificationId) {
        mNotificationManager.cancel(notificationId);
    }

    public void cancelAll() {
        mNotificationManager.cancelAll();
    }

    public void createNotification(int notificationId, String formattedDistanceInMiles) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Carry on wayward son (or daughter)")
                .setContentText("You are " + formattedDistanceInMiles + " mi away from home.");

        Intent resultIntent = new Intent(mActivity, MainActivity.class);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mActivity, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());

    }

    public void updateDistance(int notificationId, String formattedDistanceInMiles) {
        createNotification(notificationId, formattedDistanceInMiles);
    }
}
