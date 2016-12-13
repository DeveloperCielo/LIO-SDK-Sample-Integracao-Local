package cielo.ordermanager.sdk.sample;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by eduardovianna on 12/12/16.
 */

public class Util {

    static String getAmmount(long value) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((value / 100));
    }
}
