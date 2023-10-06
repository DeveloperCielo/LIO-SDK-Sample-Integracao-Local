package com.cielo.ordermanager.sdk.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cielo.orders.domain.PrinterAttributes
import cielo.sdk.order.PrinterListener
import cielo.sdk.printer.PrinterManager
import com.cielo.ordermanager.sdk.R
import com.cielo.ordermanager.sdk.TAG
import kotlinx.android.synthetic.main.activity_print_sample.print_barcode
import kotlinx.android.synthetic.main.activity_print_sample.print_image
import kotlinx.android.synthetic.main.activity_print_sample.print_multi_columns
import kotlinx.android.synthetic.main.activity_print_sample.print_qrcode
import kotlinx.android.synthetic.main.activity_print_sample.print_simple_text
import java.util.ArrayList
import java.util.HashMap

class PrintSampleActivity : AppCompatActivity() {
    private var printerManager: PrinterManager? = null
    private var printerListener: PrinterListener? = null
    private val alignCenter = HashMap<String, Int>()
    private val alignLeft = HashMap<String, Int>()
    private val alignRight = HashMap<String, Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_sample)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = "Teste de Impressão"
        }

        setStyles()
        printerManager = PrinterManager(this)
        printerListener = object : PrinterListener {
            override fun onWithoutPaper() {
                Log.d(TAG, "printer without paper")
            }

            override fun onPrintSuccess() {
                Log.d(TAG, "print success!")
            }

            override fun onError(throwable: Throwable?) {
                Log.d(TAG, String.format("printer error -> %s", throwable?.message))
            }
        }
    }

    fun setupClickListeners() {
        print_image.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cielo)
            printerManager!!.printImage(bitmap, alignCenter, printerListener!!)
        }
        print_qrcode.setOnClickListener {
            printerManager!!.printQrCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, printerListener!!)
        }
        print_barcode.setOnClickListener {
            printerManager!!.printBarCode("1234567890098765432112345678900987654321", PrinterAttributes.VAL_ALIGN_CENTER, 500, 200, false, printerListener!!)

        }
        print_multi_columns.setOnClickListener {
            val textsToPrint = arrayOf("Texto alinhado à esquerda.\n\n\n",
                    "Texto centralizado\n\n\n", "Texto alinhado à direita\n\n\n")
            val styles: MutableList<Map<String, Int>> = ArrayList()
            styles.add(alignLeft)
            styles.add(alignCenter)
            styles.add(alignRight)
            printerManager!!.printMultipleColumnText(textsToPrint, styles, printerListener!!)
        }
        print_simple_text.setOnClickListener {
            val textToPrint = "TEXTO PARA IMPRIMIR"
            printerManager!!.printText(textToPrint, alignCenter, printerListener!!)
        }
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
        return when (item.itemId) {
            else -> {
                finish()
                super.onOptionsItemSelected(item)
            }
        }
    }
}
