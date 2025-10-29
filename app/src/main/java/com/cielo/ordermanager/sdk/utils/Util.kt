package com.cielo.ordermanager.sdk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import com.cielo.ordermanager.sdk.sample.services.DeepLinkService
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

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


fun saveImage(context: Context, bitmap: Bitmap): Uri {
    val storageDir = context.getExternalFilesDir(null) ?: Environment.getExternalStorageDirectory()
    return createImageFile(storageDir, bitmap) { Uri.fromFile(it) }.getOrDefault(Uri.EMPTY)
}

fun saveImageWithProvider(context: Context, bitmap: Bitmap): Uri {
    val storageDir = context.filesDir
    return createImageFile(storageDir, bitmap) { createFileProviderUri(context, it) }.getOrDefault(Uri.EMPTY)
}

fun createImageFile(storageDir: File, bitmap: Bitmap, toUri: (File) -> Uri) : Result<Uri> {
    return runCatching {
        val imageDirectory = File(storageDir, "saved_images").apply {
            if (!exists()) mkdirs()
        }
        val randomSuffix = (1000..9999).random()
        val fileName = "Image-$$randomSuffix.jpg"
        val fileImage = File(imageDirectory, fileName).apply {
            if (exists()) delete()
        }
        FileOutputStream(fileImage).use { outputStream ->
            val compressionSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            if (!compressionSuccess) {
                throw IOException("Failed to compress bitmap")
            }
        }
        toUri(fileImage)
    }.onFailure {
        it.printStackTrace()
    }
}

private fun createFileProviderUri(context: Context, file: File): Uri {
    return try {
        val authority = "${context.packageName}.fileprovider"
        FileProvider.getUriForFile(context, authority, file).also { uri ->
            grantUriPermission(context, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Uri.EMPTY
    }
}

private fun grantUriPermission(context: Context, targetUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("lio://print"))
    context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        .mapNotNull { it.activityInfo?.packageName }
        .distinct()
        .forEach { packageName ->
            runCatching {
                context.grantUriPermission(
                    packageName,
                    targetUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }.onFailure {
                it.printStackTrace()
            }
        }
}