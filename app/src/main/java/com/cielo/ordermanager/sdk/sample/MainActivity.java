package com.cielo.ordermanager.sdk.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cielo.ordermanager.sdk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.DeviceModel;
import cielo.orders.domain.Settings;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.OrderManager;

public class MainActivity extends Activity {

    private static final int REQCODE_PERM_LOCATION = 101;
    private static final String[] LOCATION_PERMISSIONS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int REQCODE_PERM_CAMERA = 102;
    private static final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

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
        Settings settings = infoManager.getSettings(this);

        merchantCodeText.setText(settings.getMerchantCode());
        logicNumberText.setText(settings.getLogicNumber());
        Float batteryLevel = infoManager.getBatteryLevel(this);

        DeviceModel deviceModel = infoManager.getDeviceModel();
        if (deviceModel == DeviceModel.LIO_V1) {
            printerButton.setVisibility(View.GONE);
            deviceModelText.setText("LIO V1 - Bateria: " + (int) (batteryLevel * 100) + "%");
        } else {
            printerButton.setVisibility(View.VISIBLE);
            deviceModelText.setText("LIO V2- Bateria: " + (int) (batteryLevel * 100) + "%");
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
        Credentials credentials = new Credentials("bI8QDRCkhSKE", "na1bu1sW08FQ");
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

    @OnClick(R.id.payment_for_merchant_code)
    public void openExample5() {
        Intent intent = new Intent(this, PayInformingMerchantCode.class);
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

    @OnClick(R.id.location_sample_button)
    public void openExample9() {
        if (Util.checkPermissions(LOCATION_PERMISSIONS, this)) {
            Intent intent = new Intent(this, LocationSampleActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQCODE_PERM_LOCATION);
        }
    }

    @OnClick(R.id.qr_code_sample)
    public void openExample10() {
        if (Util.checkPermissions(CAMERA_PERMISSIONS, this)) {
            Intent intent = new Intent(this, QrCodeActivity.class);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, REQCODE_PERM_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQCODE_PERM_LOCATION:
                if (Util.checkPermissions(LOCATION_PERMISSIONS, this))
                    openExample9();
                else
                    Toast.makeText(this, getString(R.string.location_permission_necessary), Toast.LENGTH_LONG).show();
                break;
            case REQCODE_PERM_CAMERA:
                if (Util.checkPermissions(CAMERA_PERMISSIONS, this))
                    openExample10();
                else
                    Toast.makeText(this, getString(R.string.camera_permission_necessary), Toast.LENGTH_LONG).show();
                break;
        }

    }
}
