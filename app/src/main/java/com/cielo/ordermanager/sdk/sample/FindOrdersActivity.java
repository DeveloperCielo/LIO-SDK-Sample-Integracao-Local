package com.cielo.ordermanager.sdk.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.cielo.ordermanager.sdk.R;
import com.google.gson.GsonBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cielo.orders.aidl.ParcelableOrder;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.Payment;
import timber.log.Timber;

@SuppressLint("LogNotTimber")
public class FindOrdersActivity extends AppCompatActivity {
    public final static String TAG = "FindOrdersActivity";

    public final static String CLIENT_ID = "xxxxxxxxxx";
    private final static String ACCESS_TOKEN = "xxxxxxxxxx";

    private OrderManager orderManager;
    private boolean isOrderManagerBinded;

    @BindView(R.id.act_find_orders_rb_findMethod)
    public RadioGroup findMethodRadioGroup;

    @BindView(R.id.act_find_orders_btn_recoverOrder)
    public AppCompatButton recoverOrderButton;

    @BindView(R.id.act_find_orders_edt_orderId)
    public AppCompatEditText orderIdEditText;

    @BindView(R.id.act_find_orders_edt_amount)
    public AppCompatEditText amountEditText;

    @BindView(R.id.act_find_orders_edt_authCode)
    public AppCompatEditText authCodeEditText;

    @BindView(R.id.act_find_orders_edt_cieloCode)
    public AppCompatEditText cieloCodeEditText;

    @BindView(R.id.act_find_orders_btn_findOrder)
    public AppCompatButton findOrderButton;

    @BindView(R.id.act_find_orders_edt_result)
    public AppCompatEditText resultOrderEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_orders);
        ButterKnife.bind(this);

        configActionBar();

        configOrderManager();

        configureListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderManager.unbind();
        orderManager = null;
    }

    private void configActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Localizar Ordens");
        }
    }

    protected void configOrderManager() {
        final Context context = this;
        final Credentials credentials = new Credentials(CLIENT_ID, ACCESS_TOKEN);
        orderManager = new OrderManager(credentials, context);
        orderManager.bind(context, new ServiceBindListener() {

            @Override
            public void onServiceBoundError(@NonNull Throwable throwable) {
                isOrderManagerBinded = false;
                Timber.tag("bind").w("error");
            }

            @Override
            public void onServiceBound() {
                isOrderManagerBinded = true;
                Timber.tag("bind").w("on");
            }

            @Override
            public void onServiceUnbound() {
                isOrderManagerBinded = false;
                Timber.tag("bind").w("unbound");
            }
        });
    }

    private void configureListeners() {
        recoverOrderButton.setOnClickListener(this::recoverOrder);
        findOrderButton.setOnClickListener(this::findOrder);
        findMethodRadioGroup.setOnCheckedChangeListener(this::changeFinderFields);
    }

    private void changeFinderFields(RadioGroup rb, int resId) {
        String label;
        boolean showExtraFields1 = false, showExtraFields2 = false;
        switch (rb.getCheckedRadioButtonId()) {
            case R.id.act_find_orders_rb_findOrderById:
                label = "Recuperar última Ordem";
                showExtraFields1 = true;
                break;
            case R.id.act_find_orders_rb_findOrderByPayment:
                label = "Recuperar última Ordem PAGA";
                showExtraFields2 = true;
                break;
            case R.id.act_find_orders_rb_findProducts:
                label = "Recuperar produtos de pagamento";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + rb.getCheckedRadioButtonId());
        }
        clearEditFields();
        recoverOrderButton.setText(label);
        recoverOrderButton.setVisibility(View.VISIBLE);
        findOrderButton.setVisibility(View.GONE);
        setVisibility(R.id.act_find_orders_ll_extraFields1, showExtraFields1);
        setVisibility(R.id.act_find_orders_ll_extraFields2, showExtraFields2);
    }

    private void clearEditFields() {
        amountEditText.setText("");
        authCodeEditText.setText("");
        cieloCodeEditText.setText("");
        orderIdEditText.setText("");
        resultOrderEditText.setText("");
    }

    private void recoverOrder(View v) {
        if (!isOrderManagerBinded) {
            showMessage("Serviço de ordem não está conectado");
            return;
        }
        resultOrderEditText.setText("");
        switch (findMethodRadioGroup.getCheckedRadioButtonId()) {
            case R.id.act_find_orders_rb_findOrderByPayment:
                findOrderButton.setVisibility(View.VISIBLE);
                recoverOrderWithPayments();
                break;
            case R.id.act_find_orders_rb_findOrderById:
                findOrderButton.setVisibility(View.VISIBLE);
                recoverAnyOrder();
                break;
            case R.id.act_find_orders_rb_findProducts:
                recoverPaymentTypes();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + findMethodRadioGroup.getCheckedRadioButtonId());
        }
    }

    private void recoverAnyOrder() {
        Order selectedOrder = null;
        try {
            final ResultOrders resultOrders = orderManager.retrieveOrders(1, 0);
            selectedOrder = resultOrders != null && !resultOrders.getResults().isEmpty()
                    ? resultOrders.getResults().get(0)
                    : orderManager.createDraftOrder("Nova Ordem Criada!");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (selectedOrder == null) {
            showMessage("Nenhuma Ordem foi encontrada!");
            return;
        }
        orderIdEditText.setText(selectedOrder.getId());
    }

    private void recoverPaymentTypes() {
        List<PrimaryProduct> primaryProductList = null;
        try {
            primaryProductList = orderManager.retrievePaymentType(this.getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (primaryProductList == null) {
            showMessage("Nenhum Produto foi encontrado!");
            return;
        }
        resultOrderEditText.setText(toJson(primaryProductList));
        showMessage("O Produtos foram obtidos com sucesso!");
    }

    private void recoverOrderWithPayments() {
        ResultOrders resultOrders = null;
        try {
            resultOrders = orderManager.retrieveOrders(1, 0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (resultOrders == null || resultOrders.getResults().isEmpty()) {
            showMessage("Nenhuma Ordem foi encontrada!");
            return;
        }
        final Order selectedOrder = findOrderWithPayment(resultOrders);
        if (selectedOrder == null) {
            showMessage("Nenhuma Ordem com Pagamento foi encontrada!");
            return;
        }
        final Payment firstPayment = selectedOrder.getPayments().get(0);
        amountEditText.setText(String.valueOf(firstPayment.getAmount()));
        authCodeEditText.setText(firstPayment.getAuthCode());
        cieloCodeEditText.setText(firstPayment.getCieloCode());
    }

    private Order findOrderWithPayment(ResultOrders resultOrders) {
        for (Order order : resultOrders.getResults()) {
            if (!order.getPayments().isEmpty()) {
                return order;
            }
        }
        return null;
    }

    private void findOrder(View v) {
        if (!isOrderManagerBinded) {
            showMessage("Serviço de ordem não está conectado");
            return;
        }
        resultOrderEditText.setText("");
        resultOrderEditText.setVisibility(View.VISIBLE);
        switch (findMethodRadioGroup.getCheckedRadioButtonId()) {
            case R.id.act_find_orders_rb_findOrderByPayment:
                findOrderByPayment();
                break;
            case R.id.act_find_orders_rb_findOrderById:
                findOrderById();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + findMethodRadioGroup.getCheckedRadioButtonId());
        }
    }

    private void findOrderById() {
        final String orderId = getStringValue(orderIdEditText);
        ParcelableOrder order = null;
        try {
            order = orderManager.retrieveOrderById(orderId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (order == null) {
            showMessage("A ordem não foi localizada!");
            return;
        }
        resultOrderEditText.setText(toJson(order));
        showMessage("A ordem foi localizada atráves do Id!");
    }

    @SuppressLint("LogNotTimber")
    private void findOrderByPayment() {
        final long amount = getStringValue(amountEditText).isEmpty() ?
                0L : Long.parseLong(getStringValue(amountEditText));
        final String authCode = getStringValue(authCodeEditText);
        final String cieloCode = getStringValue(cieloCodeEditText);
        ParcelableOrder order = null;
        try {
            order = orderManager.findOrder(amount, authCode, cieloCode);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (order == null) {
            showMessage("A ordem não foi localizada!!!");
            return;
        }
        resultOrderEditText.setText(toJson(order));
        showMessage("A ordem foi localizada com base nos dados de pagamento!!!");
    }

    private void showMessage(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private String getStringValue(final EditText edit) {
        return edit != null && edit.getText() != null ? edit.getText().toString() : "";
    }

    private void setVisibility(int resId, boolean isVisible) {
        findViewById(resId).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private <T> String toJson(T object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }
}