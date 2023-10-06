package com.cielo.ordermanager.sdk.sample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import cielo.orders.domain.Order
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import com.cielo.ordermanager.sdk.adapter.OrderRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_orders.recyclerView

class ListOrdersActivity : SdkPaymentActivity() {
    var txtEmptyValue: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = "LisTAGem de Ordens"
        }
        configSDK(this::listOrders)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                listOrders()
                true
            }
            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }

//    private fun configSDK() {
//        val credentials = Credentials(getString(R.string.cielo_client_id),
//                getString(R.string.cielo_access_token))
//        orderManager = OrderManager(credentials, this)
//        orderManager?.bind(this, object : ServiceBindListener {
//            override fun onServiceBoundError(throwable: Throwable) {
//                Toast.makeText(applicationContext, String.format("Erro fazendo bind do serviço de ordem -> %s",
//                        throwable.message), Toast.LENGTH_LONG).show()
//            }
//
//            override fun onServiceBound() {
//                listOrders()
//            }
//
//            override fun onServiceUnbound() {}
//        })
//    }

    private fun listOrders() {
        try {
            val resultOrders = orderManager?.retrieveOrders(200, 0)
            txtEmptyValue?.setText(R.string.empty_orders)
            //            recyclerView.setEmptyView(txtEmptyValue);
            if (resultOrders != null) {
                recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ListOrdersActivity)
                val orderList: List<Order> = resultOrders.results
                recyclerView?.adapter = OrderRecyclerViewAdapter(orderList)
                Log.i(TAG, "orders: $orderList")
                for (or in orderList) {
                    Log.i("Order: ", or.number + " - " + or.price)
                }
            }
        } catch (e: UnsupportedOperationException) {
            Toast.makeText(this@ListOrdersActivity, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO",
                    Toast.LENGTH_LONG).show()
        }
    }
}
