package com.example.secureweather.captcha

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.secureweather.databinding.ActivityCaptchaChatbotBinding

class CaptchaChatbotActivity : BaseCaptchaActivity() {
    private lateinit var binding: ActivityCaptchaChatbotBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaptchaChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.buttonSolve.setOnClickListener {
            captchaManager.next(this)
        }
    }
}