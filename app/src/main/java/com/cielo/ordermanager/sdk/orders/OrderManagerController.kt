package com.cielo.ordermanager.sdk.orders

import android.content.Context
import android.util.Log
import cielo.orders.domain.CancellationRequest
import cielo.orders.domain.CheckoutRequest
import cielo.orders.domain.Order
import cielo.orders.domain.ResultOrders
import cielo.orders.domain.product.PrimaryProduct
import cielo.sdk.order.cancellation.CancellationListener
import cielo.sdk.order.payment.PaymentCode
import cielo.sdk.order.payment.PaymentListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderManagerController private constructor(context: Context) {

    private val orderManagerConnector: OrderManagerConnector by lazy {
        OrderManagerConnector(context, Dispatchers.Default)
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private var enabledProducts: List<PaymentCode> = emptyList()

    companion object {
        @Volatile
        private var instance: OrderManagerController? = null
        fun getInstance(
            context: Context
        ): OrderManagerController {
            return instance ?: synchronized(this) {
                instance ?: OrderManagerController(context.applicationContext).also { instance = it }
            }
        }
    }

    private suspend fun getOrderManager() = orderManagerConnector.getOrderManager()

    fun isServiceBound() = orderManagerConnector.serviceBoundState

    fun refreshOrdersFromServer() =
        scope.launch {
            getOrderManager().refreshOrdersFromServer()
        }

    suspend fun getOrders(pageSize: Int, page: Int) = getOrderManager().retrieveOrders(pageSize, page)

    suspend fun createDraftOrder(orderReference: String): Order? {
        return getOrderManager().createDraftOrder(orderReference)
    }

    suspend fun checkout(checkoutRequest: CheckoutRequest, paymentListener: PaymentListener) {
        scope.launch {
            getOrderManager().checkoutOrder(checkoutRequest, paymentListener)
        }
    }

    suspend fun cancelOrder(cancellationRequest: CancellationRequest, cancellationListener: CancellationListener) {
        scope.launch {
            getOrderManager().cancelOrder(cancellationRequest = cancellationRequest, cancellationListener = cancellationListener)
        }
    }

    fun updateOrder(order: Order) {
        scope.launch {
            getOrderManager().updateOrder(order)
        }
    }

    fun placeOrder(order: Order) {
        scope.launch {
            getOrderManager().placeOrder(order)
        }
    }

    fun fetchEnabledProducts() {
        scope.launch {
            enabledProducts = getOrderManager()
                .retrieveEnabledProducts().also { Log.d("OrderManagerController", "SDK return: $it") }

        }
    }

    fun getEnabledProducts(): List<PaymentCode> {
        return enabledProducts
    }

    suspend fun getPrimaryProducts(context: Context): List<PrimaryProduct> {
        return getOrderManager().retrievePaymentType(context)
    }

    suspend fun retrieveOrders(pageSize: Int, page: Int): ResultOrders? {
        return getOrderManager().retrieveOrders(pageSize, page)
    }

    suspend fun findOrderById(orderId: String): Order? {
        return getOrderManager().findOrderById(orderId)
    }
}
