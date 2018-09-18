package com.whc.winnernumber.Control;



import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import com.whc.winnernumber.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;


public class BootReceiver extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("BootReceiver", "ACTION_DATE_CHANGED");
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            calendar.set(Calendar.HOUR,14);

            //確認今天是否重設過
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            SharedPreferences sharedPreferences = context.getSharedPreferences("WinnerNumber", Context.MODE_PRIVATE);
            boolean todaySet = sharedPreferences.getBoolean(sf.format(new Date(calendar.getTimeInMillis())), false);
            if (todaySet) {
                return ;
            }

            //開獎日提醒
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
                Log.d("BootReceiver", "month");
                if (day == 25) {
                    Log.d("BootReceiver", "day");
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(context, BootReceiverNN.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            //執行完存檔
            sharedPreferences.edit().putBoolean(sf.format(new Date(calendar.getTimeInMillis())), true).apply();
        }
    }
}


