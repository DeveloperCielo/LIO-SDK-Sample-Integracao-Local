package cielo.ordermanager.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.ordermanager.sdk.adapter.PaymentRecyclerViewAdapter;
import cielo.ordermanager.sdk.listener.RecyclerItemClickListener;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;

public class CancelPaymentActivity extends AppCompatActivity {

    private final String TAG = "CANCEL_PAYMENT";

    private Order order;
    private Payment payment;
    private OrderManager orderManager;
    private boolean isBound = false;

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
                        }
                    })
            );

        } else {


        }
    }

    @OnClick(R.id.btn_cancel_payment)
    public void cancelPayment() {

        if (order != null && order.getPayments().size() > 0) {

            orderManager.cancelOrder(this, order.getId(), payment, new CancellationListener() {
                @Override
                public void onSuccess(Order cancelledOrder) {
                    Log.d(TAG, "ON SUCCESS");
                    cancelledOrder.cancel();
                    orderManager.updateOrder(cancelledOrder);

                    order = cancelledOrder;
                    resetUI();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "ON CANCEL");
                    resetUI();
                }

                @Override
                public void onError(PaymentError paymentError) {
                    Log.d(TAG, "ON ERROR");
                    resetUI();
                }
            });
        } else {

        }
    }

    private void resetUI() {
        payment = null;
        cancelButton.setEnabled(false);
    }

    private void configSDK() {
        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBound() {
                isBound = true;
            }

            @Override
            public void onServiceUnbound() {
                isBound = false;
            }
        });
    }

}
