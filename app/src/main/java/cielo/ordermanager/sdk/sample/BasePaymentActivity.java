package cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.ordermanager.sdk.adapter.PrimarySpinnerAdapter;
import cielo.ordermanager.sdk.adapter.SecondarySpinnerAdapter;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.order.OrderManager;

public abstract class BasePaymentActivity extends AppCompatActivity {

    protected OrderManager orderManager;
    protected final String TAG = "PAYMENT_LISTENER";

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

    @BindView(R.id.primary)
    Spinner primarySpinner;

    @BindView(R.id.secondary)
    Spinner secondarySpinner;

    @BindView(R.id.installments)
    Spinner installmentsSpinner;

    protected PrimarySpinnerAdapter primaryAdapter;
    protected SecondarySpinnerAdapter secondaryAdapter;

    protected PrimaryProduct primaryProduct;
    protected SecondaryProduct secondaryProduct;

    protected int installments;

    protected Order order;

    protected final long itemValue = 1200;
    protected String itemID = "12345";

    protected String productName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        conifgSDK();
        configUi();
    }

    public void conifgSDK() {

        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, null);
    }

    protected void configUi() {

        itemID = String.valueOf(1 + (Math.random() * 99999));
        itemName.setText("Item de exemplo");
        itemPrice.setText(Util.getAmmount(itemValue));

        placeOrderButton.setEnabled(true);

        productName = "produto teste";

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null) {
                    order.addItem(itemID, productName, itemValue, 1, "EACH");
                    orderManager.updateOrder(order);
                    updatePaymentButton();
                } else {
                    showCreateOrderMessage();
                }
            }
        });

        removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null && order.getItems().size() > 0) {
                    order.decreaseQuantity(itemID);
                    orderManager.updateOrder(order);
                    updatePaymentButton();
                } else {
                    showCreateOrderMessage();
                }
            }
        });
    }

    protected void showCreateOrderMessage() {
        Toast.makeText(BasePaymentActivity.this, "Para adicionar itens é preciso criar uma ordem.", Toast.LENGTH_SHORT).show();
    }

    protected void updatePaymentButton() {

        if (order != null) {
            int totalItens = order.getItems().size();
            itemQuantity.setText(String.valueOf(totalItens));

            boolean haveItens = totalItens > 0;
            paymentButton.setEnabled(haveItens);
            String valueText = Util.getAmmount(itemValue * totalItens);
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
        order = orderManager.createDraftOrder(productName);
    }

    protected void resetState() {
        order = null;
        configUi();
        updatePaymentButton();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @OnClick(R.id.payment_button)
    public abstract void makePayment();
}
