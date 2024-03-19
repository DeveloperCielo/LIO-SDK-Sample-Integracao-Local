package com.cielo.ordermanager.sdk;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
    }
}
