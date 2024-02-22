package com.cielo.ordermanager.sdk.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.cielo.ordermanager.sdk.R;
import cielo.orders.domain.Order;
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderViewHolder> {
    private List<Order> orderItemList;
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView summary;
        OrderViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.summary = itemView.findViewById(R.id.summary);
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
        if (orderItemList.get(position) != null) {
            Order order = orderItemList.get(position);
            String product = "";
            if (!order.getItems().isEmpty()) {
                product = order.getReference() + " - ";
            }
            if(order.getReleaseDate() != null)
                holder.title.setText(order.getReleaseDate().toString());
            holder.summary.setText(product + "R$ " +order.getPrice());
        }
    }
    @Override
    public int getItemCount() {
        return (null != orderItemList ? orderItemList.size() : 0);
    }
}