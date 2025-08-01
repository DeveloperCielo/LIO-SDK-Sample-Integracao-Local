package com.cielo.ordermanager.sdk.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.view.ViewGroup
import android.widget.TextView
import com.cielo.ordermanager.sdk.sample.services.DeepLinkService
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale
import java.util.Random

const val STOP_SERVICE = "STOP_SERVICE"

fun getAmount(value: Long): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format((value / 100.0f).toDouble())
}

fun getBase64(json: String): String {
    val data = json.toByteArray(Charsets.UTF_8)
    return Base64.encodeToString(data, Base64.DEFAULT)
}

fun startForegroundServiceAndLaunchDeepLink(context: Context, deepLink: String) {
    val serviceIntent = Intent(context, DeepLinkService::class.java)
    context.startService(serviceIntent)

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun callToStopService(context: Context) {
    val stopServiceIntent = Intent(context, DeepLinkService::class.java)
    stopServiceIntent.action = STOP_SERVICE
    context.startService(stopServiceIntent)
}

fun ViewGroup.addText(text: String) {
    TextView(this.context).let {
        it.text = text
        addView(it)
    }
}

fun Intent.queryParameter(name: String) = data?.getQueryParameter(name)

fun String.decodeBase64() = Base64.decode(this, Base64.DEFAULT).let(::String)

fun <T> String.deserializeJson(type: Class<T>): T = Gson().fromJson(this, type)

fun <T> Intent.deserializeQueryParameter(name: String, type: Class<T>, block: (String, T) -> Unit) =
    queryParameter(name)
        ?.decodeBase64()
        ?.let { block(it, it.deserializeJson(type)) }

fun Intent.deserializeQueryParameter(name: String, block: (String) -> Unit) =
    queryParameter(name)
        ?.decodeBase64()
        ?.let { block(it) }


fun saveImage(context: Context, finalBitmap: Bitmap): String {
    val root = context.getExternalFilesDir(null).toString()
    val myDir = File("$root/saved_images")
    if (!myDir.exists()) {
        myDir.mkdirs()
    }
    val generator = Random()
    var n = 10000
    n = generator.nextInt(n)
    val fname = "Image-$n.jpg"
    val file = File(myDir, fname)
    val path = file.absolutePath
    if (file.exists())
        file.delete()
    try {
        val out = FileOutputStream(file)
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return path
}