package com.cielo.ordermanager.sdk.orders

import android.content.Context
import android.util.Log
import cielo.orders.domain.CheckoutRequest
import cielo.orders.domain.Credentials
import cielo.sdk.order.OrderManager
import cielo.sdk.order.ServiceBindListener
import com.cielo.ordermanager.sdk.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class OrderManagerConnector(
    private val context: Context,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ServiceBindListener {
    private val TAG = "OrderManagerConnector"

    private val _om by lazy {
        OrderManager(Credentials(BuildConfig.CREDENTIALS_CLIENT_ID, BuildConfig.CREDENTIALS_ACCESS_TOKEN), context)
    }

    private val serviceBound = MutableStateFlow(false)
    private val orderManager = serviceBound.filter { it }.map { _om }

    private val bindMutex = Mutex()

    val serviceBoundState: Boolean
        get() = serviceBound.value

    private fun updateBoundStatus(bound: Boolean) {
        Log.d(TAG, "Emitting bound: $bound")
        serviceBound.value = bound
        Log.d(TAG, "Emit done")
    }

    suspend fun getOrderManager() = withContext(defaultDispatcher) {
        bindMutex.withLock {
            Log.d(TAG, "Calling SDK Bind")
            _om.bind(context, this@OrderManagerConnector)
            orderManager.first().also {
                Log.d(TAG, "Order manager instance obtained")
            }
        }
    }

    override fun onServiceBound() {
        Log.d(TAG, "Service bound")
        updateBoundStatus(true)
    }

    override fun onServiceUnbound() {
        Log.d(TAG, "Service unbound")
        updateBoundStatus(false)
    }

    override fun onServiceBoundError(throwable: Throwable) {
        Log.d(TAG, "Service onServiceBoundError: $throwable")
        onServiceUnbound()
    }

}