package com.cielo.ordermanager.sdk.util;

import java.util.Locale;

public class NumberUtils {

    public static String getAmmount(long value) {
        return java.text.NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100));
    }
}
