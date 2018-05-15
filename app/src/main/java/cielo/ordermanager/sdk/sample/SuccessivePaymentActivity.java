package cielo.ordermanager.sdk.sample;


import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cielo.ordermanager.sdk.R;
import cielo.orders.domain.Order;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class SuccessivePaymentActivity extends SelectPaymentMethodActivity {

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
        contentPrimary.setVisibility(View.GONE);
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

    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);

            try {

                orderManager.checkoutOrderStore(order.getId(), order.getPrice(), installments, new PaymentListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onPayment(Order paidOrder) {
                        Log.d(TAG, "ON PAYMENT");

                        order = paidOrder;
                        order.markAsPaid();
                        orderManager.updateOrder(order);

                        Toast.makeText(SuccessivePaymentActivity.this, "Pagamento efetuado com sucesso.",
                                Toast.LENGTH_LONG).show();

                        resetState();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SuccessivePaymentActivity.this, "Pagamento cancelado.",
                                Toast.LENGTH_LONG).show();
                        resetState();
                    }

                    @Override
                    public void onError(PaymentError paymentError) {
                        Toast.makeText(SuccessivePaymentActivity.this, "ERRO!",
                                Toast.LENGTH_LONG).show();
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
