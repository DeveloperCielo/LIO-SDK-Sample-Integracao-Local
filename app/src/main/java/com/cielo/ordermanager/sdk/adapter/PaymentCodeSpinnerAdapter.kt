package com.cielo.ordermanager.sdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cielo.sdk.order.payment.PaymentCode
import com.cielo.ordermanager.sdk.R

class PaymentCodeSpinnerAdapter(context: Context, textViewResourceId: Int) : ArrayAdapter<PaymentCode>(context, textViewResourceId, PaymentCode.values()) {
    private val values: List<PaymentCode> = listOf(*PaymentCode.values())
    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var tempProduct: PaymentCode? = null
    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): PaymentCode {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row = inflater.inflate(R.layout.spinner_item, parent, false)
        tempProduct = null
        tempProduct = values[position]
        val label = row.findViewById<View>(R.id.simple_title) as TextView
        label.text = tempProduct!!.name
        return row
    }

}
