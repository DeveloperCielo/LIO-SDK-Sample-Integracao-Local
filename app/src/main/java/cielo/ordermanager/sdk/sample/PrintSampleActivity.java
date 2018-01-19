package cielo.ordermanager.sdk.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import cielo.printer.client.PrinterAttributes;
import cielo.sdk.printer.PrinterManager;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.ordermanager.sdk.R;
import cielo.orders.domain.Credentials;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.PrinterListener;
import cielo.sdk.printer.PrinterManager;
import java.util.List;
import java.util.Map;

public class PrintSampleActivity extends AppCompatActivity {

    private PrinterManager printerManager;

    HashMap<String, Integer> alignCenter =  new HashMap<>();
    HashMap<String, Integer> alignLeft =  new HashMap<>();
    HashMap<String, Integer> alignRight =  new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_sample);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teste de Impressão");

        setStyles();
        configSDK();
    }

    protected void configSDK() {
        printerManager = new PrinterManager(this);
    }

    @OnClick(R.id.print_sample_button)
    public void printSample() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cielo);
        printerManager.printImage(bitmap, alignCenter, new PrinterListener() {
            @Override
            public void onWithoutPaper() {}

            @Override
            public void onPrintSuccess() {
                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ERROR", "ERROR");
            }
        });

        String textToPrint = getResources().getString(R.string.print_sample_text);
        printerManager.printText( textToPrint, alignCenter, new PrinterListener() {
            @Override
            public void onPrintSuccess() {
                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ERROR", "ERROR");
            }

            @Override
            public void onWithoutPaper() {}
        });

        List<Map<String, Integer>> styles =  new ArrayList<>();
        styles.add(alignLeft);
        styles.add(alignCenter);
        styles.add(alignRight);

        printerManager.printMultipleColumnText(
            new String[] { "ALIGN LEFT", "ALIGN CENTER", "ALIGN RIGHT" },
            styles, new PrinterListener() {

            @Override
            public void onPrintSuccess() {
                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ERROR", "ERROR");
            }

            @Override
            public void onWithoutPaper() {}
        });

    }

    private void setStyles() {
        alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);
        alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
        alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);        alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 1);
        alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_TOP, 50);
        alignLeft.put(PrinterAttributes.KEY_MARGIN_BOTTOM, 50);
        alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 2);
        alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);
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
