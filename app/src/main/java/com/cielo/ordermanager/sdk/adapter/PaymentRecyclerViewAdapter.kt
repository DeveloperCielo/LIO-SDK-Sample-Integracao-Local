package com.cielo.ordermanager.sdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cielo.sdk.order.payment.Payment
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.sample.Util.getAmount

class PaymentRecyclerViewAdapter(private val paymentList: List<Payment?>?) : androidx.recyclerview.widget.RecyclerView.Adapter<PaymentRecyclerViewAdapter.OrderViewHolder>() {

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
        if (paymentList!![position] != null) {
            val payment = paymentList[position]
            val product = payment!!.paymentFields["primaryProductName"].toString() + " - " + payment.paymentFields["secondaryProductName"] + " :: "
            holder.title.text = payment.cieloCode + " | Parcelas: " + payment.installments
            holder.summary.text = product + "R$ " + getAmount(payment.amount)
        }
    }

    override fun getItemCount(): Int {
        return paymentList?.size ?: 0
    }

}
