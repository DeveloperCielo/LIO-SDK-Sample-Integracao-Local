package com.cielo.ordermanager.sdk.sample.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityDeepLinkIntegrationBinding
import com.cielo.ordermanager.sdk.utils.Item
import com.cielo.ordermanager.sdk.utils.OrderRequest
import com.cielo.ordermanager.sdk.utils.PrintRequest
import com.cielo.ordermanager.sdk.utils.callToStopService
import com.cielo.ordermanager.sdk.utils.deserializeQueryParameter
import com.cielo.ordermanager.sdk.utils.getBase64
import com.cielo.ordermanager.sdk.utils.saveImage
import com.cielo.ordermanager.sdk.utils.startForegroundServiceAndLaunchDeepLink
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class DeepLinkIntegrationActivity : AppCompatActivity() {

    private val scheme by lazy { getString(R.string.intent_scheme) }
    private val responseHost by lazy { getString(R.string.intent_host) }
    private val callbackUrl by lazy { "$scheme://$responseHost" }
    private val callbackUrlSameActivity by lazy { "${getString(R.string.intent_scheme_same_activity)}://${getString(R.string.intent_host)}" }

    private lateinit var binding: ActivityDeepLinkIntegrationBinding

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

        binding.dlPrintTextButton.setOnClickListener {
            printSimpleText()
        }

        binding.dlPrintImageButton.setOnClickListener {
            printImage()
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
            "xxxxxxxxxxxxxxxxx",
            "xxxxxxxxxxxxxxxxxxxxxx",
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

    private fun printSimpleText() {
        val style = HashMap<String, Int>()
        val styles = ArrayList<Map<String, Int>>()
        styles.add(style)

        val value: Array<String> = arrayOf("Formatação do texto completo\n incluindo todas as linhas\n reduz o número de chamadas \n e melhora a performance \n\n\n")
        val request = PrintRequest("PRINT_TEXT", value, styles)
        val json: String = Gson().toJson(request).toString()
        val base64 = getBase64(json)

        val checkoutUri = "lio://print?request=$base64&urlCallback=$callbackUrlSameActivity"

        startForegroundServiceAndLaunchDeepLink(this, checkoutUri)
    }

    private fun printImage() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cielo)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteArrayOutputStream())

        val uri = saveImage(this, bitmap)
        val styles = ArrayList<Map<String, Int>>()
        styles.add(HashMap())
        val request = PrintRequest("PRINT_IMAGE", arrayOf(uri), styles)
        val json: String = Gson().toJson(request).toString()
        val base64 = getBase64(json)

        val checkoutUri = "lio://print?request=$base64&urlCallback=$callbackUrlSameActivity"
        startForegroundServiceAndLaunchDeepLink(this, checkoutUri)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        callToStopService(this)

        intent?.deserializeQueryParameter("response") { json ->
            Toast.makeText(this, "Intent recebida: $json", Toast.LENGTH_LONG).show()
        }
    }
}