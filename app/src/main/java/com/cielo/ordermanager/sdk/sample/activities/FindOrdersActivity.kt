package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.Order
import cielo.orders.domain.ResultOrders
import cielo.orders.domain.product.PrimaryProduct
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityFindOrdersBinding
import com.cielo.ordermanager.sdk.orders.OrderManagerController
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindOrdersActivity : AppCompatActivity() {

    private val orderManagerController: OrderManagerController = OrderManagerController.getInstance(this)
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var binding: ActivityFindOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configActionBar()
        configureListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Localizar Ordens"
        }
    }

    private fun configureListeners() {
        binding.actFindOrdersBtnRecoverOrder.setOnClickListener { this.recoverOrder() }
        binding.actFindOrdersBtnFindOrder.setOnClickListener { this.findOrder() }
        binding.actFindOrdersRbFindMethod.setOnCheckedChangeListener { rb: RadioGroup, resId: Int ->
            this.changeFinderFields(rb)
        }
    }

    private fun changeFinderFields(rb: RadioGroup) {
        val label: String
        var showExtraFields1 = false
        var showExtraFields2 = false
        when (rb.checkedRadioButtonId) {
            R.id.act_find_orders_rb_findOrderById -> {
                label = "Recuperar última Ordem"
                showExtraFields1 = true
            }

            R.id.act_find_orders_rb_findProducts -> label = "Recuperar produtos de pagamento"
            else -> throw IllegalStateException("Unexpected value: " + rb.checkedRadioButtonId)
        }
        clearEditFields()
        binding.actFindOrdersBtnRecoverOrder.text = label
        binding.actFindOrdersBtnRecoverOrder.visibility = View.VISIBLE
        binding.actFindOrdersBtnFindOrder.visibility = View.GONE
        setVisibility(R.id.act_find_orders_ll_extraFields1, showExtraFields1)
        setVisibility(R.id.act_find_orders_ll_extraFields2, showExtraFields2)
    }

    private fun clearEditFields() {
        binding.actFindOrdersEdtAmount.setText("")
        binding.actFindOrdersEdtAuthCode.setText("")
        binding.actFindOrdersEdtCieloCode.setText("")
        binding.actFindOrdersEdtOrderId.setText("")
        binding.actFindOrdersEdtResult.setText("")
    }

    private fun recoverOrder() {
        if (!orderManagerController.isServiceBound()) {
            showMessage("Serviço de ordem não está conectado")
            return
        }
        binding.actFindOrdersEdtResult.setText("")
        when (binding.actFindOrdersRbFindMethod.checkedRadioButtonId) {
            R.id.act_find_orders_rb_findOrderById -> {
                binding.actFindOrdersBtnFindOrder.visibility = View.VISIBLE
                recoverAnyOrder()
            }

            R.id.act_find_orders_rb_findProducts -> recoverPaymentTypes()
            else -> throw IllegalStateException("Unexpected value: " + binding.actFindOrdersRbFindMethod.checkedRadioButtonId)
        }
    }

    private fun recoverAnyOrder() {
        scope.launch {
            val resultOrders = orderManagerController.retrieveOrders(1, 0)
            withContext(Dispatchers.Main) {
                val selectedOrder = resultOrders?.results?.firstOrNull() ?: orderManagerController.createDraftOrder("Nova Ordem Criada!")

                if (selectedOrder != null) {
                    binding.actFindOrdersEdtOrderId.setText(selectedOrder.id)
                } else {
                    showMessage("Nenhuma Ordem foi encontrada!")
                }
            }
        }
    }

    private fun recoverPaymentTypes() {
        scope.launch {
            val primaryProductList: List<PrimaryProduct> = orderManagerController.getPrimaryProducts(applicationContext)
            withContext(Dispatchers.Main) {
                if (primaryProductList.isNotEmpty()) {
                    binding.actFindOrdersEdtResult.setText(toJson(primaryProductList))
                    showMessage("O Produtos foram obtidos com sucesso!")
                } else {
                    showMessage("Nenhum Produto foi encontrado!")

                }
            }
        }
    }

    private fun recoverOrderWithPayments() {
        scope.launch {
            val resultOrders: ResultOrders? = orderManagerController.retrieveOrders(1, 0)
            withContext(Dispatchers.Main) {
                resultOrders?.let { orders ->
                    if (orders.results.isNotEmpty()) {
                        val selectedOrder = findOrderWithPayment(orders)
                        selectedOrder?.let { order ->
                            val firstPayment = order.payments[0]
                            binding.actFindOrdersEdtAmount.setText(firstPayment.amount.toString())
                            binding.actFindOrdersEdtAuthCode.setText(firstPayment.authCode)
                            binding.actFindOrdersEdtCieloCode.setText(firstPayment.cieloCode)
                        } ?: run {
                            showMessage("Nenhuma Ordem com Pagamento foi encontrada!")
                        }
                    } else {
                        showMessage("Nenhuma Ordem com Pagamento foi encontrada!")
                    }
                } ?: run {
                    showMessage("Nenhuma Ordem com Pagamento foi encontrada!")
                }
            }
        }


    }

    private fun findOrderWithPayment(resultOrders: ResultOrders): Order? {
        for (order in resultOrders.results) {
            if (!order.payments.isEmpty()) {
                return order
            }
        }
        return null
    }

    private fun findOrder() {
        if (!orderManagerController.isServiceBound()) {
            showMessage("Serviço de ordem não está conectado")
            return
        }
        binding.actFindOrdersEdtResult.setText("")
        binding.actFindOrdersEdtResult.visibility = View.VISIBLE
        when (binding.actFindOrdersRbFindMethod.checkedRadioButtonId) {
            R.id.act_find_orders_rb_findOrderById -> findOrderById()
            else -> throw IllegalStateException("Unexpected value: " + binding.actFindOrdersRbFindMethod.checkedRadioButtonId)
        }
    }

    private fun findOrderById() {
        val orderId = getStringValue(binding.actFindOrdersEdtOrderId)

        scope.launch {
            val order: Order? = orderManagerController.findOrderById(orderId)

            withContext(Dispatchers.Main) {
                order?.let {
                    binding.actFindOrdersEdtResult.setText(toJson(it))
                    showMessage("A ordem foi localizada atráves do Id!")
                } ?: run {
                    showMessage("A ordem não foi localizada!")
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun getStringValue(edit: EditText?): String {
        return if (edit != null && edit.text != null) edit.text.toString() else ""
    }

    private fun setVisibility(resId: Int, isVisible: Boolean) {
        findViewById<View>(resId).visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun <T> toJson(`object`: T): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(`object`)
    }

    companion object {
        const val TAG: String = "FindOrdersActivity"
    }
}