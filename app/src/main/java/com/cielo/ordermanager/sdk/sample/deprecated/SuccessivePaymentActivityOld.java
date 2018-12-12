package com.cielo.ordermanager.sdk.sample.deprecated;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import com.cielo.ordermanager.sdk.R;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class SuccessivePaymentActivityOld extends SelectPaymentMethodActivityOld {

    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Parcelado";

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

                orderManager.checkoutOrder(order.getId(), order.getPrice(), PaymentCode.CREDITO_AVISTA, 0, "", "", new PaymentListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onPayment(Order order) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(PaymentError paymentError) {

                    }
                });
            } catch (UnsupportedOperationException e) {
                Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
