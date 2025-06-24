package com.cielo.ordermanager.sdk.sample.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.databinding.ActivityResponseBinding
import com.cielo.ordermanager.sdk.utils.Order
import com.cielo.ordermanager.sdk.utils.addText
import com.cielo.ordermanager.sdk.utils.deserializeQueryParameter
import timber.log.Timber

class ResponseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResponseBinding

    private val logView get() = binding.logCancelLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.deserializeQueryParameter("response", Order::class.java) { json, _ ->
            Timber.tag("ResponseActivity").d("Response: $json")
            logView.addText("JSON=$json")
        }
    }
}