package com.cielo.ordermanager.sdk.sample;


import android.support.annotation.NonNull;
import android.util.Log;

import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class ParcialPaymentActivity extends BasePaymentActivity {

    @Override
    protected void configUi() {
        super.configUi();
        productName = "Teste - Parcial";
    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);
            orderManager.checkoutOrder(order.getId(), new PaymentListener() {

                @Override
                public void onStart() {
                    Log.d(TAG, "ON START");
                }

                @Override
                public void onPayment(@NonNull Order paidOrder) {
                    Log.d(TAG, "ON PAYMENT");

                    order = paidOrder;
                    order.markAsPaid();
                    orderManager.updateOrder(order);

                    resetState();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "ON CANCEL");
                    resetState();
                }

                @Override
                public void onError(@NonNull PaymentError paymentError) {
                    Log.d(TAG, "ON ERROR");
                    resetState();
                }

            });
        }
    }
}