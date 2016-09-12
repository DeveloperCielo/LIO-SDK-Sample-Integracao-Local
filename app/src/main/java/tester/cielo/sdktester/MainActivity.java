package tester.cielo.sdktester;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import cielo.sdk.order.Credentials;
import cielo.sdk.order.Environment;
import cielo.sdk.order.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.UnitOfMeasure;
import cielo.sdk.order.payment.Payment;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;
import cielo.sdk.order.payment.PaymentOptions;


public class MainActivity extends AppCompatActivity {

    OrderManager orderManager;

    private final String TAG = "PAYMENT_LISTENER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Map<String ,  Object> options =  new  HashMap<>() ;

        Credentials credentials = new Credentials( "1234" , "1234" );
        orderManager =  new OrderManager(this,credentials , Environment.SANDBOX, options) ;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Order order = orderManager.createDraftOrder();
                order.addItem("1234567890", "coca cola", 2, 1, UnitOfMeasure.EACH);
                orderManager.placeOrder(order);
                Map<String ,  Object> options =  new  HashMap<>() ;
                orderManager.checkoutOrder(order, PaymentOptions.ALLOW_ONLY_CREDIT_PAYMENT, new PaymentListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "ON START");
                    }

                    @Override
                    public void onPayment(Payment payment) {
                        Log.d(TAG, "ON PAYMENT");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "ON CANCEL");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "ON SUCCESS");
                    }

                    @Override
                    public void onError(PaymentError paymentError) {
                        Log.d(TAG, "ON ERROR");
                    }
                });

            }
        });
    }


    public void conifgSDK() {
        Map<String, Object> options = new HashMap<>();
        orderManager = new OrderManager(this, new Credentials("", ""), Environment.SANDBOX, options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
