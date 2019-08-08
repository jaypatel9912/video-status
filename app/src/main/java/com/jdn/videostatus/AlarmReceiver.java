package com.jdn.videostatus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
/**
 * Created by lucasrodrigues on 31/07/17.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int id = intent.getIntExtra(AlarmReceiver.class.toString(), 1);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            int day = 0;

            int actualHour = calendar.get(Calendar.HOUR_OF_DAY);
            int actualMinute = calendar.get(Calendar.MINUTE);
            Log.d("Alarm received", "actualHour : actualMinute -> " + actualHour + " : " + actualMinute);
            if (actualHour != id || actualMinute != 0) {
                Log.d("Alarm received", "hour : minute -> " + id + " : " + actualMinute);
                return;
            }

            Intent resultIntent;
            resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            String cid = context.getString(R.string.notification_channel_id);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, cid);

            notificationBuilder.setContentTitle(context.getString(R.string.app_name));

            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName())));
            notificationBuilder.setSmallIcon(context.getResources().getIdentifier("ic_stat_ic_notification", "mipmap", context.getPackageName()));

            notificationBuilder.setChannelId(cid);
            notificationBuilder.setContentText(getMessageById(context));
            notificationBuilder.setContentIntent(resultPendingIntent);
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(getMessageById(context)));
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

    private String getMessageById(Context context) {
        return context.getString(R.string.check_new_videos);
    }
}
