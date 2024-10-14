package com.example.secureweather.captcha

import CaptchaManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


open class BaseCaptchaActivity : AppCompatActivity() {
    lateinit var captchaManager: CaptchaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captchaManager = CaptchaManager.getInstance()
    }

}