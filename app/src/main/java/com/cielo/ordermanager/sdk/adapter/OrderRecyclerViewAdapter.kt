package com.cielo.ordermanager.sdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cielo.orders.domain.Order
import com.cielo.ordermanager.sdk.R

class OrderRecyclerViewAdapter(private val orderItemList: List<Order?>?) : androidx.recyclerview.widget.RecyclerView.Adapter<OrderRecyclerViewAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var summary: TextView

        init {
            title = itemView.findViewById<View>(R.id.title) as TextView
            summary = itemView.findViewById<View>(R.id.summary) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        if (orderItemList!![position] != null) {
            val order = orderItemList[position]
            var product = ""
            if (!order!!.items.isEmpty()) {
                product = order.reference + " - "
            }
            if (order.releaseDate != null) holder.title.text = order.releaseDate.toString()
            holder.summary.text = product + "R$ " + order.price
        }
    }

    override fun getItemCount(): Int {
        return orderItemList?.size ?: 0
    }

}
