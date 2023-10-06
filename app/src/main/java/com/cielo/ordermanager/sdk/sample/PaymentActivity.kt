package com.cielo.ordermanager.sdk.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import cielo.orders.domain.CheckoutRequest
import cielo.orders.domain.Order
import cielo.sdk.order.payment.PaymentError
import cielo.sdk.order.payment.PaymentListener
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import com.cielo.ordermanager.sdk.adapter.PaymentCodeSpinnerAdapter
import com.cielo.ordermanager.sdk.sample.PaymentActivity
import kotlinx.android.synthetic.main.activity_payment.contentSecondary
import kotlinx.android.synthetic.main.activity_payment.email
import kotlinx.android.synthetic.main.activity_payment.installmentsSpinner
import kotlinx.android.synthetic.main.activity_payment.merchantCode
import kotlinx.android.synthetic.main.activity_payment.primarySpinner
import java.util.Arrays

class PaymentActivity : BasePaymentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configSDK(this::onServiceBound)

    }

    override fun configUi() {
        super.configUi()
        productName = "Teste - Pagamentos"
        try {
            paymentCodeAdapter = PaymentCodeSpinnerAdapter(this, R.layout.spinner_item)
            primarySpinner.adapter = paymentCodeAdapter
            primarySpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int,
                                            id: Long) {
                    paymentCode = paymentCodeAdapter?.getItem(position)
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }
            val installmentsArray = Arrays.asList(*resources
                    .getStringArray(R.array.installments_array))
            val installmentsAdapter: ArrayAdapter<String?> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                    installmentsArray)
            installmentsSpinner.adapter = installmentsAdapter
            contentSecondary.visibility = View.GONE
            installmentsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?,
                                            view: View, position: Int, id: Long) {
                    installments = installmentsAdapter.getItem(position)?.toInt() ?: 0
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }
        } catch (e: UnsupportedOperationException) {
            Toast.makeText(this, "Essa funcionalidade não está disponível nessa versão da Lio.",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun makePayment() {
        order?.let { order ->
            orderManager?.placeOrder(order)
            val ec = merchantCode.text.toString()
            val userEmail = email.text.toString()
            val requestBuilder = paymentCode?.let {
                CheckoutRequest.Builder()
                        .orderId(order.id)
                        .amount(itemValue)
                        .paymentCode(it)
                        .installments(installments)
            }
            if (ec != "") requestBuilder?.ec(ec)
            if (userEmail != "") requestBuilder?.email(userEmail)
            val request = requestBuilder?.build()
            orderManager?.checkoutOrder(request ?: return@let, object : PaymentListener {
                override fun onStart() {
                    Log.d(TAG, "ON START")
                }

                override fun onPayment(paidOrder: Order) {
                    Log.d(TAG, "ON PAYMENT")
                    this@PaymentActivity.order = paidOrder
                    order.markAsPaid()
                    orderManager?.updateOrder(order)
                    Toast.makeText(this@PaymentActivity, "Pagamento efetuado com sucesso.",
                            Toast.LENGTH_LONG).show()
                    resetState()
                }

                override fun onCancel() {
                    Log.d(TAG, "ON CANCEL")
                    Toast.makeText(this@PaymentActivity, "Pagamento cancelado.",
                            Toast.LENGTH_LONG).show()
                    resetState()
                }

                override fun onError(paymentError: PaymentError) {
                    Log.d(TAG, "ON ERROR")
                    Toast.makeText(this@PaymentActivity, "Houve um erro: ${paymentError.description}",
                            Toast.LENGTH_LONG).show()
                    resetState()
                }
            })
        }
    }

    private fun onServiceBound() {
        orderManager?.createDraftOrder("REFERENCIA DA ORDEM")
    }
}
