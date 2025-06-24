package com.cielo.ordermanager.sdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cielo.ordermanager.sdk.databinding.ActivityMainBinding
import com.cielo.ordermanager.sdk.sample.activities.DeepLinkIntegrationActivity
import com.cielo.ordermanager.sdk.sample.activities.LocalIntegrationActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sdkIntegrationButton.setOnClickListener {
            startActivity(Intent(this, LocalIntegrationActivity::class.java))
        }

        binding.deepLinkIntegrationButton.setOnClickListener {
            startActivity(Intent(this, DeepLinkIntegrationActivity::class.java))
        }
    }
}