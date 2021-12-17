package com.cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.cielo.ordermanager.sdk.BuildConfig;
import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter;
import com.cielo.ordermanager.sdk.adapter.PrimarySpinnerAdapter;
import com.cielo.ordermanager.sdk.adapter.SecondarySpinnerAdapter;
import com.cielo.ordermanager.sdk.util.NumberUtils;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Item;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentCode;

public abstract class BasePaymentActivity extends AppCompatActivity {

    public OrderManager orderManager;
    public final String TAG = "PAYMENT_LISTENER";

    @BindView(R.id.button_plus_new_item)
    public RelativeLayout addItemButton;

    @BindView(R.id.button_minus_new_item)
    public RelativeLayout removeItemButton;

    @BindView(R.id.item_quantity)
    public TextView itemQuantity;

    @BindView(R.id.item_name)
    public TextView itemName;

    @BindView(R.id.item_price)
    public TextView itemPrice;

    @BindView(R.id.payment_button)
    public Button paymentButton;

    @BindView(R.id.place_order_button)
    public Button placeOrderButton;

    @BindView(R.id.primary)
    public Spinner primarySpinner;

    @BindView(R.id.secondary)
    public Spinner secondarySpinner;

    @BindView(R.id.installments)
    public Spinner installmentsSpinner;

    @BindView(R.id.content_installments)
    public View contentInstallments;

    @BindView(R.id.content_primary)
    public View contentPrimary;

    @BindView(R.id.content_secondary)
    public View contentSecondary;

    @BindView(R.id.et_merchant_code)
    public EditText merchantCode;

    @BindView(R.id.et_email)
    public EditText email;

    public PrimarySpinnerAdapter primaryAdapter;
    public SecondarySpinnerAdapter secondaryAdapter;
    public PaymentCodeSpinnerAdapter paymentCodeAdapter;

    public PrimaryProduct primaryProduct;
    public SecondaryProduct secondaryProduct;
    public PaymentCode paymentCode;

    public int installments;

    public Order order;

    final long itemValue = 100;
    public String sku = "0000";

    public String productName = "";
    public boolean orderManagerServiceBinded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        configSDK();
        configUi();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pagamento");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    protected void configSDK() {
        Credentials credentials = new Credentials(BuildConfig.CLIENT_ID, BuildConfig.ACCESS_TOKEN);
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
                orderManagerServiceBinded = false;

                Toast.makeText(getApplicationContext(),
                        String.format("Erro fazendo bind do serviço de ordem -> %s",
                                throwable.getMessage()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
                orderManagerServiceBinded = true;
                orderManager.createDraftOrder("REFERENCIA DA ORDEM");
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
            }
        });
    }

    protected void configUi() {

        sku = String.valueOf(1 + (Math.random()));

        itemName.setText("Item de exemplo");
        itemPrice.setText(NumberUtils.getAmmount(itemValue));

        placeOrderButton.setEnabled(true);

        productName = "produto teste";

        addItemButton.setOnClickListener(v -> {
            if (order != null) {
                order.addItem(sku, productName, itemValue, 1, "EACH");
                orderManager.updateOrder(order);
                updatePaymentButton();
            } else {
                showCreateOrderMessage();
            }
        });

        removeItemButton.setOnClickListener(v -> {
            if (order != null && order.getItems().size() > 0) {
                Item item = order.getItems().get(0);
                order.decreaseQuantity(item.getId());
                orderManager.updateOrder(order);
                updatePaymentButton();
            } else {
                showCreateOrderMessage();
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
            String valueText = NumberUtils.getAmmount(itemValue * totalItens);
            paymentButton.setText((haveItens) ? "Pagar " + valueText : "Pagar");
        } else {

            paymentButton.setEnabled(false);
            paymentButton.setText("Pagar");
            itemQuantity.setText("0");
        }
    }

    @OnClick(R.id.place_order_button)
    public void placeOrder() {
        if (!orderManagerServiceBinded) {
            Toast.makeText(this, "Serviço de ordem ainda não recebeu retorno do método bind().\n"
                    + "Verifique sua internet e tente novamente", Toast.LENGTH_LONG).show();
            return;
        }

        placeOrderButton.setEnabled(false);
        order = orderManager.createDraftOrder(productName);
    }

    protected void resetState() {
        order = null;
        configUi();
        updatePaymentButton();
    }

    @OnClick(R.id.payment_button)
    public abstract void makePayment();
}
