package com.cielo.ordermanager.sdk.sample.adapter;

import static com.cielo.ordermanager.sdk.utils.UtilKt.getAmount;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cielo.ordermanager.sdk.R;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import cielo.sdk.order.payment.Payment;

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.PaymentViewHolder> {
    private final List<Payment> paymentList;

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        final TextView title1;
        final TextView title2;
        final TextView title3;
        final TextView subtitle1;
        final TextView subtitle2;
        final TextView subtitle3;
        final TextView subtitle4;

        PaymentViewHolder(View itemView) {
            super(itemView);
            this.title1 = itemView.findViewById(R.id.title1);
            this.title2 = itemView.findViewById(R.id.title2);
            this.title3 = itemView.findViewById(R.id.title3);
            this.subtitle1 = itemView.findViewById(R.id.subtitle1);
            this.subtitle2 = itemView.findViewById(R.id.subtitle2);
            this.subtitle3 = itemView.findViewById(R.id.subtitle3);
            this.subtitle4 = itemView.findViewById(R.id.subtitle4);
        }
    }

    public PaymentRecyclerViewAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int index) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        if (paymentList.get(position) != null) {
            Payment payment = paymentList.get(position);
            Map<String, String> paymentFields = payment.getPaymentFields();
            String product = paymentFields.get("primaryProductName") + " - " + paymentFields.get("secondaryProductName") + " :: ";
            holder.title1.setText(payment.getCieloCode());
            holder.title2.setText(getAmount(payment.getAmount()));
            holder.title3.setText(formatInstallments(payment.getInstallments()));
            holder.subtitle1.setText("");
            holder.subtitle2.setText(product);
            holder.subtitle3.setText(String.format("Terminal: %s", payment.getTerminal()));
            holder.subtitle4.setText(String.format("AuthCode %s", payment.getAuthCode()));
        }
    }

    private String formatInstallments(final long installments) {
        return String.format(Locale.getDefault(), "Parcelas: %d", installments);
    }

    @Override
    public int getItemCount() {
        return (null != paymentList ? paymentList.size() : 0);
    }
}
