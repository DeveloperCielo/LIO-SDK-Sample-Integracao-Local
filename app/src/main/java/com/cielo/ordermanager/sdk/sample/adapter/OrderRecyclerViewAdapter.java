package com.cielo.ordermanager.sdk.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cielo.ordermanager.sdk.R;
import com.cielo.ordermanager.sdk.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cielo.orders.domain.Item;
import cielo.orders.domain.Order;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderViewHolder> {
    private final List<Order> orderItemList;

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final TextView title1;
        final TextView title2;
        final TextView title3;
        final TextView subtitle1;
        final TextView subtitle2;
        final TextView subtitle3;
        final TextView subtitle4;

        OrderViewHolder(View itemView) {
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

    public OrderRecyclerViewAdapter(List<Order> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int index) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = orderItemList.get(position);
        if (order != null) {
            final List<Item> orderItems = order.getItems();
            holder.title1.setText(formatDate(order.getReleaseDate(), order.getCreatedAt()));
            holder.title2.setText(order.getStatus().name());
            holder.title3.setText(formatReference(order.getReference()));
            holder.subtitle1.setText(Util.getAmount(order.getPrice()));
            holder.subtitle2.setText(formatNumberOfItems(orderItems));
            holder.subtitle3.setText(formatOtherValues(order.getPaidAmount(), order.getPendingAmount()));
            holder.subtitle4.setText(String.format(Locale.getDefault(),"id: %s", order.getId()));
        }
    }

    private String formatNumberOfItems(List<Item> items) {
        return String.format(Locale.getDefault(), "Items [%d]", items.size());
    }

    private String formatDate(final Date releaseDate, final Date createdAt) {
        final Date orderDate = releaseDate != null ? releaseDate : createdAt;
        if (orderDate == null) {
            return "";
        }
        final SimpleDateFormat sf = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sf.format(orderDate);
    }

    private String formatOtherValues(long paidAmount, long pendingAmount) {
        return String.format(Locale.getDefault(),"Paid: %s / Pending: %s",
                Util.getAmount(paidAmount), Util.getAmount(pendingAmount));
    }

    private String formatReference(String reference) {
        return String.format(Locale.getDefault(),"Ref: %s", reference);
    }

    @Override
    public int getItemCount() {
        return (null != orderItemList ? orderItemList.size() : 0);
    }
}
