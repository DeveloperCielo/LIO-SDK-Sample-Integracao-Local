package com.cielo.ordermanager.sdk.sample.deprecated;


import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.cielo.ordermanager.sdk.sample.BasePaymentActivity;

import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class TotalPaymentActivity extends BasePaymentActivity {

    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Valor";
    }


    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);
            orderManager.checkoutOrder(order.getId(), order.getPrice(), new PaymentListener() {

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

                    Toast.makeText(TotalPaymentActivity.this,"Pagamento efetuado com sucesso.",
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
