package cielo.ordermanager.sdk.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Settings;
import cielo.sdk.order.OrderManager;

public class MainActivity extends Activity {

    @BindView(R.id.merchant_code_txt)
    TextView merchantCodeText;

    @BindView(R.id.logic_number_txt)
    TextView logicNumberText;

    protected OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        conifgSDK();
        Settings settings = orderManager.getSettings(this);

        merchantCodeText.setText(settings.getMerchantCode());
        logicNumberText.setText(settings.getLogicNumber());

        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG", "MODEL: " + Build.MODEL);
        Log.i("TAG", "ID: " + Build.ID);
        Log.i("TAG", "Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG", "brand: " + Build.BRAND);
        Log.i("TAG", "type: " + Build.TYPE);
        Log.i("TAG", "user: " + Build.USER);
        Log.i("TAG", "BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG", "DEVICE: " + Build.DEVICE);
        Log.i("TAG", "INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG", "SDK  " + Build.VERSION.SDK);
        Log.i("TAG", "BOARD: " + Build.BOARD);
        Log.i("TAG", "BRAND " + Build.BRAND);
        Log.i("TAG", "HOST " + Build.HOST);
        Log.i("TAG", "FINGERPRINT: " + Build.FINGERPRINT);
        Log.i("TAG", "Version Code: " + Build.VERSION.RELEASE);
        Log.i("TAG", "Hardware: " + Build.HARDWARE);

    }

    protected void conifgSDK() {

        Credentials credentials = new Credentials("cielo.sdk.sample", "cielo.sample");
        orderManager = new OrderManager(credentials, this);
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

    @OnClick(R.id.payment_with_email_button)
    public void openExample5() {
        Intent intent = new Intent(this, PayInformingEmail.class);
        startActivity(intent);
    }

    @OnClick(R.id.list_orders_button)
    public void openExample6() {
        Intent intent = new Intent(this, ListOrdersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cancel_payment_button)
    public void openExample7() {
        Intent intent = new Intent(this, CancellationOrderList.class);
        startActivity(intent);
    }

    @OnClick(R.id.print_sample_button)
    public void openExample8() {
        Intent intent = new Intent(this, PrintSampleActivity.class);
        startActivity(intent);
    }

}
