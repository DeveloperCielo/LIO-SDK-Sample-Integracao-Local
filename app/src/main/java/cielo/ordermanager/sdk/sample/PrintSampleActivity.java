package cielo.ordermanager.sdk.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cielo.orders.domain.Credentials;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.PrinterListener;


public class PrintSampleActivity extends AppCompatActivity {

    private OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_sample);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teste de Impressão");

        configSDK();
    }

    protected void configSDK() {
        Credentials credentials = new Credentials("cielo.sdk.sample", "cielo.sample");
        orderManager = new OrderManager(credentials, this);
    }

    @OnClick(R.id.print_sample_button)
    public void printSample() {

        //Imprime Imagem
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cielo);

        orderManager.printImage(bitmap, new HashMap<String, Integer>(), new PrinterListener() {
            @Override
            public void onWithoutPaper() {

            }

            @Override
            public void onPrintSuccess() {
                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ERROR", "ERROR");
            }

        });

        //Imprime Texto
        String textToPrint = getResources().getString(R.string.print_sample_text);
        orderManager.printText("\n \n" + textToPrint + "\n \n \n \n \n", new HashMap<String, Integer>(), new PrinterListener() {
            @Override
            public void onPrintSuccess() {
                Log.d("SUCCESS", "SUCCESS");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ERROR", "ERROR");
            }

            @Override
            public void onWithoutPaper() {

            }
        });
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
