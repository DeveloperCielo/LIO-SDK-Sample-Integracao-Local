package com.cielo.ordermanager.sdk.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cielo.ordermanager.sdk.R;
import cielo.sdk.order.payment.PaymentCode;

public class PaymentCodeSpinnerAdapter extends ArrayAdapter<PaymentCode> {

    private Context context;
    private List<PaymentCode> values;
    LayoutInflater inflater;
    PaymentCode tempProduct = null;

    public PaymentCodeSpinnerAdapter(Context context, int textViewResourceId, List<PaymentCode> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public PaymentCode getItem(int position) {
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
        label.setText(tempProduct.name());

        return row;
    }
}
