package com.cielo.ordermanager.sdk.sample;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.adapter.PaymentRecyclerViewAdapter;
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.CancellationRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
public class CancelPaymentActivity extends AppCompatActivity {
    private final String TAG = "CANCEL_PAYMENT";
    private Order order;
    private Payment payment;
    private OrderManager orderManager;

    @BindView(R.id.btn_cancel_payment)
    Button cancelButton;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cancel_payment);
        ButterKnife.bind(this);

        cancelButton.setEnabled(false);

        configSDK();

        this.order = (Order) getIntent().getSerializableExtra("SELECTED_ORDER");

        if (order != null && !order.getPayments().isEmpty()) {

            final List<Payment> paymentList = order.getPayments();

            recyclerView.setLayoutManager(new LinearLayoutManager(CancelPaymentActivity.this));
            recyclerView.setAdapter(
                    new PaymentRecyclerViewAdapter(paymentList));

            Log.i(TAG, "payments: " + paymentList);
            for (Payment pay : paymentList) {
                Log.i("Payment: ", pay.getExternalId() + " - " + pay.getAmount());
            }

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(CancelPaymentActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            payment = paymentList.get(position);
                            cancelButton.setEnabled(true);
                        }
                        @Override
                        public void onLongItemClick(View view, int position) {
                            // Implementação do long click, se necessário
                        }
                    })
            );

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dados para Cancelamento");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btn_cancel_payment)
    public void cancelPayment() {

        if (order != null && order.getPayments().size() > 0) {

            CancellationRequest request = new CancellationRequest.Builder()
                    .orderId(order.getId())
                    .authCode(payment.getAuthCode())
                    .cieloCode(payment.getCieloCode())
                    .value(payment.getAmount())
                    .ec(payment.getMerchantCode())
                    .build();

            orderManager.cancelOrder(request, new CancellationListener() {

                @Override
                public void onSuccess(Order cancelledOrder) {
                    Log.d(TAG, "ON SUCCESS");
                    cancelledOrder.cancel();
                    orderManager.updateOrder(cancelledOrder);

                    Toast.makeText(CancelPaymentActivity.this, "SUCESSO", Toast.LENGTH_SHORT).show();
                    order = cancelledOrder;
                    resetUI();
                }
                @Override
                public void onCancel() {
                    Log.d(TAG, "ON CANCEL");
                    Toast.makeText(CancelPaymentActivity.this, "CANCELADO", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
                @Override
                public void onError(PaymentError paymentError) {
                    Log.d(TAG, "ON ERROR");
                    Toast.makeText(CancelPaymentActivity.this, "ERRO", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            });
        }
    }
    private void resetUI() {
        payment = null;
        cancelButton.setEnabled(false);
    }
    private void configSDK() {
        Credentials credentials = new Credentials("rSAqNPGvFPJI", "XZevoUYKmkVr");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, null);
    }
}