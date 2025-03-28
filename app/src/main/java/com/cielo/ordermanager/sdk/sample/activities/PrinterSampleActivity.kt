package com.cielo.ordermanager.sdk.sample.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.PrinterAttributes
import cielo.sdk.order.PrinterListener
import cielo.sdk.printer.PrinterManager
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.databinding.ActivityPrintSampleBinding
import timber.log.Timber

class PrintSampleActivity : AppCompatActivity() {

    private val printerManager: PrinterManager by lazy { PrinterManager(applicationContext) }
    private lateinit var printerListener: PrinterListener
    private lateinit var binding: ActivityPrintSampleBinding

    private val alignCenter = hashMapOf<String, Int>()
    private val alignLeft = hashMapOf<String, Int>()
    private val alignRight = hashMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Teste de Impressão"

        setStyles()


        printerListener = object : PrinterListener {
            override fun onWithoutPaper() {
                Log.d("PrintSampleActivity", "printer without paper")
            }

            override fun onPrintSuccess() {
                Log.d("PrintSampleActivity", "print success!")
            }

            override fun onError(throwable: Throwable?) {
                Log.d("PrintSampleActivity", "printer error -> ${throwable?.message}")
            }
        }

        binding.printImage.setOnClickListener { printImage() }
        binding.printQrcode.setOnClickListener { printQrCode() }
        binding.printBarcode.setOnClickListener { printBarCode() }
        binding.printMultiColumns.setOnClickListener { printMultiColumns() }
        binding.printSimpleText.setOnClickListener { printSimpleText() }
    }

    private fun printImage() {
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.cielo)
        printerManager.printImage(bitmap, alignCenter, printerListener)
        Timber.tag("printerImage").w(printerListener.toString())
    }

    private fun printQrCode() {
        printerManager.printQrCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, printerListener)
    }

    private fun printBarCode() {
        printerManager.printBarCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, 200, false, printerListener)
    }

    private fun printMultiColumns() {
        val textsToPrint = arrayOf("Texto alinhado à esquerda.\n\n\n", "Texto centralizado\n\n\n", "Texto alinhado à direita\n\n\n")
        val styles = listOf(alignLeft, alignCenter, alignRight)
        printerManager.printMultipleColumnText(textsToPrint, styles, printerListener)
    }

    private fun printSimpleText() {
        val textToPrint = "TEXTO PARA IMPRIMIR"
        printerManager.printText(textToPrint, alignCenter, printerListener)
    }

    private fun setStyles() {
        alignLeft[PrinterAttributes.KEY_ALIGN] = PrinterAttributes.VAL_ALIGN_LEFT
        alignLeft[PrinterAttributes.KEY_TYPEFACE] = 0
        alignLeft[PrinterAttributes.KEY_TEXT_SIZE] = 30

        alignCenter[PrinterAttributes.KEY_ALIGN] = PrinterAttributes.VAL_ALIGN_CENTER
        alignCenter[PrinterAttributes.KEY_TYPEFACE] = 1
        alignCenter[PrinterAttributes.KEY_TEXT_SIZE] = 20

        alignRight[PrinterAttributes.KEY_ALIGN] = PrinterAttributes.VAL_ALIGN_RIGHT
        alignRight[PrinterAttributes.KEY_TYPEFACE] = 2
        alignRight[PrinterAttributes.KEY_TEXT_SIZE] = 10
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}