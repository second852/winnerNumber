package com.whc.winnernumber.Control;


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
    }
}
