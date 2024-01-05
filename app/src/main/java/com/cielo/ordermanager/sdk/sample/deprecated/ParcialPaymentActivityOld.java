package com.cielo.ordermanager.sdk.sample.deprecated;


import android.util.Log;

import com.cielo.ordermanager.sdk.sample.BasePaymentActivity;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class ParcialPaymentActivityOld extends BasePaymentActivity {

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
                public void onPayment(Order paidOrder) {
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
                public void onError(PaymentError paymentError) {
                    Log.d(TAG, "ON ERROR");
                    resetState();
                }

            });
        }
    }
}