package com.cielo.ordermanager.sdk.sample

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.Credentials
import cielo.sdk.order.OrderManager
import cielo.sdk.order.ServiceBindListener
import com.cielo.ordermanager.sdk.R

open class SdkPaymentActivity : AppCompatActivity() {
    var orderManager: OrderManager? = null
    var orderManagerServiceBinded = false

    protected fun configSDK(onServiceBoundCallback: (() -> Unit)? = null,
                            onServiceBoundErrorCallback: (() -> Unit)? = null,
                            onServiceUnboundCallback: (() -> Unit)? = null) {
        val credentials = Credentials(getString(R.string.cielo_client_id),
                getString(R.string.cielo_access_token))
        orderManager = OrderManager(credentials, this)
        orderManager?.bind(this, object : ServiceBindListener {
            override fun onServiceBoundError(throwable: Throwable) {
                orderManagerServiceBinded = false
                Toast.makeText(applicationContext, String.format("Erro fazendo bind do serviço de ordem -> %s",
                        throwable.message), Toast.LENGTH_LONG).show()
                onServiceBoundErrorCallback?.invoke()
            }

            override fun onServiceBound() {
                orderManagerServiceBinded = true
                onServiceBoundCallback?.invoke()
            }

            override fun onServiceUnbound() {
                orderManagerServiceBinded = false
                onServiceUnboundCallback?.invoke()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        orderManager?.unbind()
    }
}
