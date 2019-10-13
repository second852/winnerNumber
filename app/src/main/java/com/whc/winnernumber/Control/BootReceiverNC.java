package com.whc.winnernumber.Control;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;


import androidx.annotation.RequiresApi;

import com.whc.winnernumber.R;

import java.util.Calendar;

public class BootReceiverNC extends BroadcastReceiver {


    public final String PRIMARY_CHANNEL = "發票兌獎";
    NotificationManager manager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        String title=" 統一發票";
        String message;
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;
        int year=calendar.get(Calendar.YEAR)-1911;
        if(month==1)
        {
            message=" 民國"+(year-1)+"年11-12月開獎";
        }
        else if(month==3)
        {
            message=" 民國"+year+"年1-2月開獎";
        } else if(month==5)
        {
            message=" 民國"+year+"年3-4月開獎";
        } else if(month==7)
        {
            message=" 民國"+year+"年5-6月開獎";
        } else if(month==9)
        {
            message=" 民國"+year+"年7-8月開獎";
        } else
        {
            message=" 民國"+year+"年9-10月開獎";
        }
        if(manager==null)
        {
            channel(context);
        }
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
        long[] vibrate = {0,100,200,300};
        Notification nb = new Notification.Builder(context.getApplicationContext(),PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.start)
                .setAutoCancel(true).setWhen(System.currentTimeMillis()).setVibrate(vibrate)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(appIntent).build();
        nb.flags = Notification.FLAG_SHOW_LIGHTS;
        manager.notify(0, nb);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void channel(Context ctx) {
        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        chan1.setLightColor(Color.GREEN);
        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan1);
    }

}
