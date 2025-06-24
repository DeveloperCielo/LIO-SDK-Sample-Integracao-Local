package com.cielo.ordermanager.sdk.sample.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.DeviceModel
import cielo.orders.domain.Settings
import cielo.sdk.info.InfoManager
import com.cielo.ordermanager.sdk.databinding.ActivityLocalIntegrationBinding
import com.cielo.ordermanager.sdk.orders.OrderManagerController

class LocalIntegrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocalIntegrationBinding

    private val orderManagerController: OrderManagerController by lazy {
        OrderManagerController.getInstance(this)
    }
    private lateinit var infoManager: InfoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalIntegrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Integração Local (SDK)"

        infoManager = InfoManager()

        orderManagerController.fetchEnabledProducts()

        val settings: Settings = infoManager.getSettings(this)

        binding.merchantCodeTxt.text = settings.merchantCode
        binding.logicNumberTxt.text = settings.logicNumber

        val batteryLevel: Float = infoManager.getBatteryLevel(this)

        val deviceModel: DeviceModel = infoManager.getDeviceModel()

        Log.i("DeviceModel", "getDeviceModel - $deviceModel")

        when (deviceModel) {
            DeviceModel.LIO_V3 -> {
                binding.printSampleButton.visibility = View.VISIBLE
                binding.deviceModelText.text = "LIO V3 - Bateria: ${(batteryLevel * 100).toInt()}%"
            }

            else -> {
                binding.deviceModelText.text = "Versão Indefinido - Bateria: ${(batteryLevel * 100).toInt()}%"
            }
        }

        Log.i("TAG", "SERIAL: ${Build.SERIAL}")
        Log.i("TAG", "MODEL: ${Build.MODEL}")
        Log.i("TAG", "ID: ${Build.ID}")
        Log.i("TAG", "Manufacture: ${Build.MANUFACTURER}")
        Log.i("TAG", "brand: ${Build.BRAND}")
        Log.i("TAG", "type: ${Build.TYPE}")
        Log.i("TAG", "user: ${Build.USER}")
        Log.i("TAG", "BASE: ${Build.VERSION_CODES.BASE}")
        Log.i("TAG", "DEVICE: ${Build.DEVICE}")
        Log.i("TAG", "INCREMENTAL ${Build.VERSION.INCREMENTAL}")
        Log.i("TAG", "SDK  ${Build.VERSION.SDK}")
        Log.i("TAG", "BOARD: ${Build.BOARD}")
        Log.i("TAG", "BRAND ${Build.BRAND}")
        Log.i("TAG", "HOST ${Build.HOST}")
        Log.i("TAG", "FINGERPRINT: ${Build.FINGERPRINT}")
        Log.i("TAG", "Version Code: ${Build.VERSION.RELEASE}")
        Log.i("TAG", "Hardware: ${Build.HARDWARE}")

        binding.checkoutButton.setOnClickListener { openExample1() }
        binding.listOrdersButton.setOnClickListener { openExample2() }
        binding.cancelPaymentButton.setOnClickListener { openExample3() }
        binding.printSampleButton.setOnClickListener { openExample4() }
        binding.findOrdersButton.setOnClickListener { openExample5() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun openExample1() {
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
    }

    private fun openExample2() {
        val intent = Intent(this, ListOrdersActivity::class.java)
        startActivity(intent)
    }

    private fun openExample3() {
        val intent = Intent(this, CancellationOrderList::class.java)
        startActivity(intent)
    }

    private fun openExample4() {
        val intent = Intent(this, PrintSampleActivity::class.java)
        startActivity(intent)
    }

    private fun openExample5() {
        val intent = Intent(this, FindOrdersActivity::class.java)
        startActivity(intent)
    }
}