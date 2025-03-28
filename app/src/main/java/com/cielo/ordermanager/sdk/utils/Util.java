package com.cielo.ordermanager.sdk.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public static String getAmount(long value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100.0f));
    }
}
