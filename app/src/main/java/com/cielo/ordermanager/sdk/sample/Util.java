package com.cielo.ordermanager.sdk.sample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public static String getAmmount(long value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100));
    }

    public static boolean checkPermissions(String[] permissions, Context context) {

        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
