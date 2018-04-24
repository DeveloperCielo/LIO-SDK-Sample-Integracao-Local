package cielo.ordermanager.sdk.sample.deprecated;


import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cielo.ordermanager.sdk.R;
import cielo.ordermanager.sdk.adapter.PrimarySpinnerAdapter;
import cielo.ordermanager.sdk.adapter.SecondarySpinnerAdapter;
import cielo.ordermanager.sdk.sample.BasePaymentActivity;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class SelectPaymentMethodActivityOld extends BasePaymentActivity {


    @Override
    protected void configUi() {
        super.configUi();

        productName = "Teste - Direto";

        contentPrimary.setVisibility(View.VISIBLE);
        contentSecondary.setVisibility(View.VISIBLE);

        try {
            final List<PrimaryProduct> primaryProducts = orderManager.retrievePaymentType(this);
            primaryAdapter = new PrimarySpinnerAdapter(this, R.layout.spinner_item,
                    primaryProducts);
            primarySpinner.setAdapter(primaryAdapter);

            ArrayList<SecondaryProduct> secondaryProducts =
                    primaryProducts.get(0).getSecondaryProducts();

            secondaryAdapter =
                    new SecondarySpinnerAdapter(this, R.layout.spinner_item,
                            secondaryProducts);
            secondarySpinner.setAdapter(secondaryAdapter);

            primarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                           long id) {
                    primaryProduct = primaryAdapter.getItem(position);

                    ArrayList<SecondaryProduct> secondaryProducts =
                            primaryProduct.getSecondaryProducts();
                    secondaryProduct = secondaryProducts.get(0);
                    secondaryAdapter.setValues(secondaryProducts);
                    secondaryAdapter.notifyDataSetChanged();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });

            secondarySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    secondaryProduct = secondaryAdapter.getItem(position);
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

    @OnClick(R.id.payment_button)
    public void makePayment() {
        if (order != null) {

            orderManager.placeOrder(order);

            try {

                orderManager.checkoutOrder(order.getId(), order.getPrice(),
                        primaryProduct.getCode(),
                        secondaryProduct.getCode(), new PaymentListener() {

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

                                Toast.makeText(SelectPaymentMethodActivityOld.this, "Pagamento efetuado com sucesso.",
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
