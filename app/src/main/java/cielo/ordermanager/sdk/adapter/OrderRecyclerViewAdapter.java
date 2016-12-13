package cielo.ordermanager.sdk.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cielo.ordermanager.sdk.sample.R;
import cielo.orders.domain.Order;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderViewHolder> {
    private List<Order> orderItemList;

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView summary;

        OrderViewHolder(View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.title);
            this.summary = (TextView) itemView.findViewById(R.id.summary);
        }
    }

    public OrderRecyclerViewAdapter(List<Order> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int index) {
        View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.order_item, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        if (orderItemList.get(position) != null) {
            Order order = orderItemList.get(position);
            String product = "";

            if (!order.getItems().isEmpty()) {
                product = order.getItems().get(0).getName() + " - ";
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
