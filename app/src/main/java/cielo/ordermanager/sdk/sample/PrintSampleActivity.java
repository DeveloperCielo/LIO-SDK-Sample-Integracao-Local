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
    private PrinterListener printerListener;

    private HashMap<String, Integer> alignCenter =  new HashMap<>();
    private HashMap<String, Integer> alignLeft =  new HashMap<>();
    private HashMap<String, Integer> alignRight =  new HashMap<>();

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
                Log.d("PrintSampleActivity", "printer withou paper");
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
    }

    @OnClick(R.id.print_multi_columns)
    public void printMultiColumns() {
        String[] textsToPrint = new String[] { "Texto alinhado à esquerda.\n\n\n",
            "Texto centralizado\n\n\n", "Texto alinhado à direita\n\n\n"  };
        List<Map<String, Integer>> styles =  new ArrayList<>();
        styles.add(alignLeft);
        styles.add(alignCenter);
        styles.add(alignRight);

        printerManager.printMultipleColumnText(textsToPrint, styles, printerListener);
    }

    @OnClick(R.id.print_simple_text)
    public void printSimpleText() {
        String textToPrint = getResources().getString(R.string.print_sample_text);
        printerManager.printText( textToPrint, alignCenter, printerListener);
    }

    private void setStyles() {
        alignLeft.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_LEFT);
        alignLeft.put(PrinterAttributes.KEY_TYPEFACE, 0);
        alignLeft.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        alignCenter.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_CENTER);
        alignCenter.put(PrinterAttributes.KEY_TYPEFACE, 1);
        alignCenter.put(PrinterAttributes.KEY_TEXT_SIZE, 20);

        alignRight.put(PrinterAttributes.KEY_ALIGN, PrinterAttributes.VAL_ALIGN_RIGHT);
        alignRight.put(PrinterAttributes.KEY_TYPEFACE, 2);
        alignRight.put(PrinterAttributes.KEY_TEXT_SIZE, 20);
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
