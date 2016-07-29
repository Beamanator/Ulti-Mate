package io.scoober.ulti.ulti_mate;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by Poo on 7/26/2016.
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get notification data from intent
        String msg = intent.getStringExtra(MainMenuActivity.NOTIFICATION_MESSAGE);
        String msgText = intent.getStringExtra(MainMenuActivity.NOTIFICATION_MESSAGE_TEXT);
        String msgAlert = intent.getStringExtra(MainMenuActivity.NOTIFICATION_MESSAGE_ALERT);
        Class className = (Class<Activity>)
                intent.getSerializableExtra(MainMenuActivity.NOTIFICATION_NEXT_CLASS);
        int notificationId = intent.getIntExtra(MainMenuActivity.NOTIFICATION_ID, 0);

        // get game bundle data
        MainMenuActivity.DisplayToLaunch dtl = (MainMenuActivity.DisplayToLaunch)
                intent.getSerializableExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA);
        long gameId = intent.getLongExtra(MainMenuActivity.GAME_ID_EXTRA, 0);

        if (dtl != null || gameId != 0) {
            Bundle newArgs = new Bundle();
            newArgs.putSerializable(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, dtl);
            newArgs.putLong(MainMenuActivity.GAME_ID_EXTRA, gameId);

            createNotification(context, msg, msgText, msgAlert, className, notificationId,
                    newArgs);
        } else {
            createNotification(context, msg, msgText, msgAlert, className, notificationId);
        }
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert,
                                   Class className, int id) {
        createNotification(context, msg, msgText, msgAlert, className, id, null);
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert,
                                   Class className, int id, Bundle bundle) {

        Intent nextIntent = new Intent(context, className);
        if (bundle != null) {
            nextIntent.putExtras(bundle);
        }

        // Need to pass PendingIntent.FLAG_UPDATE_CURRENT to pass along game extras
        PendingIntent notificationIntent = PendingIntent.getActivity(context, id,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_hourglass_empty_black_24dp)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert)
                .setAutoCancel(true)
                .setContentIntent(notificationIntent)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // id is unique ID for this notification
        mNotificationManager.notify(id, mBuilder.build());
    }
}
