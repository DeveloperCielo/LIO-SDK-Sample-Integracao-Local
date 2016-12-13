package cielo.ordermanager.sdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cielo.orders.domain.product.PrimaryProduct;
import cielo.orders.domain.product.SecondaryProduct;

public class SecondarySpinnerAdapter extends ArrayAdapter<SecondaryProduct> {

    private Context context;
    private List<SecondaryProduct> values;

    public SecondarySpinnerAdapter(Context context, int textViewResourceId, List<SecondaryProduct> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public void setValues(List<SecondaryProduct> values) {
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public SecondaryProduct getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getName());

        return label;
    }
}
