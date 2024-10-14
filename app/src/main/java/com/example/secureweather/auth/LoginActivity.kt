package com.example.secureweather.auth

import CaptchaManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.secureweather.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authService: AuthService = AuthService.getInstance(this)
    private lateinit var captchaManager: CaptchaManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        captchaManager = CaptchaManager.getInstance()

        binding.buttonNextForgot.setOnClickListener {
            // navigate to registration activity
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        binding.buttonNext.setOnClickListener {
            captchaManager.next(this)
        }
    }
}