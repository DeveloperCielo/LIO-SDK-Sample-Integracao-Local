package com.cielo.ordermanager.sdk.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.R
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import kotlinx.android.synthetic.main.activity_qr_code.barcodeView
import kotlinx.android.synthetic.main.activity_qr_code.resultText

class QrCodeActivity : AppCompatActivity() {

    private val capture: CaptureManager? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        barcodeView.decodeContinuous(callback)
    }

    public override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    public override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null) {
                return
            }
            resultText.text = result.text
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }
}
