package com.cielo.ordermanager.sdk;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.mapbox.mapboxsdk.Mapbox;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
