package cielo.ordermanager.sdk.sample;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public static String getAmmount(long value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100));
    }
}
