package com.cielo.ordermanager.sdk.utils

import cielo.orders.domain.product.PrimaryProduct
import cielo.sdk.order.payment.PaymentCode
import java.text.NumberFormat
import java.util.Locale

fun Long.getAmountToString(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format((this / 100.0f).toDouble())
}

fun Pair<String, String>.toPaymentCode(): PaymentCode? {
    return hashMapOf<String, PaymentCode?>().getOrPut("$first-$second") {
        PaymentCode.entries.firstOrNull {
            it.codePrimary == first && it.codeSecondary == second
        }
    }
}


