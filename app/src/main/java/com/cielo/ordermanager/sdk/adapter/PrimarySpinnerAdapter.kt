package com.cielo.ordermanager.sdk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cielo.orders.domain.product.PrimaryProduct
import com.cielo.ordermanager.sdk.R

class PrimarySpinnerAdapter(context: Context, textViewResourceId: Int,
                            private val values: List<PrimaryProduct>) : ArrayAdapter<PrimaryProduct>(context, textViewResourceId, values) {
    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var tempProduct: PrimaryProduct? = null
    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): PrimaryProduct {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row = inflater.inflate(R.layout.spinner_item, parent, false)
        tempProduct = null
        tempProduct = values[position]
        val label = row.findViewById<View>(R.id.simple_title) as TextView
        label.text = tempProduct!!.name
        return row
    }

}
