package com.cielo.ordermanager.sdk.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter;
import com.cielo.ordermanager.sdk.adapter.PrimarySpinnerAdapter;
import com.cielo.ordermanager.sdk.adapter.SecondarySpinnerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Item;
import cielo.orders.domain.Order;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentCode;
import timber.log.Timber;

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
    public AppCompatSpinner primarySpinner;

    @BindView(R.id.secondary)
    public AppCompatSpinner secondarySpinner;

    @BindView(R.id.installments)
    public AppCompatSpinner installmentsSpinner;

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

    @BindView(R.id.et_order_reference)
    public EditText orderReference;

    @BindView(R.id.product_item)
    public LinearLayout productItem;

    public PrimarySpinnerAdapter primaryAdapter;
    public SecondarySpinnerAdapter secondaryAdapter;
    public PaymentCodeSpinnerAdapter paymentCodeAdapter;
    public PrimaryProduct primaryProduct;
    public SecondaryProduct secondaryProduct;
    public PaymentCode paymentCode;

    public int installments;
    public Order order;
    long itemValue = 100;
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
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
    protected void configSDK() {
        Credentials credentials = new Credentials("rSAqNPGvFPJI", "XZevoUYKmkVr");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
                orderManagerServiceBinded = false;
                Toast.makeText(getApplicationContext(),
                        String.format("Erro fazendo bind do serviço de ordem -> %s", throwable.getMessage()), Toast.LENGTH_LONG).show();
                Timber.tag("bind").w("error");
            }
            @Override
            public void onServiceBound() {
                orderManagerServiceBinded = true;
                if (!isCreateButtonVisible()){
                    order = orderManager.createDraftOrder("REFERENCIA DA ORDEM");
                }
                Timber.tag("bind").w("on");
            }

            @Override
            public void onServiceUnbound() {
                orderManagerServiceBinded = false;
                Timber.tag("bind").w("unbound");
            }

            private boolean isCreateButtonVisible() {
                final Button orderButton = findViewById(R.id.place_order_button);
                return orderButton != null &&
                        orderButton.isEnabled() &&
                        orderButton.getVisibility() == View.VISIBLE;
            }
        });
    }
    protected void configUi() {
        sku = String.valueOf(1 + (Math.random()));
        itemName.setText("Item de exemplo");
        itemPrice.setText(Util.getAmmount(itemValue));
        productItem.setOnClickListener(v->{
            showSetItemValueDiagog();
        });
        orderReference.setText("");
        orderReference.setEnabled(true);
        placeOrderButton.setEnabled(true);
        productName = "produto teste";
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null) {
                    order.addItem(sku, productName, itemValue, 1, "EACH");
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
                    Item item = order.getItems().get(0);
                    order.decreaseQuantity(item.getId());
                    orderManager.updateOrder(order);
                    updatePaymentButton();
                } else {
                    showCreateOrderMessage();
                }
            }
        });
    }

    private void showSetItemValueDiagog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setTitle("Digite valor")
                .setPositiveButton("OK", (dialog, id) -> {
                    String number = input.getText().toString();
                    if (!number.isEmpty()) {
                        itemPrice.setText(Util.getAmmount(Long.parseLong(number)));
                        itemValue = Long.parseLong(number);
                        updatePaymentButton();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    dialog.cancel();
                });

// Cria e mostra o AlertDialog
        builder.create().show();
    }

    protected void showCreateOrderMessage() {
        Toast.makeText(BasePaymentActivity.this, "Para adicionar itens é preciso criar uma ordem.", Toast.LENGTH_SHORT).show();
    }
    protected void updatePaymentButton() {
        if (order != null) {
            int totalItems = order.getItems().size();
            itemQuantity.setText(String.valueOf(totalItems));
            boolean haveItems = totalItems > 0;
            paymentButton.setEnabled(haveItems);
            String valueText = Util.getAmmount(itemValue * totalItems);
            paymentButton.setText((haveItems) ? "Pagar " + valueText : "Pagar");
        } else {
            paymentButton.setEnabled(false);
            paymentButton.setText("Pagar");
            itemQuantity.setText("0");
        }
    }

    @OnClick(R.id.place_order_button)
    public void placeOrder() {
        clearCurrentFocus();
        if (!orderManagerServiceBinded) {
            Toast.makeText(this, "Serviço de ordem ainda não recebeu retorno do método bind().\n" +
                    "Verifique sua internet e tente novamente", Toast.LENGTH_LONG).show();
            return;
        }
        productName += " - " + getAdditionalReference();
        orderReference.setText(productName);
        orderReference.setEnabled(false);
        placeOrderButton.setEnabled(false);
        order = orderManager.createDraftOrder(productName);
    }

    private void clearCurrentFocus() {
        final View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
        }
    }

    private String getAdditionalReference() {
        return orderReference != null && orderReference.getText() != null ?
                orderReference.getText().toString() : "";
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