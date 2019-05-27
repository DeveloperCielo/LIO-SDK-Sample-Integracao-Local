package com.cielo.ordermanager.sdk.sample;


import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class PaymentActivity extends BasePaymentActivity {

    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Pagamento";
    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);

            CheckoutRequest request = new CheckoutRequest.Builder()
                    .orderId(order.getId())
                    .amount(itemValue)
                    .paymentCode(PaymentCode.CREDITO_AVISTA)
                    .installments(0)
                    .email("teste@email.com")
                    .ec("0000000000000003")
                    .build();

            orderManager.checkoutOrder(request, new PaymentListener() {

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

                    Toast.makeText(PaymentActivity.this,"Pagamento efetuado com sucesso.",
                            Toast.LENGTH_LONG).show();

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

    @Override
    protected void onDestroy() {
        orderManager.unbind();
        super.onDestroy();
    }
}
