package com.whc.winnernumber.Control;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.winnernumber.R;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, new Donwload());
        fragmentTransaction.commit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setJob();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJob()
    {

        //判斷是否建立過
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled=false;
        for (JobInfo jobInfo : tm.getAllPendingJobs()) {
            if (jobInfo.getId() == 0) {
                hasBeenScheduled = true;
                break;
            }
        }
        if(hasBeenScheduled)
        {
            return;
        }

        ComponentName mServiceComponent = new ComponentName(this, BootReceiverJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        builder.setPeriodic(0);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());
    }
}
