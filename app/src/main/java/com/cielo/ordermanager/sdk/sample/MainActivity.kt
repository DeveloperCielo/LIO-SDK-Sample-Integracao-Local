package com.cielo.ordermanager.sdk.sample

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import cielo.orders.domain.Credentials
import cielo.orders.domain.DeviceModel
import cielo.sdk.info.InfoManager
import cielo.sdk.order.OrderManager
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import kotlinx.android.synthetic.main.activity_main.cancelPaymentButton
import kotlinx.android.synthetic.main.activity_main.checkoutButton
import kotlinx.android.synthetic.main.activity_main.deviceModelText
import kotlinx.android.synthetic.main.activity_main.listOrdersButton
import kotlinx.android.synthetic.main.activity_main.locationSampleButton
import kotlinx.android.synthetic.main.activity_main.logicNumberText
import kotlinx.android.synthetic.main.activity_main.merchantCodeText
import kotlinx.android.synthetic.main.activity_main.printerButton
import kotlinx.android.synthetic.main.activity_main.qrCodeSample

class MainActivity : Activity() {

    private val orderManager by lazy {
        val credentials = Credentials("clientID", "accessToken")
        OrderManager(credentials, this)
    }
    private val infoManager by lazy { InfoManager() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
        printDeviceInfos()
    }

    private fun printDeviceInfos() {
        val settings = infoManager.getSettings(this)
        merchantCodeText.text = settings?.merchantCode
        logicNumberText.text = settings?.logicNumber
        val batteryLevel = infoManager.getBatteryLevel(this)
        val deviceModel = infoManager.getDeviceModel()

        if (deviceModel == DeviceModel.LIO_V1) {
            printerButton.visibility = View.GONE
            deviceModelText.text = "LIO V1 - Bateria: " + (batteryLevel * 100).toInt() + "%"
        } else {
            printerButton.visibility = View.VISIBLE
            deviceModelText.text = "LIO V2- Bateria: " + (batteryLevel * 100).toInt() + "%"
        }
        Log.i(TAG, "SERIAL: " + Build.SERIAL)
        Log.i(TAG, "MODEL: " + Build.MODEL)
        Log.i(TAG, "ID: " + Build.ID)
        Log.i(TAG, "Manufacture: " + Build.MANUFACTURER)
        Log.i(TAG, "brand: " + Build.BRAND)
        Log.i(TAG, "type: " + Build.TYPE)
        Log.i(TAG, "user: " + Build.USER)
        Log.i(TAG, "BASE: " + Build.VERSION_CODES.BASE)
        Log.i(TAG, "DEVICE: " + Build.DEVICE)
        Log.i(TAG, "INCREMENTAL " + Build.VERSION.INCREMENTAL)
        Log.i(TAG, "SDK  " + Build.VERSION.SDK)
        Log.i(TAG, "BOARD: " + Build.BOARD)
        Log.i(TAG, "BRAND " + Build.BRAND)
        Log.i(TAG, "HOST " + Build.HOST)
        Log.i(TAG, "FINGERPRINT: " + Build.FINGERPRINT)
        Log.i(TAG, "Version Code: " + Build.VERSION.RELEASE)
        Log.i(TAG, "Hardware: " + Build.HARDWARE)
    }


    private fun setupClickListeners() {
        checkoutButton.setOnClickListener { startActivity(Intent(this, PaymentActivity::class.java)) }
        listOrdersButton.setOnClickListener { startActivity(Intent(this, ListOrdersActivity::class.java)) }
        cancelPaymentButton.setOnClickListener { startActivity(Intent(this, CancellationOrderList::class.java)) }
        printerButton.setOnClickListener { startActivity(Intent(this, PrintSampleActivity::class.java)) }
        locationSampleButton.setOnClickListener { startActivity(Intent(this, LocationSampleActivity::class.java)) }
        qrCodeSample.setOnClickListener { startActivity(Intent(this, QrCodeActivity::class.java)) }
    }
}
