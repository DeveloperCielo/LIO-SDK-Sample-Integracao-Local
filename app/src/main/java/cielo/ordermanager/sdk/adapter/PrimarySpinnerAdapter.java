package cielo.ordermanager.sdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cielo.ordermanager.sdk.sample.R;
import cielo.orders.domain.product.PrimaryProduct;

public class PrimarySpinnerAdapter extends ArrayAdapter<PrimaryProduct> {

    private Context context;
    private List<PrimaryProduct> values;

    public PrimarySpinnerAdapter(Context context, List<PrimaryProduct> values) {
        super(context, R.id.simple_title, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public PrimaryProduct getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getView(convertView);

        TextView label = (TextView) convertView.findViewById(R.id.simple_title);
        label.setText(values.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = getView(convertView);

        TextView label = (TextView) convertView.findViewById(R.id.simple_title);
        label.setText(values.get(position).getName());

        return label;
    }

    private View getView(View convertView) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item,null);
        }
        return convertView;
    }
}
