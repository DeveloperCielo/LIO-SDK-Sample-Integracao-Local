package cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.ordermanager.sdk.RecyclerViewEmptySupport;
import cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;

public class ListOrdersActivity extends AppCompatActivity {

    OrderManager orderManager;
    private final String TAG = "ORDER_LIST";

    @BindView(R.id.recycler_view)
    RecyclerViewEmptySupport recyclerView;

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
        listOrders();
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
        Credentials credentials = new Credentials("cielo.sdk.sample", "cielo.sample");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, null);
    }

    private void listOrders() {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(200, 0);
            txtEmptyValue.setText(R.string.empty_orders);
            recyclerView.setEmptyView(txtEmptyValue);

            if (resultOrders != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(ListOrdersActivity.this));

                final List<Order> orderList = resultOrders.getResults();

                recyclerView.setAdapter(
                        new OrderRecyclerViewAdapter(orderList));

                Log.i(TAG, "orders: " + orderList);
                for (Order or : orderList) {
                    Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
                }
            }
        } catch (UnsupportedOperationException e) {
            Toast.makeText(ListOrdersActivity.this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO",
                    Toast.LENGTH_LONG).show();
        }
    }
}
