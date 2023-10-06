package com.cielo.ordermanager.sdk.sample

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cielo.orders.domain.CancellationRequest
import cielo.orders.domain.Order
import cielo.sdk.order.cancellation.CancellationListener
import cielo.sdk.order.payment.Payment
import cielo.sdk.order.payment.PaymentError
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import com.cielo.ordermanager.sdk.adapter.PaymentRecyclerViewAdapter
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_cancel_payment.cancelButton
import kotlinx.android.synthetic.main.activity_cancel_payment.recyclerView

class CancelPaymentActivity : SdkPaymentActivity() {
    private var order: Order? = null
    private var payment: Payment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_payment)

        cancelButton?.run {
            isEnabled = false
            setOnClickListener {
                cancelPayment()
            }
        }
        configSDK()
        order = intent.getSerializableExtra("SELECTED_ORDER") as Order
        if (order != null && order?.payments?.isNotEmpty() == true) {
            val paymentList: List<Payment> = order!!.payments
            recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@CancelPaymentActivity)
            recyclerView?.adapter = PaymentRecyclerViewAdapter(paymentList)
            Log.i(TAG, "payments: $paymentList")
            for (pay in paymentList) {
                Log.i("Payment: ", pay.externalId + " - " + pay.amount)
            }
            recyclerView?.addOnItemTouchListener(
                    RecyclerItemClickListener(this@CancelPaymentActivity, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View?, position: Int) {
                            payment = paymentList[position]
                            cancelButton?.isEnabled = true
                        }

                        override fun onLongItemClick(view: View?, position: Int) {}
                    })
            )
        }
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cancel_data)
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

    private fun cancelPayment() {
        if (order != null && order!!.payments.size > 0) {
            val request = CancellationRequest.Builder()
                    .orderId(order!!.id)
                    .authCode(payment!!.authCode)
                    .cieloCode(payment!!.cieloCode)
                    .value(payment!!.amount)
                    .ec("0000000000000003")
                    .build()
            orderManager?.cancelOrder(request, object : CancellationListener {
                override fun onSuccess(canceledOrder: Order) {
                    Log.d(TAG, "ON SUCCESS")
                    canceledOrder.cancel()
                    orderManager?.updateOrder(canceledOrder)
                    Toast.makeText(this@CancelPaymentActivity, "SUCESSO", Toast.LENGTH_SHORT).show()
                    order = canceledOrder
                    resetUI()
                }

                override fun onCancel() {
                    Log.d(TAG, "ON CANCEL")
                    Toast.makeText(this@CancelPaymentActivity, "CANCELADO", Toast.LENGTH_SHORT).show()
                    resetUI()
                }

                override fun onError(paymentError: PaymentError) {
                    Log.d(TAG, "ON ERROR")
                    Toast.makeText(this@CancelPaymentActivity, "ERRO", Toast.LENGTH_SHORT).show()
                    resetUI()
                }
            })
        }
    }

    private fun resetUI() {
        payment = null
        cancelButton?.isEnabled = false
    }
}
