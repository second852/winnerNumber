package com.whc.winnernumber.Control;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;




@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BootReceiverJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("BootReceiver", "BootReceiverJob");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        calendar.set(Calendar.HOUR,14);

        //確認今天是否重設過
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences sharedPreferences = getSharedPreferences("WinnerNumber", Context.MODE_PRIVATE);
        boolean todaySet = sharedPreferences.getBoolean(sf.format(new Date(calendar.getTimeInMillis())), false);
        if (todaySet) {
            return true;
        }

        //開講提醒
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 9 || month == 11) {
            if (day == 25) {
                Log.d("BootReceiver", "day");
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent;
                //版本處理
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    alarmIntent = new Intent(this, BootReceiverNC.class);
                }else {
                    alarmIntent = new Intent(this, BootReceiverNN.class);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+20, pendingIntent);
            }
        }
        sharedPreferences.edit().putBoolean(sf.format(new Date(calendar.getTimeInMillis())), true).apply();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
