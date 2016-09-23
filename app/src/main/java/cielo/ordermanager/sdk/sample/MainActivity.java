package cielo.ordermanager.sdk.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.sdk.order.Credentials;
import cielo.sdk.order.Environment;
import cielo.sdk.order.Item;
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

    @BindView(R.id.button_plus_new_item)
    RelativeLayout addItemButton;

    @BindView(R.id.button_minus_new_item)
    RelativeLayout removeItemButton;

    @BindView(R.id.item_quantity)
    TextView itemQuantity;

    @BindView(R.id.item_name)
    TextView itemName;

    @BindView(R.id.item_price)
    TextView itemPrice;

    @BindView(R.id.payment_button)
    Button paymentButton;

    @BindView(R.id.place_order_button)
    Button placeOrderButton;

    private Order order;

    private final long itemValue = 1200;
    private final String itemID = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        conifgSDK();
        configUi();
    }

    public void conifgSDK() {

        Map<String, Object> options = new HashMap<>();

        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, Environment.SANDBOX, options);
        orderManager.bind(this);
    }

    private void configUi() {

        itemName.setText("Item de exemplo");
        itemPrice.setText(getAmmount(itemValue));

        placeOrderButton.setEnabled(true);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null) {
                    order.addItem(itemID, "produto teste", itemValue, 1, UnitOfMeasure.EACH);
                    updatePaymentButton();
                } else {
                    showCreateOrderMEssage();
                }
            }
        });

        removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null && order.getItems().size() > 0) {
                    order.decreaseQuantity(itemID);
                    updatePaymentButton();
                } else {
                    showCreateOrderMEssage();
                }
            }
        });
    }

    private void showCreateOrderMEssage() {
        Toast.makeText(MainActivity.this, "Para adicionar itens é preciso criar uma ordem.", Toast.LENGTH_SHORT).show();
    }

    private String getAmmount(long value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100));
    }

    private void updatePaymentButton() {

        if (order != null) {
            int totalItens = order.getItems().size();
            itemQuantity.setText(String.valueOf(totalItens));

            boolean haveItens = totalItens > 0;
            paymentButton.setEnabled(haveItens);
            String valueText = getAmmount(itemValue * totalItens);
            paymentButton.setText((haveItens) ? "Pagar " + valueText : "Pagar");
        } else {

            paymentButton.setEnabled(false);
            paymentButton.setText("Pagar");
            itemQuantity.setText("0");
        }
    }

    @OnClick(R.id.place_order_button)
    public void placeOrder() {
        placeOrderButton.setEnabled(false);
        order = orderManager.createDraftOrder("Produto Teste");

    }

    private void resetState(){
        order = null;
        configUi();
        updatePaymentButton();
    }

    @OnClick(R.id.payment_button)
    public void makePayment() {

        if (order != null) {

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
                    resetState();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "ON CANCEL");
                    resetState();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "ON SUCCESS");
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
}
