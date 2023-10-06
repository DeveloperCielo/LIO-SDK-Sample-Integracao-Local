package com.cielo.ordermanager.sdk.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cielo.orders.domain.Order
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_cancellation_order_list.recyclerView
import kotlinx.android.synthetic.main.activity_cancellation_order_list.txtEmptyValue

class CancellationOrderList : SdkPaymentActivity() {
    private var order: Order? = null
    override fun onResume() {
        super.onResume()
        configSDK(this::listOrders)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancellation_order_list)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cancel_transaction)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun listOrders() {
        try {
            val resultOrders = orderManager?.retrieveOrders(20, 0)
            recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@CancellationOrderList)
            txtEmptyValue?.setText(R.string.empty_orders_cancellation)
            recyclerView?.setEmptyView(txtEmptyValue)
            if (resultOrders != null) {
                val orderList: List<Order> = resultOrders.results
                recyclerView?.adapter = OrderRecyclerViewAdapter(orderList)
                Log.i(TAG, "orders: $orderList")
                for (or in orderList) {
                    Log.i(TAG, or.number + " - " + or.price)
                }
                recyclerView?.addOnItemTouchListener(
                        RecyclerItemClickListener(this@CancellationOrderList, recyclerView,
                                object : RecyclerItemClickListener.OnItemClickListener {
                                    override fun onItemClick(view: View?, position: Int) {
                                        order = orderList[position]
                                        if (order!!.payments.size > 0) {
                                            orderManager!!.unbind()
                                            val intent = Intent(this@CancellationOrderList,
                                                    CancelPaymentActivity::class.java)
                                            intent.putExtra("SELECTED_ORDER", order)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@CancellationOrderList,
                                                    "Não há pagamentos nessa ordem.",
                                                    Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onLongItemClick(view: View?, position: Int) {}
                                })
                )
            }
        } catch (e: UnsupportedOperationException) {
            Toast.makeText(this@CancellationOrderList, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO",
                    Toast.LENGTH_LONG).show()
        }
    }
}
