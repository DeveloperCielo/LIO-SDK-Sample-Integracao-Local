package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cielo.orders.domain.CancellationRequest
import cielo.orders.domain.Order
import cielo.sdk.order.cancellation.CancellationListener
import cielo.sdk.order.payment.Payment
import cielo.sdk.order.payment.PaymentError
import com.cielo.ordermanager.sdk.databinding.ActivityCancelPaymentBinding
import com.cielo.ordermanager.sdk.listener.RecyclerItemClickListener
import com.cielo.ordermanager.sdk.orders.OrderManagerController
import com.cielo.ordermanager.sdk.sample.adapter.PaymentRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CancelPaymentActivity : AppCompatActivity() {

    private val TAG = "CANCEL_PAYMENT"
    private lateinit var order: Order
    private var payment: Payment? = null
    private val orderManagerController: OrderManagerController = OrderManagerController.getInstance(this)
    private lateinit var binding: ActivityCancelPaymentBinding
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelPayment.isEnabled = false


        order = intent.getSerializableExtra("SELECTED_ORDER") as Order

        if (order.payments.isNotEmpty()) {
            val paymentList = order.payments

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = PaymentRecyclerViewAdapter(paymentList)

            Log.i(TAG, "payments: $paymentList")

            for (pay in paymentList) {
                Log.i("Payment: ", "${pay.externalId} - ${pay.amount}")
            }

            binding.recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(this, binding.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        payment = paymentList[position]
                        binding.btnCancelPayment.isEnabled = true
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        // Implementação do long click, se necessário
                    }
                })
            )
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dados para Cancelamento"

        binding.btnCancelPayment.setOnClickListener {
            scope.launch {
                cancelPayment()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private suspend fun cancelPayment() {
        if (order.payments.isNotEmpty()) {
            payment?.let {
                val request = CancellationRequest.Builder()
                    .orderId(order.id)
                    .authCode(it.authCode)
                    .cieloCode(it.cieloCode)
                    .value(it.amount)
                    .ec(it.merchantCode)
                    .build()

                orderManagerController.cancelOrder(request, object : CancellationListener {
                    override fun onSuccess(cancelledOrder: Order) {
                        Log.d(TAG, "ON SUCCESS")
                        cancelledOrder.cancel()
                        orderManagerController.updateOrder(cancelledOrder)

                        Toast.makeText(this@CancelPaymentActivity, "SUCESSO", Toast.LENGTH_SHORT).show()
                        order = cancelledOrder
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
            } ?: run {
                Toast.makeText(this, "Pagamento não pode ser null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetUI() {
        payment = null
        binding.btnCancelPayment.isEnabled = false
    }
}