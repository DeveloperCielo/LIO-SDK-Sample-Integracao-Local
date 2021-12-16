package com.cielo.ordermanager.sdk.sample;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.cielo.ordermanager.sdk.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class QrCodeActivity extends AppCompatActivity {


    DecoratedBarcodeView barcodeView;
    TextView resultText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        barcodeView = findViewById(R.id.zxing_barcode_scanner);
        resultText = findViewById(R.id.text_result);
        barcodeView.decodeContinuous(callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pause();
    }


    private final CaptureManager capture = null;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) {
                return;
            }
            resultText.setText(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };


}
