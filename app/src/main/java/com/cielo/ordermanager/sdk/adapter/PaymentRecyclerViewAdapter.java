package com.cielo.ordermanager.sdk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.sample.Util;
import cielo.sdk.order.payment.Payment;

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.OrderViewHolder> {

    private List<Payment> paymentList;

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView summary;

        OrderViewHolder(View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.title);
            this.summary = (TextView) itemView.findViewById(R.id.summary);
        }
    }

    public PaymentRecyclerViewAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int index) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        if (paymentList.get(position) != null) {
            Payment payment = paymentList.get(position);
            String product = payment.getPaymentFields().get("primaryProductName") + " - " + payment.getPaymentFields().get("secondaryProductName") + " :: ";

            holder.title.setText(payment.getCieloCode() + " | Parcelas: " + payment.getInstallments());
            holder.summary.setText(product + "R$ " + Util.getAmmount(payment.getAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return (null != paymentList ? paymentList.size() : 0);
    }
}
