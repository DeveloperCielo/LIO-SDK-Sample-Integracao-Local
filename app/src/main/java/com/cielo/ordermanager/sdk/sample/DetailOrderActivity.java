package com.cielo.ordermanager.sdk.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.cielo.ordermanager.sdk.R;

import cielo.orders.domain.Order;

public class DetailOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        getSupportActionBar().setTitle("Ordem Detalhada");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Order order = (Order) getIntent().getSerializableExtra("order");
        TextView idOrdem = findViewById(R.id.idOrdem);
        TextView transactionValue = findViewById(R.id.transactionValue);
        Button orderButton = findViewById(R.id.orderButton);

        idOrdem.setText(order.getId());
        transactionValue.setText("R$ " + String.valueOf(order.getPrice()));

        orderButton.setOnClickListener(view -> {
            finish();
        });
    }

}
