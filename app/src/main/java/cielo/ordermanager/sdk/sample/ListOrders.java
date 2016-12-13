package cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;

public class ListOrders extends AppCompatActivity {

    OrderManager orderManager;
    private final String TAG = "PAYMENT_LISTENER";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        ButterKnife.bind(this);

        configSDK();
    }

    @OnClick(R.id.btn_list_orders)
    public void listOrders() {
        try {
            ResultOrders resultOrders = orderManager.retrieveOrders(20, 0);

            recyclerView.setLayoutManager(new LinearLayoutManager(ListOrders.this));

            if (resultOrders != null) {
                recyclerView.setAdapter(
                        new OrderRecyclerViewAdapter(resultOrders.getResults()));

                Log.i(TAG, "orders: " + resultOrders.getResults());
                for (Order or : resultOrders.getResults()) {
                    Log.i("Order: ", or.getNumber() + " - " + or.getPrice());
                }
            }

        } catch (UnsupportedOperationException e) {
            Toast.makeText(ListOrders.this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show();
        }

    }

    public void configSDK() {
        Credentials credentials = new Credentials("1234", "1234");
        orderManager = new OrderManager(credentials, this);
        orderManager.bind(this, null);
    }
}
