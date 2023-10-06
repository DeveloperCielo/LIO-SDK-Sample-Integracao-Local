package com.cielo.ordermanager.sdk.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cielo.orders.aidl.ParcelableOrder
import cielo.orders.aidl.ParcelableTransaction

class LIOCancelationBroadcastReceiver : BroadcastReceiver() {
    var MY_ACCESS_KEY = "cielo.sdk.sample"
    var MY_SECRET_ACCESS_KEY = "cielo.sample"
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CANCELAMENTO", "Cancelamento Recebido")
        val order: ParcelableOrder = intent.extras.getParcelable("ORDER")
        val transaction: ParcelableTransaction = intent.extras.getParcelable("TRANSACTION")
        if (MY_ACCESS_KEY.equals(order.accessKey, ignoreCase = true)
                && MY_SECRET_ACCESS_KEY.equals(order.secretAccessKey, ignoreCase = true)) {
            Log.d("CANCELAMENTO", "A ordem pertence ao meu aplicativo")
        } else {
            Log.d("CANCELAMENTO", "A ordem não pertence ao meu aplicativo. Ignorar")
        }
    }
}
