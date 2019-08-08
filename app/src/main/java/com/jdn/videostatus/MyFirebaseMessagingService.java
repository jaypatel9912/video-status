package com.jdn.videostatus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        if (!refreshedToken.equalsIgnoreCase(Utils.getPreference(getBaseContext(), Constants.TOKEN))) {
            Utils.setPreference(getBaseContext(), Constants.TOKEN, refreshedToken);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
        }

        try {
            Context context = getBaseContext();

            Intent resultIntent;
            resultIntent = new Intent(getBaseContext(), MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String cid = context.getString(R.string.notification_channel_id);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, cid);

            notificationBuilder.setContentTitle(context.getString(R.string.app_name));

            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName())));
            notificationBuilder.setSmallIcon(context.getResources().getIdentifier("ic_stat_ic_notification", "mipmap", context.getPackageName()));

            notificationBuilder.setChannelId(cid);
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
            notificationBuilder.setAutoCancel(true);

            Notification notification = notificationBuilder.build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            Random random = new Random();
            notificationManager.notify(random.nextInt(1000), notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
