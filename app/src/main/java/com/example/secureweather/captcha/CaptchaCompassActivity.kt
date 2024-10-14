package com.example.secureweather.captcha

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.secureweather.databinding.ActivityCaptchaCompassBinding

class CaptchaCompassActivity : BaseCaptchaActivity() {
    private lateinit var binding: ActivityCaptchaCompassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaptchaCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.buttonSolve.setOnClickListener {
            captchaManager.next(this)
        }
    }
}