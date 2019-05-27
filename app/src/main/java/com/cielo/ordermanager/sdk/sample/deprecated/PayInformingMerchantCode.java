package com.cielo.ordermanager.sdk.sample.deprecated;


import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import com.cielo.ordermanager.sdk.R;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class PayInformingMerchantCode extends SelectPaymentMethodActivity {

    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - MerchantCde";

        List<String> installmentsArray = Arrays.asList(getResources()
                .getStringArray(R.array.installments_array));
        final ArrayAdapter<String> installmentsAdapter =
                new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                        installmentsArray);

        installmentsSpinner.setAdapter(installmentsAdapter);
        contentInstallments.setVisibility(View.VISIBLE);
        installmentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long id) {
                installments = Integer.parseInt(installmentsAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);

            try {

                orderManager.checkoutOrder(order.getId(), order.getPrice(), paymentCode, installments, "teste@email.com","0000000000000003", new PaymentListener() {

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
            } catch (UnsupportedOperationException e) {
                Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
