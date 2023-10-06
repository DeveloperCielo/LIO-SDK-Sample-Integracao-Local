package com.cielo.ordermanager.sdk.sample

import java.text.NumberFormat
import java.util.Locale

object Util {
    @JvmStatic
    fun getAmount(value: Long): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value / 100)
    }
}
