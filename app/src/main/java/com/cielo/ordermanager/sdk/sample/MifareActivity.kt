package com.cielo.ordermanager.sdk.sample

import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cielo.sdk.mifare.Mifare
import cielo.sdk.mifare.MifareRequest
import cielo.sdk.mifare.MifareResponse
import com.cielo.ordermanager.sdk.R
import kotlinx.android.synthetic.main.activity_mifare.cardData
import kotlinx.android.synthetic.main.activity_mifare.insertCardView
import kotlinx.android.synthetic.main.activity_mifare.textBlock
import kotlinx.android.synthetic.main.activity_mifare.textNewValue
import kotlinx.android.synthetic.main.activity_mifare.textSector
import java.math.BigInteger

class MifareActivity : AppCompatActivity() {

    private val tag = "MifareSdk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mifare)
    }

    private fun waitForCard() {
        insertCardView.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        insertCardView.visibility = View.GONE
    }

    private fun report(message: String, display: Boolean) {
        Log.i(tag, message)
        if (display)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun processResponse(op: String, response: MifareResponse, display: Boolean = false, block: MifareResponse.() -> Unit) {
        if (response.isSuccessful) {
            report("$op successful", display)
            block(response)
        } else
            report("$op failed. Result ${response.result}, detail: ${response.detail}", true)
    }

    fun readBtnClick(view: View) {
        Log.i(tag, "Mifare clicked")
        Mifare(this).let(::MifareReadFlow).start()

    }

    fun writeBtnClick(view: View) {
        Log.i(tag, "Write clicked")
        Mifare(this).let(::MifareWriteFlow).start()
    }

    fun backupBtnClick(view: View) {
        Log.i(tag, "Backup clicked")
        Mifare(this).let(::MifareBackupFlow).start()
    }

    fun deactivateBtnClick(view: View) {
        Log.i(tag, "Deactivate clicked")
        hideProgress()
        Mifare(this).deactivate {
            processResponse("Deactivate", it, true) {}
        }
    }


    /**
     * Example of a mifare read flow
     */
    inner class MifareReadFlow(private val mifare: Mifare) {
        private val req = MifareRequest(key = MifareClassic.KEY_DEFAULT,
            keyType = 'A',
            sector = textSector.text.toByte(),
            block = textBlock.text.toByte())

        fun start() {
            Log.i(tag, "Calling detect")
            waitForCard()
            mifare.detect(::onDetect)
        }

        private fun onDetect(response: MifareResponse) {
            hideProgress()
            processResponse("Detect", response) {
                Log.i(tag, "Calling authenticate")
                mifare.authenticate(req, ::onAuthenticate)
            }
        }

        private fun onAuthenticate(response: MifareResponse) {
            processResponse("Authenticate", response) {
                Log.i(tag, "Calling read")
                mifare.readData(req, ::onRead)
            }
        }

        private fun onRead(response: MifareResponse) {
            processResponse("Read", response, true) {
                data?.format().let {
                    Log.i(tag, "Data read: $it")
                    cardData.text = it
                }
            }
        }
    }


    /**
     * Example of a mifare write flow
     */
    inner class MifareWriteFlow(private val mifare: Mifare) {
        private val req = MifareRequest(key = MifareClassic.KEY_DEFAULT,
            keyType = 'A',
            sector = textSector.text.toByte(),
            block = textBlock.text.toByte(),
            data = textNewValue.text.trim().toString().toInt().toByteArray())
        fun start() {
            Log.i(tag, "Calling detect")
            waitForCard()
            mifare.detect(::onDetect)
        }

        private fun onDetect(response: MifareResponse) {
            hideProgress()
            processResponse("Detect", response) {
                Log.i(tag, "Calling authenticate")
                mifare.authenticate(req, ::onAuthenticate)
            }
        }

        private fun onAuthenticate(response: MifareResponse) {
            processResponse("Authenticate", response) {
                Log.i(tag, "Calling write")
                mifare.writeData(req, ::onWrite)
            }
        }

        private fun onWrite(response: MifareResponse) {
            processResponse("Write", response, true) {
                req.data.format().let {
                    Log.i(tag, "Data written: $it")
                }
            }
        }
    }

    /**
     * Example of a mifare write flow
     */
    inner class MifareBackupFlow(private val mifare: Mifare) {
        private val req = MifareRequest(key = MifareClassic.KEY_DEFAULT,
            keyType = 'A',
            sector = textSector.text.toByte(),
            block = textBlock.text.toByte(),
            destinationBlock = textNewValue.text.toByte())
        fun start() {
            Log.i(tag, "Calling detect")
            waitForCard()
            mifare.detect(::onDetect)
        }

        private fun onDetect(response: MifareResponse) {
            hideProgress()
            processResponse("Detect", response) {
                Log.i(tag, "Calling authenticate")
                mifare.authenticate(req, ::onAuthenticate)
            }
        }

        private fun onAuthenticate(response: MifareResponse) {
            processResponse("Authenticate", response) {
                Log.i(tag, "Calling write")
                mifare.backup(req, ::onBackup)
            }
        }

        private fun onBackup(response: MifareResponse) {
            processResponse("Backup", response, true) {
                Log.i(tag, "Data copied from block ${req.block} to ${req.destinationBlock}")
            }
        }
    }

}

fun CharSequence.toByte() = this.trim().toString().toInt().toByte()
fun String.hexToByteArray(): ByteArray = padStart(32, '0').chunked(2).map { it.toInt(16).toByte() }.toByteArray()
fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
fun Int.toByteArray() = "%02x".format(this).hexToByteArray()
fun ByteArray.toInt() = BigInteger(this).toInt()
fun ByteArray.format() = "${toHex()} (${toInt()})"