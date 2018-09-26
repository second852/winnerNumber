package com.whc.winnernumber.Control;


import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.whc.winnernumber.DataBase.PriceDB;
import com.whc.winnernumber.DataBase.WinnerDB;
import com.whc.winnernumber.R;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        WinnerDB winnerDB = new WinnerDB(this);
        PriceDB priceDB = new PriceDB(winnerDB.getReadableDatabase());
        if (priceDB.getAll().size() <= 0) {
            askPermissions();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.body, new PriceActivity());
            fragmentTransaction.commit();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setJob();
        } else {
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            startService(intent);
        }
        adjustFontScale(getResources().getConfiguration());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 87: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permisionOk();
                } else {

                    PermissionFragment permissionFragment = new PermissionFragment();
                    permissionFragment.setObject(MainActivity.this);
                    permissionFragment.show(getSupportFragmentManager(), "show");

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    87);
        } else {
            permisionOk();
        }
    }

    private void permisionOk() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.body, new Donwload());
        fragmentTransaction.commit();
    }


    public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJob() {

        //判斷是否建立過
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : tm.getAllPendingJobs()) {
            if (jobInfo.getId() == 0) {
                hasBeenScheduled = true;
                break;
            }
        }
        if (hasBeenScheduled) {
            return;
        }

        ComponentName mServiceComponent = new ComponentName(this, BootReceiverJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        builder.setPeriodic(1000 * 60 * 60);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        tm.schedule(builder.build());
    }
}
