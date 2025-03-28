package com.cielo.ordermanager.sdk.sample.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cielo.orders.domain.Order
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.RecyclerViewEmptySupport
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener
import com.cielo.ordermanager.sdk.orders.OrderManagerController
import com.cielo.ordermanager.sdk.sample.adapter.OrderRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CancellationOrderList : AppCompatActivity() {

    private val TAG = "CANCELLATION_LIST"
    private var orderManagerController: OrderManagerController = OrderManagerController.getInstance(this)
    private lateinit var recyclerView: RecyclerViewEmptySupport
    private lateinit var txtEmptyValue: TextView
    private var order: Order? = null
    private val scope = CoroutineScope(Dispatchers.Default)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancellation_order_list)
        recyclerView = findViewById(R.id.recycler_view)
        txtEmptyValue = findViewById(R.id.empty_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cancelamento de Transação"

        initListOrders()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun initListOrders() {
        scope.launch {
            val orderList = retrieveOrdersFromRepository()
            withContext(Dispatchers.Main) {
                listOrders(orderList)
            }
        }
    }


    private fun listOrders(orderList: List<Order>) {
        try {
            recyclerView.layoutManager = LinearLayoutManager(this)
            txtEmptyValue.setText(R.string.empty_orders_cancellation)
            recyclerView.setEmptyView(txtEmptyValue)
            recyclerView.adapter = OrderRecyclerViewAdapter(orderList)
            for (or in orderList) {
                Log.i("Order: ", "${or.number} - ${or.price}")
            }
            recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        order = orderList[position]
                        if (!order!!.payments.isEmpty()) {
                            val intent = Intent(this@CancellationOrderList, CancelPaymentActivity::class.java)
                            intent.putExtra("SELECTED_ORDER", order)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@CancellationOrderList, "Não há pagamentos nessa ordem.", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                    }
                })
            )
        } catch (e: UnsupportedOperationException) {
            Toast.makeText(this, "FUNCAO NAO SUPORTADA NESSA VERSAO DA LIO", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun retrieveOrdersFromRepository(): List<Order> {
        val resultOrders = orderManagerController.getOrders(15, 0)
        return resultOrders?.results?.sortedWith(
            compareBy<Order> { it.releaseDate }
                .thenBy { it.createdAt }
                .reversed()
        ) ?: emptyList()
    }


    override fun onResume() {
        initListOrders()
        super.onResume()
    }

}