package cielo.ordermanager.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.parcial_checkout_button)
    public void openExample1() {
        Intent intent = new Intent(this, ParcialPaymentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.checkout_button)
    public void openExample2() {
        Intent intent = new Intent(this, TotalPaymentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.payment_type_checkout_button)
    public void openExample3() {
        Intent intent = new Intent(this, SelectPaymentMethodActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.successive_payment_type_checkout_button)
    public void openExample4() {
        Intent intent = new Intent(this, SuccessivePaymentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.list_orders_button)
    public void openExample5() {
        Intent intent = new Intent(this, ListOrders.class);
        startActivity(intent);
    }

}
