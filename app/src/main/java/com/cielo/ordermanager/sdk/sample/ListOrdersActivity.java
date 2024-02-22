package com.cielo.ordermanager.sdk.sample;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;

import java.util.List;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
public class ListOrdersActivity extends AppCompatActivity {
    OrderManager orderManager;
    private final String TAG = "ORDER_LIST";
    private RecyclerView recyclerView;
    private TextView txtEmptyValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        recyclerView = findViewById(R.id.recycler_view);
        txtEmptyValue = findViewById(R.id.empty_view);
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
            case R.id.refresh:
                listOrders();
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
    public void configSDK() {
        Credentials credentials = new Credentials("clientID", "accessToken");
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
    private void listOrders() {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);
            txtEmptyValue.setText(R.string.empty_orders);
            if (resultOrders != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                final List<Order> orderList = resultOrders.getResults();
                recyclerView.setAdapter(new OrderRecyclerViewAdapter(orderList));
                for (Order or : orderList) {
                    Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
                }
            }
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show();
        }
    }
}