package com.cielo.ordermanager.sdk.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.cielo.ordermanager.sdk.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.PrinterAttributes;
import cielo.sdk.info.InfoManager;
import cielo.sdk.order.PrinterListener;
import cielo.sdk.printer.PrinterManager;
import timber.log.Timber;

public class PrintSampleActivity extends AppCompatActivity {

    private PrinterManager printerManager;
    private PrinterListener printerListener;

    private HashMap<String, Integer> alignCenter = new HashMap<>();
    private HashMap<String, Integer> alignLeft = new HashMap<>();
    private HashMap<String, Integer> alignRight = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_sample);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teste de Impressão");

        setStyles();

        printerManager = new PrinterManager(this);
        printerListener = new PrinterListener() {
            @Override
            public void onWithoutPaper() {
                Log.d("PrintSampleActivity", "printer without paper");
            }

            @Override
            public void onPrintSuccess() {
                Log.d("PrintSampleActivity", "print success!");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PrintSampleActivity",
                        String.format("printer error -> %s", throwable.getMessage()));
            }
        };
    }

    @OnClick(R.id.print_image)
    public void printImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cielo);
        printerManager.printImage(bitmap, alignCenter, printerListener);
        Timber.tag("printerImage").w(printerListener.toString());
    }

    @OnClick(R.id.print_qrcode)
    public void printQrCode() {
        printerManager.printQrCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, printerListener);
    }

    @OnClick(R.id.print_barcode)
    public void printBarCode() {
        printerManager.printBarCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, 200, false, printerListener);
    }

    @OnClick(R.id.print_multi_columns)
    public void printMultiColumns() {
        String[] textsToPrint = new String[]{"Texto alinhado à esquerda.\n\n\n",
                "Texto centralizado\n\n\n", "Texto alinhado à direita\n\n\n"};
        List<Map<String, Integer>> styles = new ArrayList<>();
        styles.add(alignLeft);
        styles.add(alignCenter);
        styles.add(alignRight);

        printerManager.printMultipleColumnText(textsToPrint, styles, printerListener);
    }

    @OnClick(R.id.print_simple_text)
    public void printSimpleText() {
        String textToPrint = "TEXTO PARA IMPRIMIR";
        printerManager.printText(textToPrint, alignCenter, printerListener);
    }

    private void setStyles() {
        alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
        alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
        alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 30);

        alignCenter.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
        alignCenter.put(PrinterAttributes.KEY_TYPEFACE, 1);
        alignCenter.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        alignRight.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
        alignRight.put(PrinterAttributes.KEY_TYPEFACE, 2);
        alignRight.put(PrinterAttributes.KEY_TEXT_SIZE, 10);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
}
