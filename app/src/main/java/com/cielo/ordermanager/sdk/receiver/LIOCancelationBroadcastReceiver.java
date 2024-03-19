package com.cielo.ordermanager.sdk.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cielo.orders.aidl.ParcelableOrder;
import cielo.orders.aidl.ParcelableTransaction;

public class LIOCancelationBroadcastReceiver extends BroadcastReceiver {

    String MY_ACCESS_KEY = "cielo.sdk.sample";
    String MY_SECRET_ACCESS_KEY = "cielo.sample";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CANCELAMENTO", "Cancelamento Recebido");

        ParcelableOrder order  = intent.getExtras().getParcelable("ORDER");
        ParcelableTransaction transaction  = intent.getExtras().getParcelable("TRANSACTION");

        if(MY_ACCESS_KEY.equalsIgnoreCase(order.getAccessKey())
                && MY_SECRET_ACCESS_KEY.equalsIgnoreCase(order.getSecretAccessKey())){
            Log.d("CANCELAMENTO", "A ordem pertence ao meu aplicativo");
        } else {
            Log.d("CANCELAMENTO", "A ordem não pertence ao meu aplicativo. Ignorar");
        }

    }
}
