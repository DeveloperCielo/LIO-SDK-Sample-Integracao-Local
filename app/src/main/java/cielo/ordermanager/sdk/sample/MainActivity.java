package cielo.ordermanager.sdk.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.Credentials;
import cielo.sdk.order.Environment;
import cielo.sdk.order.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.UnitOfMeasure;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;
import cielo.sdk.order.payment.PaymentOptions;

public class MainActivity extends AppCompatActivity {

    OrderManager orderManager;
    private final String TAG = "PAYMENT_LISTENER";

    @BindView(R.id.value_text)
    EditText valueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        conifgSDK();
    }

    public void conifgSDK() {

        Map<String, Object> options = new HashMap<>();

        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, Environment.SANDBOX, options);
        orderManager.bind(this);
    }

    @OnClick(R.id.payment_button)
    public void testPayment() {
        Order order = orderManager.createDraftOrder("TESTE");

        String valueContent = valueText.getText().toString();

        if (!valueContent.isEmpty()) {

            long value = Long.parseLong(valueContent);

            if (order != null) {
                order.addItem("1234567890", "produto teste", value, 1, UnitOfMeasure.EACH);
                orderManager.placeOrder(order);

                Map<String, Object> options = new HashMap<>();
                orderManager.checkoutOrder(order, PaymentOptions.ALLOW_ONLY_CREDIT_PAYMENT.getValue(), new PaymentListener() {

                    @Override
                    public void onStart() {
                        Log.d(TAG, "ON START");
                    }

                    @Override
                    public void onPayment(@NonNull Payment payment) {
                        Log.d(TAG, "ON PAYMENT");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "ON CANCEL");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "ON SUCCESS");
                    }

                    @Override
                    public void onError(@NonNull PaymentError paymentError) {
                        Log.d(TAG, "ON ERROR");
                    }

                });
            }
        }else{
            Toast.makeText(this,"Insira um valor!", Toast.LENGTH_LONG).show();
        }

    }
}
