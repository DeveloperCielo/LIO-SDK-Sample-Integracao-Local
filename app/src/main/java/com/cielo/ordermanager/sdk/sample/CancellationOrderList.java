package com.cielo.ordermanager.sdk.sample;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.RecyclerViewEmptySupport;
import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
public class CancellationOrderList extends AppCompatActivity {
    private final String TAG = "CANCELLATION_LIST";
    private RecyclerViewEmptySupport recyclerView;
    private TextView txtEmptyValue;
    private Order order;
    private OrderManager orderManager;
    @Override
    protected void onResume() {
        super.onResume();
        configSDK();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation_order_list);
        recyclerView = findViewById(R.id.recycler_view);
        txtEmptyValue = findViewById(R.id.empty_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cancelamento de Transação");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
    private void listOrders() {
        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            txtEmptyValue.setText(R.string.empty_orders_cancellation);
            recyclerView.setEmptyView(txtEmptyValue);
            final List<Order> orderList = retrieveOrdersFromRepository();
            recyclerView.setAdapter(new OrderRecyclerViewAdapter(orderList));
            for (Order or : orderList) {
                Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
            }
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            order = orderList.get(position);
                            if (!order.getPayments().isEmpty()) {
                                orderManager.unbind();
                                Intent intent = new Intent(CancellationOrderList.this, CancelPaymentActivity.class);
                                intent.putExtra("SELECTED_ORDER", order);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CancellationOrderList.this, "Não há pagamentos nessa ordem.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // Implementação do long click, se necessário
                        }
                    }));
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show();
        }
    }
    private void configSDK() {
        Credentials credentials = new Credentials("rSAqNPGvFPJI", "XZevoUYKmkVr");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBoundError(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Erro fazendo bind do serviço de ordem -> " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onServiceBound() {
                listOrders();
            }
            @Override
            public void onServiceUnbound() {
                // Implementação do serviço desvinculado, se necessário
            }
        });
    }

    private List<Order> retrieveOrdersFromRepository() {
        final List<Order> allOrders = new ArrayList<>();
        final ResultOrders resultOrders = orderManager.retrieveOrders(50, 0);
        if (resultOrders != null){
            allOrders.addAll(resultOrders.getResults());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                allOrders.sort(Comparator.comparing(Order::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            }
        }
        return allOrders;
    }
}