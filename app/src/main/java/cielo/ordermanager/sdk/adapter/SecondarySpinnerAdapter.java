package cielo.ordermanager.sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cielo.ordermanager.sdk.R;
import cielo.orders.domain.product.SecondaryProduct;

public class SecondarySpinnerAdapter extends ArrayAdapter<SecondaryProduct> {

    private Context context;
    private List<SecondaryProduct> values;
    LayoutInflater inflater;
    SecondaryProduct tempProduct = null;

    public SecondarySpinnerAdapter(Context context, int textViewResourceId,
                                   List<SecondaryProduct> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner_item, parent, false);

        tempProduct = null;
        tempProduct = values.get(position);

        TextView label = (TextView) row.findViewById(R.id.simple_title);
        label.setText(tempProduct.getName());

        return row;
    }
}
