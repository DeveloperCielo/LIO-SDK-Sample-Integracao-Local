package com.cielo.ordermanager.sdk.sample;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.RecyclerViewEmptySupport;
import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;

public class ListOrdersActivity extends AppCompatActivity {
    OrderManager orderManager;
    private final String TAG = "ORDER_LIST";
    private RecyclerViewEmptySupport recyclerView;
    private TextView txtEmptyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        recyclerView = findViewById(R.id.recycler_view);
        txtEmptyValue = findViewById(R.id.empty_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Listagem de Ordens");
        configSDK();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retrieveOrder:
                retrieveOrder();
                return true;
            case R.id.refreshOrders:
                refreshOrdersFromServer();
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    public void configSDK() {
        Credentials credentials = new Credentials("rSAqNPGvFPJI", "XZevoUYKmkVr");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBoundError(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Erro fazendo bind do serviço de ordem -> " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
                retrieveOrder();
            }

            @Override
            public void onServiceUnbound() {
                // Implementação do serviço desvinculado, se necessário
            }
        });
    }

    private void retrieveOrder() {
        try {
            txtEmptyValue.setText(R.string.empty_orders);
            recyclerView.setEmptyView(txtEmptyValue);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            final List<Order> orderList = retrieveOrdersFromRepository();
            recyclerView.setAdapter(new OrderRecyclerViewAdapter(orderList));
            for (Order or : orderList) {
                Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
            }
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show();
        }
    }

    private List<Order> retrieveOrdersFromRepository() {
        List<Order> allOrders = new ArrayList<>();
        ResultOrders resultOrders;
        for (int currentPage = 0; (resultOrders = orderManager.retrieveOrders(50, currentPage)) != null
                && currentPage < resultOrders.getTotalPages(); currentPage++) {
            allOrders.addAll(resultOrders.getResults());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allOrders.sort(Comparator.comparing(Order::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        }
        return allOrders;
    }

    private void refreshOrdersFromServer() {
        orderManager.refreshOrdersFromServer();
    }
}