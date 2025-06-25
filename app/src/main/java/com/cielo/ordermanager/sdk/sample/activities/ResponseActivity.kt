package com.cielo.ordermanager.sdk.sample.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityResponseBinding
import com.cielo.ordermanager.sdk.sample.services.DeepLinkService
import com.cielo.ordermanager.sdk.utils.CancelRequest
import com.cielo.ordermanager.sdk.utils.Order
import com.cielo.ordermanager.sdk.utils.STOP_SERVICE
import com.cielo.ordermanager.sdk.utils.addText
import com.cielo.ordermanager.sdk.utils.deserializeQueryParameter
import com.cielo.ordermanager.sdk.utils.getBase64
import com.cielo.ordermanager.sdk.utils.startForegroundServiceAndLaunchDeepLink
import com.google.gson.Gson
import timber.log.Timber

class ResponseActivity : AppCompatActivity() {

    private val scheme by lazy { getString(R.string.intent_scheme) }
    private val responseHost by lazy { getString(R.string.intent_host) }
    private val callbackUrl by lazy { "$scheme://$responseHost" }

    private lateinit var binding: ActivityResponseBinding

    private val logView get() = binding.logCancelLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stopServiceIntent = Intent(this, DeepLinkService::class.java)
        stopServiceIntent.action = STOP_SERVICE
        startService(stopServiceIntent)

        intent.deserializeQueryParameter("response", Order::class.java) { json, order ->
            Timber.tag("ResponseActivity").d("Response: $json")
            logView.addText("JSON=$json")

            binding.dlCancelButton.setOnClickListener { cancelOrder(order) }
        }
    }

    private fun cancelOrder(order: Order) {
        try {
            if (order.payments.isNotEmpty()) {
                val cancelRequest = CancelRequest(
                    order.id,
                    "xxxxxxxxxxxxxxxxxxx",
                    "xxxxxxxxxxxxxxxx",
                    order.payments[0].cieloCode,
                    order.payments[0].authCode,
                    order.payments[0].merchantCode,
                    order.payments[0].amount
                )

                val json = Gson().toJson(cancelRequest).toString()
                val base64 = getBase64(json)
                val cancelUri = "lio://payment-reversal?request=$base64&urlCallback=$callbackUrl"
                startForegroundServiceAndLaunchDeepLink(this, cancelUri)
            } else {
                logView.addText("Não ha ordens para cancelar.")
            }
        } catch (e: Exception) {
            logView.addText("ResponseActivity ERROR = " + e.toString() + " " + e.message)
        }
    }
}
