package com.cielo.ordermanager.sdk.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.sample.deprecated.ParcialPaymentActivity;
import com.cielo.ordermanager.sdk.sample.deprecated.PayInformingMerchantCode;
import com.cielo.ordermanager.sdk.sample.deprecated.SelectPaymentMethodActivity;
import com.cielo.ordermanager.sdk.sample.deprecated.SuccessivePaymentActivity;
import com.cielo.ordermanager.sdk.sample.deprecated.TotalPaymentActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.DeviceModel;
import cielo.orders.domain.Settings;
import cielo.orders.domain.product.PrimaryProduct;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;

public class MainActivity extends Activity {

    @BindView(R.id.device_model_text)
    TextView deviceModelText;

    @BindView(R.id.merchant_code_txt)
    TextView merchantCodeText;

    @BindView(R.id.logic_number_txt)
    TextView logicNumberText;

    @BindView(R.id.print_sample_button)
    Button printerButton;

    protected OrderManager orderManager;
    protected InfoManager infoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        conifgSDK();
        List<PrimaryProduct> listaTeste = orderManager.retrievePaymentType(this);
        String lista = listaTeste.toString();
        Log.w("lista", lista);
        Settings settings = infoManager.getSettings(this);

        merchantCodeText.setText(settings.getMerchantCode());
        logicNumberText.setText(settings.getLogicNumber());
        Float batteryLevel = infoManager.getBatteryLevel(this);

        DeviceModel deviceModel = infoManager.getDeviceModel();

        Log.i("DeviceModel", "getDeviceModel - " + deviceModel);

        if (deviceModel == DeviceModel.LIO_V1) {
            printerButton.setVisibility(View.GONE);
            deviceModelText.setText("LIO V1 - Bateria: " + (int) (batteryLevel * 100) + "%");
        } else if (deviceModel == DeviceModel.LIO_V2){
            printerButton.setVisibility(View.VISIBLE);
            deviceModelText.setText("LIO V2- Bateria: " + (int) (batteryLevel * 100) + "%");
        } else if (deviceModel == DeviceModel.LIO_V3) {
            printerButton.setVisibility(View.VISIBLE);
            deviceModelText.setText("LIO V3- Bateria: " + (int) (batteryLevel * 100) + "%");
        } else {
            deviceModelText.setText("LIO Versão Indefinido - Bateria: " + (int) (batteryLevel * 100) + "%");
        }

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
        infoManager = new InfoManager();
        Credentials credentials = new Credentials( "rSAqNPGvFPJI", "XZevoUYKmkVr");
        orderManager = new OrderManager(credentials, this);
    }

    @OnClick(R.id.checkout_button)
    public void openExample1() {
        Intent intent = new Intent(this, PaymentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.list_orders_button)
    public void openExample2() {
        Intent intent = new Intent(this, ListOrdersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cancel_payment_button)
    public void openExample3() {
        Intent intent = new Intent(this, CancellationOrderList.class);
        startActivity(intent);
    }

    @OnClick(R.id.print_sample_button)
    public void openExample4() {
        Intent intent = new Intent(this, PrintSampleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.location_sample_button)
    public void openExample5() {
        Intent intent = new Intent(this, LocationSampleActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.qr_code_sample)
    public void openExample6() {
        Intent intent = new Intent(this, QrCodeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.find_orders_button)
    public void openExample7() {
        Intent intent = new Intent(this, FindOrdersActivity.class);
        startActivity(intent);
    }
}
