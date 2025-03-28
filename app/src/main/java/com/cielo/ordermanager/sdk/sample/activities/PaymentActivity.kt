package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.CheckoutRequest
import cielo.orders.domain.Order
import cielo.orders.domain.SubAcquirer
import cielo.sdk.order.payment.PaymentCode
import cielo.sdk.order.payment.PaymentError
import cielo.sdk.order.payment.PaymentListener
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityPaymentBinding
import com.cielo.ordermanager.sdk.orders.OrderManagerController
import com.cielo.ordermanager.sdk.sample.adapter.PaymentCodeSpinnerAdapter
import com.cielo.ordermanager.sdk.utils.getAmountToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentActivity : AppCompatActivity() {

    private val TAG = "PaymentActivity"

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var paymentCodeAdapter: PaymentCodeSpinnerAdapter
    private var paymentCode: PaymentCode? = null

    private var installments: Int = 0
    private var order: Order? = null
    private var itemValue: Long = 100
    private var sku: String = "0000"
    private var productName: String = ""
    private val scope = CoroutineScope(Dispatchers.Default)

    private var orderManagerController: OrderManagerController = OrderManagerController.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pagamento"

        configUi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun configUi() {
        productName = "Teste - Pagamentos"
        sku = (1 + Math.random()).toString()
        binding.itemName.text = "Item de exemplo"
        binding.itemPrice.text = itemValue.getAmountToString()
        binding.productItem.setOnClickListener { showSetItemValueDialog() }
        binding.etOrderReference.setText("")
        binding.etOrderReference.isEnabled = true
        binding.createOrderButton.isEnabled = true
        binding.placeOrderButton.isEnabled = false
        binding.cbSubacquirer.isChecked = false

        binding.orderData.visibility = View.VISIBLE
        binding.paymentData.visibility = View.GONE

        clearSubAcquirerFields()

        populateEnabledProductsSpinner()

        binding.buttonPlusNewItem.setOnClickListener {
            order?.let {
                it.addItem(sku, productName, itemValue, 1, "EACH")
                binding.placeOrderButton.isEnabled = true
                orderManagerController.updateOrder(it)
                updatePaymentButton()
            } ?: showCreateOrderMessage()
        }

        binding.buttonMinusNewItem.setOnClickListener {
            order?.let {
                it.removeAllItems()
                binding.placeOrderButton.isEnabled = false
                orderManagerController.updateOrder(it)
                updatePaymentButton()
            }
        }

        binding.placeOrderButton.setOnClickListener {
            scope.launch {
                order?.let { orderManagerController.placeOrder(it) }
                withContext(Dispatchers.Main) {
                    binding.paymentData.visibility = View.VISIBLE
                    binding.paymentButton.visibility = View.VISIBLE
                }
            }
        }

        binding.cbSubacquirer.setOnClickListener {
            binding.contentSubacquirer.visibility = if (binding.cbSubacquirer.isChecked) View.VISIBLE else View.GONE
        }

        binding.createOrderButton.setOnClickListener {
            createDraftOrder()
        }

        binding.paymentButton.setOnClickListener {
            scope.launch {
                makePayment()
            }
        }
    }

    private fun populateEnabledProductsSpinner() {
        scope.launch {
            val enabledProducts = orderManagerController.getEnabledProducts()
            withContext(Dispatchers.Main) {
                try {
                    paymentCodeAdapter = PaymentCodeSpinnerAdapter(this@PaymentActivity, R.layout.spinner_item, enabledProducts)
                    binding.primarySpinner.adapter = paymentCodeAdapter

                    binding.primarySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            paymentCode = paymentCodeAdapter.getItem(position)!!
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    val installmentsArray = resources.getStringArray(R.array.installments_array).toList()
                    val installmentsAdapter = ArrayAdapter(this@PaymentActivity, android.R.layout.simple_spinner_item, installmentsArray)
                    installmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    binding.installmentsSpinner.adapter = installmentsAdapter
                    binding.contentSecondary.visibility = View.GONE

                    binding.installmentsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            installments = installmentsArray[position].toInt()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@PaymentActivity, "Failed to load enabled products", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSetItemValueDialog() {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setTitle("Digite valor")
            .setPositiveButton("OK") { _, _ ->
                val number = input.text.toString()
                if (number.isNotEmpty()) {
                    binding.itemPrice.text = number.toLong().getAmountToString()
                    itemValue = number.toLong()
                    updatePaymentButton()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    private fun showCreateOrderMessage() {
        Toast.makeText(this, "Para adicionar itens é preciso criar uma ordem.", Toast.LENGTH_SHORT).show()
    }

    private fun updatePaymentButton() {
        order?.let {
            val totalItems = it.items.size
            binding.itemQuantity.text = totalItems.toString()
            val haveItems = totalItems > 0
            binding.paymentButton.isEnabled = haveItems
            val valueText = itemValue * totalItems
            binding.paymentButton.text = if (haveItems) "Pagar ${valueText.getAmountToString()}" else "Pagar"
        } ?: run {
            binding.paymentButton.isEnabled = false
            binding.paymentButton.text = "Pagar"
            binding.itemQuantity.text = "0"
        }
    }

    private fun createDraftOrder() {
        clearCurrentFocus()
        scope.launch {
            if (!orderManagerController.isServiceBound()) {
                withContext(Dispatchers.Main) {
                    toastMaker(
                        "Serviço de ordem ainda não recebeu retorno do método bind()." +
                                "\nVerifique sua internet e tente novamente"
                    )
                }
            } else {
                val additionalReference = getAdditionalReference()
                withContext(Dispatchers.Main) {
                    productName += " - $additionalReference"
                    binding.etOrderReference.setText(productName)
                    binding.etOrderReference.isEnabled = false
                    binding.createOrderButton.isEnabled = false
                    order = orderManagerController.createDraftOrder(productName)
                }
            }
        }
    }

    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun clearCurrentFocus() {
        currentFocus?.clearFocus()
    }

    private fun getAdditionalReference(): String {
        return binding.etOrderReference.text.toString()
    }

    private fun resetState() {
        order = null
        configUi()
        updatePaymentButton()
    }

    private suspend fun makePayment() {
        order?.let { order ->
            val ec = binding.etMerchantCode.text.toString()
            val userEmail = binding.etEmail.text.toString()

            val requestBuilder = CheckoutRequest.Builder()
                .orderId(order.id)
                .amount(order.pendingAmount())
                .installments(installments)

            paymentCode?.let { paymentCode -> requestBuilder.paymentCode(paymentCode) }

            if (ec.isNotEmpty()) requestBuilder.ec(ec)
            if (userEmail.isNotEmpty()) requestBuilder.email(userEmail)

            if (binding.cbSubacquirer.isChecked) {
                if (validateSubAcquirerFields()) {
                    requestBuilder.subAcquirer(
                        SubAcquirer(
                            binding.subSoftDescriptor.text.toString(),
                            binding.subTerminalId.text.toString(),
                            binding.subMerchantCode.text.toString(),
                            binding.subCity.text.toString(),
                            binding.subTelephone.text.toString(),
                            binding.subState.text.toString(),
                            binding.subPostalCode.text.toString(),
                            binding.subAddress.text.toString(),
                            binding.subId.text.toString(),
                            binding.subMcc.text.toString(),
                            binding.subCountry.text.toString(),
                            binding.subInformationType.text.toString(),
                            binding.subDocument.text.toString(),
                            binding.subIbge.text.toString()
                        )
                    )
                }
            }

            val request = requestBuilder.build()

            orderManagerController.checkout(request, object : PaymentListener {
                override fun onStart() {
                    // Implementação do início do pagamento
                }

                override fun onPayment(order: Order) {
                    orderManagerController.updateOrder(order)
                    Toast.makeText(this@PaymentActivity, "Pagamento efetuado com sucesso.", Toast.LENGTH_LONG).show()
                    resetState()
                }

                override fun onCancel() {
                    Log.d(TAG, "ON CANCEL")
                    resetState()
                }

                override fun onError(error: PaymentError) {
                    Log.d(TAG, "ON ERROR ${error.description}")
                    resetState()
                }
            })
        }
    }

    private fun validateSubAcquirerFields(): Boolean {
        return binding.subSoftDescriptor.text.toString().isNotEmpty() &&
                binding.subTerminalId.text.toString().isNotEmpty() &&
                binding.subMerchantCode.text.toString().isNotEmpty() &&
                binding.subCity.text.toString().isNotEmpty() &&
                binding.subTelephone.text.toString().isNotEmpty() &&
                binding.subState.text.toString().isNotEmpty() &&
                binding.subPostalCode.text.toString().isNotEmpty() &&
                binding.subAddress.text.toString().isNotEmpty() &&
                binding.subId.text.toString().isNotEmpty() &&
                binding.subMcc.text.toString().isNotEmpty() &&
                binding.subCountry.text.toString().isNotEmpty() &&
                binding.subInformationType.text.toString().isNotEmpty()
    }

    private fun clearSubAcquirerFields() {
        binding.contentSubacquirer.visibility = View.GONE
        binding.subSoftDescriptor.setText("")
        binding.subTerminalId.setText("")
        binding.subMerchantCode.setText("")
        binding.subCity.setText("")
        binding.subTelephone.setText("")
        binding.subState.setText("")
        binding.subPostalCode.setText("")
        binding.subAddress.setText("")
        binding.subId.setText("")
        binding.subMcc.setText("")
        binding.subCountry.setText("")
        binding.subInformationType.setText("")
    }

    override fun onResume() {
        resetState()
        super.onResume()
    }
}