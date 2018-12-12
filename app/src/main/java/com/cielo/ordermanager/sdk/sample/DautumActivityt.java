package com.cielo.ordermanager.sdk.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cielo.ordermanager.sdk.R;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


/**
 * Created by eduardovianna on 04/12/18.
 */

public class DautumActivityt extends AppCompatActivity {


    DecoratedBarcodeView barcodeView;
    TextView resultText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        barcodeView = findViewById(R.id.zxing_barcode_scanner);
        resultText = findViewById(R.id.text_result);


    }
}
