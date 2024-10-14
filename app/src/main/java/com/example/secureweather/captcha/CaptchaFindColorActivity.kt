package com.example.secureweather.captcha

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.secureweather.databinding.ActivityCaptchaFindColorBinding

class CaptchaFindColorActivity : BaseCaptchaActivity() {
    private lateinit var binding: ActivityCaptchaFindColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaptchaFindColorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.buttonSolve.setOnClickListener {
            captchaManager.next(this)
        }
    }
}