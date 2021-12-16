package com.cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.cielo.ordermanager.sdk.BuildConfig;
import com.cielo.ordermanager.sdk.R;

import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;


public class ListOrdersActivity extends AppCompatActivity {

    OrderManager orderManager;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    TextView txtEmptyValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);
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
        if (item.getItemId() == R.id.refresh) {
            listOrders();
            return true;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void configSDK() {
        Credentials credentials = new Credentials(BuildConfig.CLIENT_ID, BuildConfig.ACCESS_TOKEN);
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, new ServiceBindListener() {
            @Override
            public void onServiceBoundError(Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        String.format("Erro fazendo bind do serviço de ordem -> %s",
                                throwable.getMessage()), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceBound() {
                listOrders();
            }

            @Override
            public void onServiceUnbound() {
            }
        });
    }

    private void listOrders() {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);

            if (resultOrders != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(ListOrdersActivity.this));

                final List<Order> orderList = resultOrders.getResults();

                recyclerView.setAdapter(
                        new OrderRecyclerViewAdapter(orderList));

                String TAG = "ORDER_LIST";
                Log.i(TAG, "orders: " + orderList);
                for (Order or : orderList) {
                    Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
                }
            }
            txtEmptyValue.setVisibility(resultOrders == null || resultOrders.getResults().size() == 0 ?
                    View.VISIBLE : View.GONE);

        } catch (UnsupportedOperationException e) {
            Toast.makeText(ListOrdersActivity.this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO",
                    Toast.LENGTH_LONG).show();
        }
    }
}
