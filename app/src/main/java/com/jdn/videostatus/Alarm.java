package com.jdn.videostatus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lucasrodrigues on 31/07/17.
 */

public class Alarm {
    private static int NOTIFICATION_FLAG = PendingIntent.FLAG_CANCEL_CURRENT;

    public static void start(Context context) {
        boolean alarm12 = Alarm.alarmExists(context, 12);
        boolean alarm19 = Alarm.alarmExists(context, 19);
        if (!alarm12) {
            Alarm.addAlarm(context, 12);
        }

        if (!alarm19) {
            Alarm.addAlarm(context, 19);
        }

        if (!alarm12 && !alarm19) {
            ComponentName componentName = new ComponentName(context, AlarmBootReceiver.class);
            PackageManager packageManager = context.getPackageManager();

            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    private static boolean alarmExists(Context context, int id) {
        try {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.class.toString(), id);

            return (PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE) != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void addAlarm(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 50000);

        calendar.set(Calendar.HOUR_OF_DAY, id);
        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.class.toString(), id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                id, intent, Alarm.NOTIFICATION_FLAG);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String test = sdf.format(calendar.getTime());
        Log.e("Reminder time : ", test);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
