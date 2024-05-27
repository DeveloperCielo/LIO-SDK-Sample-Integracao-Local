package com.cielo.ordermanager.sdk.sample;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter;

import java.util.Arrays;
import java.util.List;

import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class PaymentActivity extends BasePaymentActivity {



    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Pagamentos";



        try {
            paymentCodeAdapter = new PaymentCodeSpinnerAdapter(this, R.layout.spinner_item);
            primarySpinner.setAdapter(paymentCodeAdapter);

            primarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                           long id) {
                    paymentCode = paymentCodeAdapter.getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });

            List<String> installmentsArray = Arrays.asList(getResources()
                    .getStringArray(R.array.installments_array));
            final ArrayAdapter<String> installmentsAdapter =
                    new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                            installmentsArray);

            installmentsSpinner.setAdapter(installmentsAdapter);
            contentSecondary.setVisibility(View.GONE);

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

        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);
            String ec = merchantCode.getText().toString();
            String userEmail = email.getText().toString();

            CheckoutRequest.Builder requestBuilder = new CheckoutRequest.Builder()
                    .orderId(order.getId())
                    .amount(itemValue)
                    .paymentCode(paymentCode)
                    .installments(installments);

            if (!ec.equals(""))
                requestBuilder.ec(ec);

            if (!userEmail.equals(""))
                requestBuilder.email(userEmail);

            CheckoutRequest request = requestBuilder.build();

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
