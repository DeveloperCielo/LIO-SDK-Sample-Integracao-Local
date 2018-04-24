package cielo.ordermanager.sdk.sample;


import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cielo.ordermanager.sdk.R;
import cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter;
import cielo.ordermanager.sdk.adapter.PrimarySpinnerAdapter;
import cielo.ordermanager.sdk.adapter.SecondarySpinnerAdapter;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class SelectPaymentMethodActivity extends BasePaymentActivity {

    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Códigos";

        contentPrimary.setVisibility(View.VISIBLE);
        contentSecondary.setVisibility(View.VISIBLE);

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

            contentSecondary.setVisibility(View.GONE);

        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);

            try {

                orderManager.checkoutOrder(order.getId(), order.getPrice(), paymentCode, new PaymentListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onPayment(Order paidOrder) {
                        Log.d(TAG, "ON PAYMENT");

                        order = paidOrder;
                        order.markAsPaid();
                        orderManager.updateOrder(order);

                        Toast.makeText(SelectPaymentMethodActivity.this, "Pagamento efetuado com sucesso.",
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
            } catch (UnsupportedOperationException e) {
                Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
