package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityDeepLinkIntegrationBinding
import com.cielo.ordermanager.sdk.utils.OrderRequest
import com.cielo.ordermanager.sdk.utils.Item
import com.cielo.ordermanager.sdk.utils.Order
import com.cielo.ordermanager.sdk.utils.getBase64
import com.cielo.ordermanager.sdk.utils.startForegroundServiceAndLaunchDeepLink
import com.google.gson.Gson

class DeepLinkIntegrationActivity : AppCompatActivity() {

    private val scheme by lazy { getString(R.string.intent_scheme) }
    private val responseHost by lazy { getString(R.string.intent_host) }
    private val callbackUrl by lazy { "$scheme://$responseHost" }

    private lateinit var binding: ActivityDeepLinkIntegrationBinding

    private var order: Order? = null

    private val reference get() = "uriapp #" + (System.currentTimeMillis() / 1000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLinkIntegrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Integração via Deep Link"

        binding.dlCheckoutButton.setOnClickListener {
            makePayment()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun makePayment() {
        val price = (500L..1000L).random()
        val quantity = (1..5).random()
        val randomSku: Int = (1000..100000).random()
        val item = Item(
            sku = randomSku.toString(),
            name = "Produto de Teste",
            unitPrice = price,
            quantity = quantity,
            unitOfMeasure = "unidade"
        )
        val items = mutableListOf(item)

        val request = OrderRequest(
            "rSAqNPGvFPJI",
            "XZevoUYKmkVr",
            price * quantity,
            null,
            1,
            "eduardo.vianna@m4u.com.br",
            null,
            reference,
            items,
        )

        val json = Gson().toJson(request).toString()
        val base64 = getBase64(json)
        val checkoutUri = "lio://payment?request=$base64&urlCallback=$callbackUrl"
        startForegroundServiceAndLaunchDeepLink(this, checkoutUri)
    }
}