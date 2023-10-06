package com.cielo.ordermanager.sdk.sample

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cielo.orders.domain.Order
import cielo.sdk.order.payment.PaymentCode
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter
import kotlinx.android.synthetic.main.activity_payment.addItemButton
import kotlinx.android.synthetic.main.activity_payment.itemName
import kotlinx.android.synthetic.main.activity_payment.itemPrice
import kotlinx.android.synthetic.main.activity_payment.itemQuantity
import kotlinx.android.synthetic.main.activity_payment.paymentButton
import kotlinx.android.synthetic.main.activity_payment.placeOrderButton
import kotlinx.android.synthetic.main.activity_payment.removeItemButton

abstract class BasePaymentActivity : SdkPaymentActivity() {
    var paymentCodeAdapter: PaymentCodeSpinnerAdapter? = null
    var paymentCode: PaymentCode? = null
    var installments = 0
    var order: Order? = null
    val itemValue: Long = 100
    private var sku = "0000"

    var productName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        configUi()
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.payment)
        }
        setupClickListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }

    protected open fun configUi() {
        sku = (1 + Math.random()).toString()
        itemName.text = "Item de exemplo"
        itemPrice.text = Util.getAmount(itemValue)
        placeOrderButton.isEnabled = true
        productName = "produto teste"
        addItemButton.setOnClickListener { v: View? ->
            if (order != null) {
                order?.addItem(sku, productName, itemValue, 1, "EACH")
                orderManager?.updateOrder(order ?: return@setOnClickListener)
                updatePaymentButton()
            } else {
                showCreateOrderMessage()
            }
        }
        removeItemButton.setOnClickListener { v: View? ->
            if (order != null && order?.items?.size ?: 0 > 0) {
                val item = order?.items?.get(0)
                order?.decreaseQuantity(item?.getId() ?: return@setOnClickListener)
                orderManager?.updateOrder(order ?: return@setOnClickListener)
                updatePaymentButton()
            } else {
                showCreateOrderMessage()
            }
        }
    }

    private fun showCreateOrderMessage() {
        Toast.makeText(this@BasePaymentActivity, "Para adicionar itens é preciso criar uma ordem.", Toast.LENGTH_SHORT).show()
    }

    private fun updatePaymentButton() {
        if (order != null) {
            val totalItens = order?.items?.size ?: 0
            itemQuantity.text = totalItens.toString()
            val haveItens = totalItens > 0
            paymentButton.isEnabled = haveItens
            val valueText = Util.getAmount(itemValue * totalItens)
            paymentButton.text = if (haveItens) "Pagar $valueText" else "Pagar"
        } else {
            paymentButton.isEnabled = false
            paymentButton.text = "Pagar"
            itemQuantity.text = "0"
        }
    }

    private fun setupClickListeners() {
        placeOrderButton.setOnClickListener {
            if (!orderManagerServiceBinded) {
                Toast.makeText(this, "Serviço de ordem ainda não recebeu retorno do método bind().\n"
                        + "Verifique sua internet e tente novamente", Toast.LENGTH_LONG).show()
            }
            placeOrderButton.isEnabled = false
            order = orderManager?.createDraftOrder(productName)
        }
        paymentButton.setOnClickListener { makePayment() }
    }

    protected fun resetState() {
        order = null
        configUi()
        updatePaymentButton()
    }

    abstract fun makePayment()
}
