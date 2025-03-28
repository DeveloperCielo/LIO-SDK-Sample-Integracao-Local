package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cielo.orders.domain.Order
import cielo.orders.domain.ResultOrders
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityOrdersBinding
import com.cielo.ordermanager.sdk.orders.OrderManagerController
import com.cielo.ordermanager.sdk.sample.adapter.OrderRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListOrdersActivity : AppCompatActivity() {

    private val orderManagerController: OrderManagerController = OrderManagerController.getInstance(this)
    private lateinit var binding: ActivityOrdersBinding
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private val TAG = "ORDER_LIST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrdersBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Listagem de Ordens"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.retrieveOrder -> {
                scope.launch {
                    retrieveOrder()
                }
                true
            }

            R.id.refreshOrders -> {
                scope.launch {
                    refreshOrdersFromServer()
                }
                true
            }

            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }

    private suspend fun retrieveOrder() {
        try {
            binding.emptyView.setText(R.string.empty_orders)
            binding.emptyView.setText(R.string.empty_orders)
            binding.recyclerView.setEmptyView(binding.emptyView)
            binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
            val orderList = retrieveOrdersFromRepository()
            binding.recyclerView.setAdapter(OrderRecyclerViewAdapter(orderList))
            for (or in orderList) {
                Log.i("Order: ", or.number + " - " + or.price)
            }
        } catch (e: UnsupportedOperationException) {
            Log.e(TAG, "Unsupported operation", e)
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

    private fun refreshOrdersFromServer() {
        orderManagerController.refreshOrdersFromServer()
    }
}